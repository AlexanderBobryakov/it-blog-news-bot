package com.asbobryakov.service.parser;

import com.asbobryakov.dto.Article;
import com.asbobryakov.dto.ArticleTag;

import java.util.List;

public interface BlogParser {
    ArticleTag getArticleTag();

    List<Article> parseLastArticles();
}
