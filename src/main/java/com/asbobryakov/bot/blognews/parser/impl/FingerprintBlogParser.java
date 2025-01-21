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

import static com.asbobryakov.bot.blognews.dto.ArticleTag.FINGERPRINT;
import static java.util.Collections.reverse;

@Slf4j
public class FingerprintBlogParser implements BlogParser {
    private static final String BASE_LINK = "https://fingerprint.com/";
    private static final String BLOG_LINK = BASE_LINK + "/blog";

    @Override
    public ArticleTag getArticleTag() {
        return FINGERPRINT;
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
            final var gridContainer = doc.selectFirst("div[class^=Grid-module--grid]");
            if (gridContainer != null) {
                final var posts = gridContainer.select("div[class^=Post-module--post]");
                for (final Element post : posts) {
                    final var title = post.select("h1[class^=Post-module--title]").text();
                    final var link = BASE_LINK + post.select("a").attr("href");
                    final var description = post.select("p[class^=Post-module--description]").text();
                    final var date = post.select("span[class^=Post-module--publishDate]").text();
                    result.add(new Article(link, title, description, date, getArticleTag()));
                }
            } else {
                throw new RuntimeException("Grid div is null");
            }
        } catch (Exception e) {
            log.error("Error while parsing articles on page url {}", pageUrl, e);
            throw new ParserFailedException(e);
        }
        return result;
    }
}
