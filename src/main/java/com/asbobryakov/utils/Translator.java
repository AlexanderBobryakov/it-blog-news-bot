package com.asbobryakov.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.config.Env.GOOGLE_TRANSLATE_TOKEN;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * <a href="https://stackoverflow.com/a/48159904/13196276"> Stackoverflow realization via Google
 * API
 * </a>
 */
@Slf4j
@UtilityClass
public class Translator {
    private static final String EN = "en";
    private static final String RU = "ru";

    public static String translate(String text) {
        try {
            final var urlStr = "https://script.google.com/macros/s/" + GOOGLE_TRANSLATE_TOKEN + "/exec" +
                               "?q=" + URLEncoder.encode(text, UTF_8) +
                               "&target=" + RU +
                               "&source=" + EN;
            final var response = new StringBuilder();
            final var con = (HttpURLConnection) new URL(urlStr).openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            try (final var in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return response.toString();
        } catch (Exception e) {
            log.error("Error while translating text: '{}'", text, e);
            return text;
        }
    }
}
