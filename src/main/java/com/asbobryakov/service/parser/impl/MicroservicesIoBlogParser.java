package com.asbobryakov.service.parser.impl;

import com.asbobryakov.dto.Article;
import com.asbobryakov.dto.ArticleTag;
import com.asbobryakov.service.parser.BlogParser;
import com.asbobryakov.utils.RssParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.dto.ArticleTag.MICROSERVICES_IO;
import static java.util.Collections.reverse;

@Slf4j
public class MicroservicesIoBlogParser implements BlogParser {
    private static final String BLOG_LINK = "https://microservices.io/feed.xml";

    @Override
    public ArticleTag getArticleTag() {
        return MICROSERVICES_IO;
    }

    @Override
    public List<Article> parseLastArticles() {
        final var result = new ArrayList<Article>();
        try {
            result.addAll(RssParser.parse(new URL(BLOG_LINK), getArticleTag()));
        } catch (MalformedURLException e) {
            log.error("Error while parsing articles on page url {}", BLOG_LINK, e);
        }

        // очередность: от старого к свежему
        reverse(result);
        return result.stream().unordered().toList();
    }
}
