package com.asbobryakov.bot.blognews;

import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.BlogParser;
import com.asbobryakov.bot.blognews.parser.exception.ParserFailedException;
import com.asbobryakov.bot.blognews.parser.impl.AlgomasterBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.ApacheBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.DecodableBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.FingerprintBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.FlinkBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.KafkaBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.ScrapflyBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.VladMihalceaBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.WebkitBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.rss.AkamaiBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.rss.ConfluentBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.rss.DanVegaBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.rss.MicroservicesIoBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.rss.QuastorBlogParser;
import com.asbobryakov.bot.blognews.parser.impl.rss.SpringBlogParser;
import com.asbobryakov.bot.blognews.telegram.ItNewsBot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleStatus.DISABLED;
import static com.asbobryakov.bot.blognews.dto.ArticleStatus.EXCEPTION;
import static com.asbobryakov.bot.blognews.utils.Formatting.formatArticleLink;

@Slf4j
public class Main {

    public static void main(String[] args) throws Exception {
        log.info("Starting application");
        final var itNewsBot = new ItNewsBot(new DefaultBotOptions());
        final var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(itNewsBot);

        // all blogParsers
        final var blogParsers = List.<BlogParser>of(
            new KafkaBlogParser(),
            new FlinkBlogParser(),
            new SpringBlogParser(),
            new ApacheBlogParser(),
            new VladMihalceaBlogParser(),
            new MicroservicesIoBlogParser(),
            new DecodableBlogParser(),
            new QuastorBlogParser(),
            new ConfluentBlogParser(),
            new AlgomasterBlogParser(),
            new DanVegaBlogParser(),
            new WebkitBlogParser(),
            new FingerprintBlogParser(),
            new ScrapflyBlogParser(),
            new AkamaiBlogParser()
        );

        final var lastArticlesByTags = new LinkedHashMap<>(itNewsBot.restoreLastArticlesFromPinnedMessage());
        while (true) {
            for (BlogParser parser : blogParsers) {
                if (!parser.isEnabled()) {
                    lastArticlesByTags.put(parser.getArticleTag(), DISABLED.getValue());
                    continue;
                }
                try {
                    processBlogParser(parser, lastArticlesByTags, itNewsBot);
                } catch (Exception e) {
                    log.error("Error while processing {}, lastArticlesByTags={}", parser.getArticleTag(), lastArticlesByTags, e);
                    lastArticlesByTags.put(parser.getArticleTag(), EXCEPTION.getValue());
                }
            }
            itNewsBot.updatePinnedMessageBy(lastArticlesByTags);
        }
    }

    private static void processBlogParser(BlogParser blogParser,
                                          Map<ArticleTag, String> lastArticlesByTags,
                                          ItNewsBot itNewsBot) throws ParserFailedException {
        final var articles = blogParser.parseLastArticles();
        // determine which ones need to be published (those that are 'later' lying in lastArticlesByTags)
        final var lastPublishedArticleLink = lastArticlesByTags.getOrDefault(blogParser.getArticleTag(), "");
        final var lastPublishedArticleOpt = articles.stream()
            .filter(article -> {
                return lastPublishedArticleLink.equals(formatArticleLink(article))
                    || lastPublishedArticleLink.equals(article.title());
            })
            .findFirst();
        if (lastPublishedArticleOpt.isEmpty()) {
            log.warn("All are 'new'. Blog = {}, lastArticlesByTags={}, articles={}", blogParser.getArticleTag(), lastArticlesByTags, articles);
            // send only last (not exceptioned)
            if (!articles.isEmpty() && !lastPublishedArticleLink.equals(EXCEPTION.getValue())) {
                final var lastArticle = getLast(articles);
                itNewsBot.publishArticle(lastArticle);
            }
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
        if (!articles.isEmpty()) {
            final var newestArticle = getLast(articles);
            lastArticlesByTags.put(blogParser.getArticleTag(), formatArticleLink(newestArticle));
        } else {
            lastArticlesByTags.put(blogParser.getArticleTag(), "_empty_");
        }
    }

    public static <T> T getLast(List<T> list) {
        if (list.isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }
        return list.get(list.size() - 1);
    }
}