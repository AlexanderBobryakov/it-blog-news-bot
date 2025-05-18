package com.asbobryakov.bot.blognews.config;

import lombok.experimental.UtilityClass;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.System.getenv;

@UtilityClass
public class Env {
    public static final String CHANNEL_ID = getenv("CHANNEL_ID");
    public static final String BOT_TOKEN = getenv("BOT_TOKEN");
    public static final String GOOGLE_TRANSLATE_TOKEN = getenv("GOOGLE_TRANSLATE_TOKEN");
    public static final boolean USE_TRANSLATOR = parseBoolean(getenv("USE_TRANSLATOR"));
}
