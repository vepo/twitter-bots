package io.vepo.bots.twitter;

import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.vepo.twitter4j.Rule;
import io.vepo.twitter4j.Rule.Language;
import io.vepo.twitter4j.TwitterClient;
import io.vepo.twitter4j.TwitterClient.Expansions;
import io.vepo.twitter4j.TwitterClient.MediaFields;
import io.vepo.twitter4j.TwitterClient.PlaceFields;
import io.vepo.twitter4j.TwitterClient.PollFields;
import io.vepo.twitter4j.TwitterClient.TweetFields;
import io.vepo.twitter4j.TwitterClient.UserFields;

public class TwitterBotAnalyzer {
    public static void main(String[] args) throws Exception {
        TwitterClient tClient = new TwitterClient(System.getenv("API_KEY"),
                                                  System.getenv("API_SECRET_KEY"));

        var previousWords = new HashMap<String, Integer>();
        var nextsWords = new HashMap<String, Integer>();
        tClient.authenticate()
               .stream()
               .withRule(Rule.builder()
                             .withLanguage(Language.Portuguese)
//                         .withToken("java")
                             .withToken("bolsonaro")
                             .withLinks()
                             .applyTag("Bolsonaro"))
               .requestExpansions(Stream.of(Expansions.values()).collect(toSet()))
               .requestMediaFields(Stream.of(MediaFields.values()).collect(toSet()))
               .requestPlaceFields(Stream.of(PlaceFields.values()).collect(toSet()))
               .requestPollFields(Stream.of(PollFields.values()).collect(toSet()))
               .requestTweetFields(Stream.of(TweetFields.values()).collect(toSet()))
               .requestUserFields(Stream.of(UserFields.values()).collect(toSet()))
               .consume(tweet -> {
                   System.out.println(tweet);
                   var message = tweet.getData().getText().split("\\s+");
                   for (int i = 0; i < message.length; ++i) {
                       if (message[i].compareToIgnoreCase("java") == 0) {
                           if (i > 0 && message[i - 1].matches("[A-Za-z]{3,}?")) {
                               previousWords.compute(message[i - 1].toLowerCase(),
                                                     (word, counter) -> counter == null ? 1 : counter + 1);
                           }
                           if (i < message.length && message[i + 1].matches("[A-Za-z]{3,}?")) {
                               nextsWords.compute(message[i + 1].toLowerCase(),
                                                  (word, counter) -> counter == null ? 1 : counter + 1);
                           }
                       }
                   }
                   if (previousWords.size() > 0 && nextsWords.size() > 0) {
                       System.out.println("------------------------");
                       if (previousWords.size() > 0) {
                           System.out.println("Anteriores: ");
                           previousWords.entrySet()
                                        .stream()
                                        .sorted(TwitterBotAnalyzer::compare)
                                        .limit(5)
                                        .forEachOrdered(entry -> System.out.println(entry.getKey() + ":"
                                                + entry.getValue()));
                       }
                       if (previousWords.size() > 0 && nextsWords.size() > 0) {
                           System.out.println("++++++++++++++++++++++++");
                       }
                       if (nextsWords.size() > 0) {
                           System.out.println("Posteriores: ");
                           nextsWords.entrySet()
                                     .stream()
                                     .sorted(TwitterBotAnalyzer::compare)
                                     .limit(5)
                                     .forEachOrdered(entry -> System.out.println(entry.getKey() + ":"
                                             + entry.getValue()));
                       }
                       System.out.println("------------------------");
                   }
               })
               .join();

    }

    private static int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        return o2.getValue() - o1.getValue();
    }

}
