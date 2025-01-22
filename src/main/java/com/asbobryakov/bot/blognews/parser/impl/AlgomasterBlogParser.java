package com.asbobryakov.bot.blognews.parser.impl;

import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.BlogParser;
import com.asbobryakov.bot.blognews.parser.exception.ParserFailedException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.ALGOMASTER;
import static java.util.Collections.reverse;

@Slf4j
public class AlgomasterBlogParser implements BlogParser {
    private static final String BLOG_LINK = "https://blog.algomaster.io/t/system-design";

    @Override
    public ArticleTag getArticleTag() {
        return ALGOMASTER;
    }

    @Override
    public List<Article> parseLastArticles() throws ParserFailedException {
        final var result = new ArrayList<Article>();
        result.addAll(parseArticlesOnPage(BLOG_LINK));

        // очередность: от старого к свежему
        reverse(result);
        return result;
    }

    private List<Article> parseArticlesOnPage(String pageUrl) throws ParserFailedException {
        final var result = new ArrayList<Article>();
        try {
            final Document doc = Jsoup.connect(pageUrl).get();
            final var gridContainer = doc.selectFirst("div[class=portable-archive-list]");
            final var posts = gridContainer.select("div._container_6i6j0_1");
            for (final Element post : posts) {
                final var title = post.selectFirst("a[data-testid=post-preview-title]").text();
                final var description = post.selectFirst("div:nth-of-type(2) a").text();
                final var link = post.selectFirst("a[data-testid=post-preview-title]").attr("href");
                final var date = post.selectFirst("time").attr("datetime");

                result.add(new Article(link, title, description, date, getArticleTag()));
            }
        } catch (Exception e) {
            log.error("Error while parsing articles on page url {}", pageUrl, e);
            throw new ParserFailedException(e);
        }
        return result;
    }
}
