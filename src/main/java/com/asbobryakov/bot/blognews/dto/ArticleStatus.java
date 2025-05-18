package com.asbobryakov.bot.blognews.dto;

import lombok.Getter;

@Getter
public enum ArticleStatus {
    DISABLED("_disabled_"),
    EXCEPTION("_exception_"),
    ;

    private final String value;

    ArticleStatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
