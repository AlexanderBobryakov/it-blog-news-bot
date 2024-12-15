package com.asbobryakov.bot.blognews.parser.impl;

import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.BlogParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.DECODABLE;
import static java.util.Collections.reverse;

@Slf4j
public class DecodableBlogParser implements BlogParser {
    private static final String BASE_LINK = "https://www.decodable.co";
    private static final String BLOG_LINK = BASE_LINK + "/blog";

    @Override
    public ArticleTag getArticleTag() {
        return DECODABLE;
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
            final var posts = doc.select(".blog-post-related_content");
            for (Element post : posts) {
                final var link = BASE_LINK + post.parent().attr("href");
                final var title = post.select("h3.heading-style-h5").text();
                final var description = post.select(".margin-bottom.margin-small .text-size-small").text();
                final var date = post.select(".blog-grid_meta-wrapper div").first().text();
                result.add(new Article(link, title, description, date, getArticleTag()));
            }
        } catch (Exception e) {
            log.error("Error while parsing articles on page url {}", pageUrl, e);
        }
        return result;
    }
}
