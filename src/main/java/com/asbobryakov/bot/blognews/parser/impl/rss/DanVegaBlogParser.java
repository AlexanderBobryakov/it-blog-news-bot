package com.asbobryakov.bot.blognews.parser.impl.rss;

import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.RssBlogParser;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.DAN_VEGA;

@Slf4j
public class DanVegaBlogParser extends RssBlogParser {
    private static final String RSS_LINK = "https://www.danvega.dev/rss.xml";

    @Override
    public ArticleTag getArticleTag() {
        return DAN_VEGA;
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
