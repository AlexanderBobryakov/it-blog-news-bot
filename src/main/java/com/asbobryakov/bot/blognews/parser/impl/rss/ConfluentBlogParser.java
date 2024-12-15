package com.asbobryakov.bot.blognews.parser.impl.rss;

import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.RssBlogParser;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.CONFLUENT;

@Slf4j
public class ConfluentBlogParser extends RssBlogParser {
    private static final String RSS_LINK = "https://www.confluent.io/rss.xml";

    @Override
    public ArticleTag getArticleTag() {
        return CONFLUENT;
    }

    @Override
    protected String getRssLink() {
        return RSS_LINK;
    }
}
