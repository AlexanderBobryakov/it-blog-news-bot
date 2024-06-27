package com.asbobryakov.bot.blognews.utils;

import com.asbobryakov.bot.blognews.dto.Article;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Formatting {
    public static String formatArticleLink(Article article) {
        return "<a href=\"%s\">%s</a>".formatted(article.link(), article.title());
    }
}
