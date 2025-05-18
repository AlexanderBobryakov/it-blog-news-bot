package com.asbobryakov.bot.blognews.parser.impl.rss;

import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.RssBlogParser;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.AKAMAI;

@Slf4j
public class AkamaiBlogParser extends RssBlogParser {
    private static final String RSS_LINK = "https://feeds.feedburner.com/akamai/sitr";

    @Override
    public ArticleTag getArticleTag() {
        return AKAMAI;
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
