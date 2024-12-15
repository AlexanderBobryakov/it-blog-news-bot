package com.asbobryakov.bot.blognews.parser;

import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.utils.RssParser;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static java.util.Collections.reverse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public abstract class RssBlogParser implements BlogParser {

    @Override
    public List<Article> parseLastArticles() {
        final var result = new ArrayList<Article>();
        try {
            result.addAll(
                RssParser.parse(new URL(getRssLink()), getArticleTag()).stream()
                    .filter(a -> isNotBlank(a.description()))
                    .collect(Collectors.toMap(
                        Article::title,
                        article -> article,
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                    ))
                    .values());
        } catch (Exception e) {
            log.error("Error while parsing articles on page url {}", getRssLink(), e);
        }

        // очередность: от старого к свежему
        reverse(result);
        return result;
    }

    protected abstract String getRssLink();
}