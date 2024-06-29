package com.asbobryakov.bot.blognews;

import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.BlogParser;
import com.asbobryakov.bot.blognews.parser.impl.ApacheBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.FlinkBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.KafkaBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.MicroservicesIoBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.SpringBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.TestContainersBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.ThorbenJanssenBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.VladMihalceaBlogParser;
import com.asbobryakov.bot.blognews.telegram.ItNewsBot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.utils.Formatting.formatArticleLink;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        log.info("Starting application");
        final var itNewsBot = new ItNewsBot(new DefaultBotOptions());
        final var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(itNewsBot);

        // all blogParsers
        final var blogParsers = List.of(
            new KafkaBlogParser(),
            new FlinkBlogParser(),
            new SpringBlogParser(),
            new ApacheBlogParser(),
            new VladMihalceaBlogParser(),
            new TestContainersBlogParser(),
            new MicroservicesIoBlogParser(),
            new ThorbenJanssenBlogParser()
        );

        final var lastArticlesByTags = new ConcurrentHashMap<>(itNewsBot.restoreLastArticlesFromPinnedMessage());
        @Cleanup final var executorService = Executors.newFixedThreadPool(blogParsers.size());
        while (true) {
            log.info("Start parsers");
            final var futures = new HashSet<CompletableFuture<?>>();
            for (BlogParser parser : blogParsers) {
                futures.add(CompletableFuture.runAsync(() -> processBlogParser(parser, lastArticlesByTags, itNewsBot), executorService));
            }
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
            itNewsBot.updatePinnedMessageBy(lastArticlesByTags);

            sleep();
        }
    }

    private static void processBlogParser(BlogParser blogParser,
                                          Map<ArticleTag, String> lastArticlesByTags,
                                          ItNewsBot itNewsBot) {
        final var articles = blogParser.parseLastArticles();
        // determine which ones need to be published (those that are 'later' lying in lastArticlesByTags)
        final var lastPublishedArticleLink = lastArticlesByTags.getOrDefault(blogParser.getArticleTag(), "");
        final var lastPublishedArticleOpt = articles.stream()
            .filter(article -> lastPublishedArticleLink.equals(formatArticleLink(article)) || lastPublishedArticleLink.equals(article.title()))
            .findFirst();
        if (lastPublishedArticleOpt.isEmpty()) {
            // all are “new”, we publish them
            articles.forEach(article -> {
                itNewsBot.publishArticle(article);
                Thread.yield();
            });
        } else {
            // “new” are only those that are after the one found (which was published earlier)
            final var lastPublishedArticle = lastPublishedArticleOpt.get();
            final var lastPublishedArticleIndex = articles.indexOf(lastPublishedArticle);
            articles.stream().skip(lastPublishedArticleIndex + 1).forEach(article -> {
                itNewsBot.publishArticle(article);
                Thread.yield();
            });
        }
        // updating the map of recent articles
        final var newestArticle = articles.getLast();
        lastArticlesByTags.put(blogParser.getArticleTag(), formatArticleLink(newestArticle));
    }

    private static void sleep() {
        log.info("Sleep...");
        // wait 10 minutes (can't use `Thread.sleep()` because cloud providers stop running application)
        final var from = Instant.now();
        while (true) {
            if (Duration.between(from, Instant.now()).toMinutes() >= 10) {
                break;
            }
        }
        System.out.println();
    }
}