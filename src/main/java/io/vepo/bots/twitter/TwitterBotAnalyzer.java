package io.vepo.bots.twitter;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import io.vepo.twitter4j.Rule;
import io.vepo.twitter4j.Rule.Language;
import io.vepo.twitter4j.TwitterClient;

public class TwitterBotAnalyzer {
    public static void main(String[] args) throws Exception {
        TwitterClient tClient = new TwitterClient(System.getenv("API_KEY"),
                                                  System.getenv("API_SECRET_KEY"));

        var previousWords = new HashMap<String, Integer>();
        var nextsWords = new HashMap<String, Integer>();
        tClient.authenticate()
               .stream()
               .with(Rule.builder()
                         .withLanguage(Language.Portuguese)
                         .withToken("bolsonaro")
                         .applyTag("Bolsonaro Tweets"))
               .consume(tweet -> {
                   System.out.println(tweet);
                   var message = tweet.getData().getText().split("\\s+");
                   for (int i = 0; i < message.length; ++i) {
                       if (message[i].compareToIgnoreCase("bolsonaro") == 0) {
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
