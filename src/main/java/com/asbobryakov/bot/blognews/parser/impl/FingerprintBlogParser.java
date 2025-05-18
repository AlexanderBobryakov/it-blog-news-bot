package com.asbobryakov.bot.blognews.parser.impl;

import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.asbobryakov.bot.blognews.parser.BlogParser;
import com.asbobryakov.bot.blognews.parser.exception.ParserFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.dto.ArticleTag.FINGERPRINT;
import static java.util.Collections.reverse;

@Slf4j
public class FingerprintBlogParser implements BlogParser, AutoCloseable {
    private static final String BLOG_LINK = "https://fingerprint.com/page-data/blog/page-data.json";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public FingerprintBlogParser() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ArticleTag getArticleTag() {
        return FINGERPRINT;
    }

    @Override
    public List<Article> parseLastArticles() throws ParserFailedException {
        final var result = new ArrayList<Article>();
        result.addAll(parseArticlesOnPage(BLOG_LINK));

        // очередность: от старого к свежему
        reverse(result);
        return result;
    }

    private List<Article> parseArticlesOnPage(String pageUrl) throws ParserFailedException {
        final var result = new ArrayList<Article>();
        try {
            final var response = httpClient.send(
                HttpRequest.newBuilder()
                    .uri(URI.create(pageUrl))
                    .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            final var rootNode = objectMapper.readTree(response.body());
            final var edges = rootNode.path("result").path("data").path("posts").path("edges");
            edges.forEach(edge -> {
                final var node = edge.path("node");
                final var frontmatter = node.path("frontmatter");
                final var metadata = frontmatter.path("metadata");
                result.add(new Article(
                    metadata.path("url").asText(),
                    metadata.path("title").asText(),
                    metadata.path("description").asText(),
                    frontmatter.path("publishDate").asText(),
                    getArticleTag()));
            });
        } catch (Exception e) {
            log.error("Error while parsing articles on page url {}", pageUrl, e);
            throw new ParserFailedException(e);
        }
        return result;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void close() {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}
