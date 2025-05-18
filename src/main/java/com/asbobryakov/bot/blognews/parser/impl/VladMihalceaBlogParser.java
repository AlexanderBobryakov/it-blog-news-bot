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

import static com.asbobryakov.bot.blognews.dto.ArticleTag.VLAD_MIHALCEA;
import static java.util.Collections.reverse;

@Slf4j
public class VladMihalceaBlogParser implements BlogParser {
    private static final String BLOG_LINK = "https://vladmihalcea.com/blog/";

    @Override
    public ArticleTag getArticleTag() {
        return VLAD_MIHALCEA;
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
        } catch (Exception e) {
            log.error("Error while parsing articles on page url {}", pageUrl, e);
            throw new ParserFailedException(e);
        }
        return result;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
