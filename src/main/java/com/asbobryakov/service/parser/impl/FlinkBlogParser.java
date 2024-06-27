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

import static com.asbobryakov.dto.ArticleTag.FLINK;
import static java.util.Collections.reverse;

@Slf4j
public class FlinkBlogParser implements BlogParser {
    private static final String BASE_LINK = "https://flink.apache.org";
    private static final String BLOG_LINK = BASE_LINK + "/posts/";
    private static final String PAGE_BLOG_LINK = BASE_LINK + "/posts/page/%s/";
    private static final int MAX_PAGES = 0;

    @Override
    public ArticleTag getArticleTag() {
        return FLINK;
    }

    @Override
    public List<Article> parseLastArticles() {
        final var result = new ArrayList<Article>();
        // first page
        result.addAll(parseArticlesOnPage(BLOG_LINK));
        // other pages
        for (int pageNumber = 2; pageNumber <= MAX_PAGES; pageNumber++) {
            try {
                result.addAll(parseArticlesOnPage(PAGE_BLOG_LINK.formatted(pageNumber)));
            } catch (Exception e) {
                log.error("Error while parsing articles for page {}", pageNumber, e);
            }
        }
        // очередность: от старого к свежему
        reverse(result);
        return result.stream().unordered().toList();
    }

    private List<Article> parseArticlesOnPage(String pageUrl) {
        final var result = new ArrayList<Article>();
        try {
            final Document doc = Jsoup.connect(pageUrl).get();
            final var posts = doc.select("article.markdown.book-post");
            for (Element post : posts) {
                final var titleElement = post.selectFirst("h3 > a");
                final var title = titleElement.text();
                final var link = BASE_LINK + titleElement.attr("href");
                final var date = post.ownText().split(" - ")[0];
                final var description = post.selectFirst("p").text();
                result.add(new Article(link, title, description, date, getArticleTag()));
            }
        } catch (IOException e) {
            log.error("Error while parsing articles on page url {}", pageUrl, e);
        }
        return result;
    }
}