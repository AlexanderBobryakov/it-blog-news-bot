package com.asbobryakov.bot.blognews.dto;

import lombok.Getter;

@Getter
public enum ArticleTag {
    FLINK("Flink"),
    KAFKA("Kafka"),
    SPRING("Spring"),
    APACHE("Apache"),
    VLAD_MIHALCEA("Vlad_Mihalcea"),
    TEST_CONTAINERS("TestContainers"),
    MICROSERVICES_IO("Microservices_io"),
    DECODABLE("Decodable"),
    QUASTOR("Quastor"),
    CONFLUENT("Confluent"),
    ALGOMASTER("AlgoMaster"),

    WEBKIT("Webkit"),
    FINGERPRINT("Fingerprint"),
    ;

    private final String value;

    ArticleTag(String value) {
        this.value = value;
    }
}
