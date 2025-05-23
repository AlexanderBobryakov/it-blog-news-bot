package com.asbobryakov.bot.blognews.telegram;

import com.asbobryakov.bot.blognews.config.Env;
import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.dto.ArticleTag;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.asbobryakov.bot.blognews.config.Env.CHANNEL_ID;
import static com.asbobryakov.bot.blognews.utils.Formatting.formatArticleLink;
import static com.asbobryakov.bot.blognews.utils.Translator.translate;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Locale.ROOT;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
public class ItNewsBot extends TelegramLongPollingBot {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ss");

    public ItNewsBot(DefaultBotOptions options) {
        super(options, Env.BOT_TOKEN);
    }

    @Override
    public void onUpdateReceived(Update update) {
        // no op
    }

    @Override
    public String getBotUsername() {
        return "news_publisher";
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    @SneakyThrows
    public void updatePinnedMessageBy(Map<ArticleTag, String> lastArticleByTag) {
        log.info("Update pinned message...");
        sleep();
        final var message = new EditMessageText();
        message.setChatId(CHANNEL_ID);
        message.setMessageId(execute(new GetChat(CHANNEL_ID)).getPinnedMessage().getMessageId());

        message.setText(
            """
                Channel with IT articles from different blogs.
                
                Contacts: @appp_master
                Github: https://github.com/AlexanderBobryakov/it-blog-news-bot
                
                Latest articles:
                %s
                
                <i>Updated: %s</i>
                """.formatted(
                lastArticleByTag.entrySet().stream()
                    .map(e -> "<b> - " + e.getKey().getValue() + "</b>: " + e.getValue())
                    .collect(Collectors.joining("\n")),
                LocalDateTime.now(ZoneId.of("Europe/Moscow")).format(DATE_TIME_FORMATTER)
            )
        );

        message.disableWebPagePreview();
        message.enableHtml(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            // message's text didn't changes
            log.debug("Error while update pinned message", e);
        }
    }

    @SneakyThrows
    public Map<ArticleTag, String> restoreLastArticlesFromPinnedMessage() {
        final var getChat = new GetChat(CHANNEL_ID);
        final var chatInfo = execute(getChat);
        final var text = chatInfo.getPinnedMessage().getText();

        final var lastArticleByTag = new HashMap<ArticleTag, String>();
        try {
            final var startWords = "Latest articles:";
            final var articlesByTag = text.substring(text.lastIndexOf(startWords) + startWords.length(), text.lastIndexOf("Updated:"))
                .split("\n");
            for (String tagAndArticle : articlesByTag) {
                if (isBlank(tagAndArticle)) {
                    continue;
                }
                final var tag = tagAndArticle.substring(3, tagAndArticle.indexOf(":"));
                final var article = tagAndArticle.substring(tagAndArticle.indexOf(":") + 1).trim();
                lastArticleByTag.put(ArticleTag.valueOf(tag.toUpperCase(ROOT)), article);
            }
        } catch (Exception e) {
            log.error("Error while parsing pinned message", e);
        }

        return lastArticleByTag;
    }

    @SneakyThrows
    public synchronized void publishArticle(Article article) {
        log.info("Publish article {}", article.title());
        sleep();
        final var message = new SendMessage();
        message.setChatId(CHANNEL_ID);
        message.setText(
            """
                %s
                
                %s
                
                <i>%s</i>
                
                #%s
                """.formatted(
                formatArticleLink(article),
                translate(article.description()),
                article.date(),
                article.tag().getValue()
            )
        );

        message.enableWebPagePreview();
        message.enableNotification();
        message.setProtectContent(false);
        message.enableHtml(true);
        execute(message);
    }

    private void sleep() throws InterruptedException {
        // cause: Telegram "Too Many Requests"
        Thread.sleep(1000L * ThreadLocalRandom.current().nextInt(15, 30));
    }
}
