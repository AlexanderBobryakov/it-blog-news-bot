package com.asbobryakov.bot.blognews.parser.impl.rss;

import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.RssBlogParser;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.SPRING;

@Slf4j
public class SpringBlogParser extends RssBlogParser {
    private static final String RSS_LINK = "https://spring.io/blog.atom";

    @Override
    public ArticleTag getArticleTag() {
        return SPRING;
    }

    @Override
    protected String getRssLink() {
        return RSS_LINK;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
