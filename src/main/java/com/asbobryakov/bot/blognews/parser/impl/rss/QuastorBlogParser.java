package com.asbobryakov.bot.blognews.parser.impl.rss;

import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.RssBlogParser;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.QUASTOR;

@Slf4j
public class QuastorBlogParser extends RssBlogParser {
    private static final String RSS_LINK = "https://rss.beehiiv.com/feeds/nczRb4PQ6t.xml";

    @Override
    public ArticleTag getArticleTag() {
        return QUASTOR;
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
