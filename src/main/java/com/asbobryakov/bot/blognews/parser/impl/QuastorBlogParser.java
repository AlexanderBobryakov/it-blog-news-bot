package com.asbobryakov.bot.blognews.parser.impl;

import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.BlogParser;
import com.asbobryakov.bot.blognews.utils.RssParser;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.QUASTOR;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public class QuastorBlogParser implements BlogParser {
    private static final String RSS_LINK = "https://rss.beehiiv.com/feeds/nczRb4PQ6t.xml";

    @Override
    public ArticleTag getArticleTag() {
        return QUASTOR;
    }

    @Override
    public List<Article> parseLastArticles() {
        final var result = new ArrayList<Article>();
        try {
            result.addAll(
                RssParser.parse(new URL(RSS_LINK), getArticleTag()).stream()
                    .filter(a -> isNotBlank(a.description()))
                    .collect(Collectors.toMap(
                        Article::title,
                        article -> article,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                    ))
                    .values());
        } catch (Exception e) {
            log.error("Error while parsing articles on page url {}", RSS_LINK, e);
        }

        // очередность: от старого к свежему
        Collections.reverse(result);
        return result;
    }
}
