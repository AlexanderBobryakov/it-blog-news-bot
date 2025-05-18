package com.asbobryakov.bot.blognews.parser.impl.rss;

import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.RssBlogParser;
import com.asbobryakov.bot.blognews.parser.exception.ParserFailedException;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.DAN_VEGA;

@Slf4j
public class DanVegaBlogParser extends RssBlogParser {
    public static final String BASE_LINK = "https://www.danvega.dev";
    private static final String RSS_LINK = BASE_LINK + "/rss.xml";

    @Override
    public ArticleTag getArticleTag() {
        return DAN_VEGA;
    }

    @Override
    protected String getRssLink() {
        return RSS_LINK;
    }

    @Override
    public List<Article> parseLastArticles() throws ParserFailedException {
        return super.parseLastArticles().stream().map(a -> new Article(
            BASE_LINK + a.link(),
            a.title(),
            a.description(),
            a.date(),
            a.tag()
        )).toList().reversed();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
