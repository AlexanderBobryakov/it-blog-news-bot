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

import static com.asbobryakov.dto.ArticleTag.VLAD_MIHALCEA;
import static java.util.Collections.reverse;

@Slf4j
public class VladMihalceaBlogParser implements BlogParser {
    private static final String BLOG_LINK = "https://vladmihalcea.com/blog/";

    @Override
    public ArticleTag getArticleTag() {
        return VLAD_MIHALCEA;
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
            final var posts = doc.select("div.blog-holder");
            for (final Element post : posts) {
                final var linkElement = post.selectFirst("h2.headline a");
                final var link = linkElement.attr("href");
                final var title = linkElement.attr("title");
                final var description = post.selectFirst("div.article > p").text();
                final var date = post.selectFirst("span.post-date-entry").text()
                    .replace("Posted on ", "");
                result.add(new Article(link, title, description, date, getArticleTag()));
            }
        } catch (IOException e) {
            log.error("Error while parsing articles on page url {}", pageUrl, e);
        }
        return result;
    }
}
