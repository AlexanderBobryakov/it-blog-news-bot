package com.asbobryakov.service.parser.impl;

import com.asbobryakov.dto.Article;
import com.asbobryakov.dto.ArticleTag;
import com.asbobryakov.service.parser.BlogParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.dto.ArticleTag.KAFKA;
import static java.util.Collections.reverse;

@Slf4j
public class KafkaBlogParser implements BlogParser {
    private static final String BLOG_LINK = "https://kafka.apache.org/blog";

    @Override
    public ArticleTag getArticleTag() {
        return KAFKA;
    }

    @Override
    public List<Article> parseLastArticles() {
        final var result = new ArrayList<Article>();
        result.addAll(parseArticlesOnPage(BLOG_LINK));

        // очередность: от старого к свежему
        reverse(result);
        return result.stream().unordered().toList();
    }

    private List<Article> parseArticlesOnPage(String pageUrl) {
        final var result = new ArrayList<Article>();
        try {
            final Document doc = Jsoup.connect(pageUrl).get();
            final var posts = doc.select("article");
            for (Element post : posts) {
                final var linkElement = post.selectFirst("h2.bullet a[href]");
                final var link = BLOG_LINK + linkElement.attr("href");
                final var title = linkElement.text();
                final var dateAndAuthor = post.select("h2.bullet").first().nextSibling().toString().trim();;
                final var date = dateAndAuthor.split(" - ")[0];
                final var paragraphs = post.select("p");
                final var description = paragraphs.get(0).text();
                result.add(new Article(link, title, description, date, getArticleTag()));
            }
        } catch (IOException e) {
            log.error("Error while parsing articles on page url {}", pageUrl, e);
        }
        return result;
    }
}
