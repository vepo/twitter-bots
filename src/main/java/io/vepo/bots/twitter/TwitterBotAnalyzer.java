package io.vepo.bots.twitter;

import io.vepo.twitter4j.Rule;
import io.vepo.twitter4j.TwitterClient;
import io.vepo.twitter4j.Rule.Language;
import io.vepo.twitter4j.Rule.Matching;

public class TwitterBotAnalyzer {
    public static void main(String[] args) throws Exception {
        new TwitterBotAnalyzer().run();

    }

    public void run() throws Exception {
        TwitterClient tClient = new TwitterClient(System.getenv("API_KEY"),
                                                  System.getenv("API_SECRET_KEY"));
        tClient.authenticate()
               .stream()
               .with(Rule.builder()
                         .withLanguage(Language.Portuguese)
                         .withToken("carnaval")
                         .without(Matching.builder()
                                          .withImages()
                                          .or()
                                          .isRetweet()
                                          .build())
                         .applyTag("Bolsonaro Tweets"))
               .consume(System.out::println);
        System.out.println("Finished!");
    }

}
