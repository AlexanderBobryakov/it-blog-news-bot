package com.asbobryakov.config;

import lombok.experimental.UtilityClass;

import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;

@UtilityClass
public class Env {
    public static final String CHANNEL_ID = getenv("CHANNEL_ID");
    public static final int INFO_MESSAGE_ID = parseInt(getenv("INFO_MESSAGE_ID"));
    public static String BOT_TOKEN = getenv("BOT_TOKEN");
    public static String GOOGLE_TRANSLATE_TOKEN = getenv("GOOGLE_TRANSLATE_TOKEN");
}
