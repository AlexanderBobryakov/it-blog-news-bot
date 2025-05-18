package com.asbobryakov.bot.blognews.parser.impl.rss;

import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.RssBlogParser;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.MICROSERVICES_IO;

@Slf4j
public class MicroservicesIoBlogParser extends RssBlogParser {
    private static final String BLOG_LINK = "https://microservices.io/feed.xml";

    @Override
    public ArticleTag getArticleTag() {
        return MICROSERVICES_IO;
    }

    @Override
    protected String getRssLink() {
        return BLOG_LINK;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
