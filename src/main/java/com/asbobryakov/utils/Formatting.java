package com.asbobryakov.utils;

import com.asbobryakov.dto.Article;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Formatting {
    public static String formatArticleLink(Article article) {
        return "<a href=\"%s\">%s</a>".formatted(article.link(), article.title());
    }
}
