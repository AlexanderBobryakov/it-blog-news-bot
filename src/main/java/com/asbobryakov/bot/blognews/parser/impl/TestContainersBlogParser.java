package com.asbobryakov.bot.blognews.parser.impl;

import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.BlogParser;
import com.asbobryakov.bot.blognews.parser.exception.ParserFailedException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.TEST_CONTAINERS;
import static java.util.Collections.reverse;

@Slf4j
public class TestContainersBlogParser implements BlogParser {
    private static final String BASE_LINK = "https://www.atomicjar.com";
    private static final String BLOG_LINK = BASE_LINK + "/category/testcontainers/";

    @Override
    public ArticleTag getArticleTag() {
        return TEST_CONTAINERS;
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
            final var posts = doc.select("article.masonry-blog-item");
            for (final Element post : posts) {
                final var linkElement = post.selectFirst("a.entire-meta-link");
                final var link = BASE_LINK + linkElement.attr("href");
                final var title = linkElement.attr("aria-label");
                final var description = post.selectFirst("div.excerpt").text();
                final var date = post.selectFirst("div.grav-wrap span").text();
                result.add(new Article(link, title, description, date, getArticleTag()));
            }
        } catch (IOException e) {
            log.error("Error while parsing articles on page url {}", pageUrl, e);
            throw new ParserFailedException(e);
        }
        return result;
    }
}
