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

import static com.asbobryakov.bot.blognews.dto.ArticleTag.SCRAPFLY;
import static java.util.Collections.reverse;

@Slf4j
public class ScrapflyBlogParser implements BlogParser {
    private static final String BASE_LINK = "https://scrapfly.io";
    private static final String TAG_BLOG_LINK = BASE_LINK + "/blog/tag/blocking/";

    @Override
    public ArticleTag getArticleTag() {
        return SCRAPFLY;
    }

    @Override
    public List<Article> parseLastArticles() throws ParserFailedException {
        final var result = new ArrayList<Article>();
        result.addAll(parseArticlesOnPage(TAG_BLOG_LINK));

        // очередность: от старого к свежему
        reverse(result);
        return result;
    }

    private List<Article> parseArticlesOnPage(String pageUrl) throws ParserFailedException {
        final var result = new ArrayList<Article>();
        try {
            final Document doc = Jsoup.connect(pageUrl).get();
            final var postCards = doc.select(".post-card.js-post-card");
            for (Element card : postCards) {
                final var singlePostCard = card.selectFirst(".single-post-card");
                if (singlePostCard == null) continue;
                final var link = BASE_LINK + singlePostCard.selectFirst("a.post-card-link").attr("href");
                final var title = singlePostCard.selectFirst("h5.post-title a").text();
                final var date = singlePostCard.selectFirst("time.card-meta-date").text().trim();
                final var description = singlePostCard.selectFirst("p.excerpt-text").text();
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
