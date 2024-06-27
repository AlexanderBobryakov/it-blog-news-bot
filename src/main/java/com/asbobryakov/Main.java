package com.asbobryakov;

import com.asbobryakov.dto.ArticleTag;
import com.asbobryakov.service.parser.impl.ApacheBlogParser;
import com.asbobryakov.service.parser.BlogParser;
import com.asbobryakov.service.parser.impl.FlinkBlogParser;
import com.asbobryakov.service.parser.impl.KafkaBlogParser;
import com.asbobryakov.service.parser.impl.MicroservicesIoBlogParser;
import com.asbobryakov.service.parser.impl.SpringBlogParser;
import com.asbobryakov.service.parser.impl.TestContainersBlogParser;
import com.asbobryakov.service.parser.impl.ThorbenJanssenBlogParser;
import com.asbobryakov.service.parser.impl.VladMihalceaBlogParser;
import com.asbobryakov.telegram.ItNewsBot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.asbobryakov.utils.Formatting.formatArticleLink;

public class Main {
    public static void main(String[] args) throws Exception {
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
        while (true) {
            blogParsers.forEach(parser -> {
                processBlogParser(parser, lastArticlesByTags, itNewsBot);
            });
            itNewsBot.updatePinnedMessageBy(lastArticlesByTags);

            Thread.sleep(30 * 60 * 1000);  // 30мин
        }
    }

    private static void processBlogParser(BlogParser blogParser,
                                          Map<ArticleTag, String> lastArticlesByTags,
                                          ItNewsBot itNewsBot) {
        final var articles = blogParser.parseLastArticles();
        // определяем какие нужно опубликовать (те, которые 'позже' лежащей в lastArticlesByTags)
        final var lastPublishedArticleLink = lastArticlesByTags.getOrDefault(blogParser.getArticleTag(), "");
        final var lastPublishedArticleOpt = articles.stream()
            .filter(article -> lastPublishedArticleLink.equals(formatArticleLink(article)) || lastPublishedArticleLink.equals(article.title()))
            .findFirst();
        if (lastPublishedArticleOpt.isEmpty()) {
            // все - новые, публикуем их
            articles.forEach(itNewsBot::publishArticle);
        } else {
            // новые - только после найденного (который публиковали раньше)
            final var lastPublishedArticle = lastPublishedArticleOpt.get();
            final var lastPublishedArticleIndex = articles.indexOf(lastPublishedArticle);
            articles.stream().skip(lastPublishedArticleIndex + 1).forEach(itNewsBot::publishArticle);
        }
        // обновляем мапу последних сообщений
        final var newestArticle = articles.getLast();
        lastArticlesByTags.put(blogParser.getArticleTag(), formatArticleLink(newestArticle));
    }
}