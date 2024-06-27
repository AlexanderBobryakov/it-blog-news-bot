package com.asbobryakov.bot.blognews.parser;

import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.dto.ArticleTag;

import java.util.List;

public interface BlogParser {
    ArticleTag getArticleTag();

    List<Article> parseLastArticles();
}
