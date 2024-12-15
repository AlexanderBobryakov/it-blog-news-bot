package com.asbobryakov.bot.blognews.dto;

import static java.util.Objects.requireNonNull;

public record Article(
    String link,
    String title,
    String description,
    String date,
    ArticleTag tag
) {
    public Article(String link,
                   String title,
                   String description,
                   String date,
                   ArticleTag tag) {
        this.link = requireNonNull(link, "Link can not be null").trim();
        this.title = requireNonNull(title, "Title can not be null").trim();
        requireNonNull(description, "Description can not be null");
        this.description = description.length() >= 1000
            ? description.trim().substring(0, 1000) + "..."
            : description.trim();
        this.date = requireNonNull(date, "Date can not be null").trim();
        this.tag = requireNonNull(tag, "Tag can not be null");
    }
}
