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
            final var posts = doc.select("div.post-preview-container");
            for (final Element post : posts) {
                final var titleElement = post.selectFirst("a[data-testid=post-preview-title]");
                final var title = titleElement.text();
                final var link = titleElement.attr("href");

                final var description = post.select("div.post-preview-container").select("a").get(1).text();

                final var dateElement = post.selectFirst("time._date_qpf1t_1");
                final var date = dateElement.text();

                result.add(new Article(link, title, description, date, getArticleTag()));
            }
        } catch (Exception e) {
            log.error("Error while parsing articles on page url {}", pageUrl, e);
            throw new ParserFailedException(e);
        }
        return result;
    }
}
