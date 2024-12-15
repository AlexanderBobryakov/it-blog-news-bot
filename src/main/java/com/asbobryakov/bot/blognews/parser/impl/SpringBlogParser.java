package com.asbobryakov.bot.blognews.parser.impl;

import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.BlogParser;
import com.asbobryakov.bot.blognews.utils.RssParser;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.SPRING;
import static java.util.Collections.reverse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class SpringBlogParser implements BlogParser {
    private static final String RSS_LINK = "https://spring.io/blog.atom";

    @Override
    public ArticleTag getArticleTag() {
        return SPRING;
    }

    @Override
    public List<Article> parseLastArticles() {
        final var result = new ArrayList<Article>();
        try {
            RssParser.parse(new URL(RSS_LINK), getArticleTag()).stream()
                .filter(a -> isNotBlank(a.description()))
                .forEach(result::add);
        } catch (Exception e) {
            log.error("Error while parsing articles on page url {}", RSS_LINK, e);
        }

        // очередность: от старого к свежему
        reverse(result);
        return result;
    }
}
