package com.asbobryakov.bot.blognews.utils;

import com.asbobryakov.bot.blognews.dto.Article;
import com.asbobryakov.bot.blognews.dto.ArticleTag;
import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.ZoneId.systemDefault;
import static java.util.Optional.ofNullable;

@UtilityClass
public class RssParser {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        .localizedBy(Locale.of("ru"));

    public static List<Article> parse(URL feedUrl, ArticleTag tag) throws IOException, FeedException {
        final var result = new ArrayList<Article>();
        final var con = (HttpURLConnection) feedUrl.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        final var reader = new XmlReader(con.getInputStream(), true, UTF_8.name());
        final var syndFeedInput = new SyndFeedInput();
        syndFeedInput.setAllowDoctypes(true);
        final var feed = syndFeedInput.build(reader);
        for (SyndEntry entry : feed.getEntries()) {
            final var link = entry.getLink();
            final var title = entry.getTitle();
            final var htmlDescription = ofNullable(entry.getDescription())
                .map(SyndContent::getValue)
                .orElseGet(() -> {
                    return ofNullable(entry.getContents())
                        .map(c -> c.stream().map(SyndContent::getValue).collect(Collectors.joining(".\n\n")))
                        .orElse("");
                });
            final var description = parseHtmlText(htmlDescription);
            final var date = entry.getPublishedDate().toInstant();
            result.add(new Article(link, title, description,
                LocalDate.ofInstant(date, systemDefault()).format(DATE_FORMAT), tag));
        }
        return result;
    }

    private String parseHtmlText(String html) {
        final var document = Jsoup.parse(html);
        final var text = new StringBuilder();

        for (var element : document.body().children()) {
            if (element.tagName().equals("p")) {
                text.append(element.text()).append("\n\n");
            } else if (element.tagName().equals("ul")) {
                for (var li : element.select("li")) {
                    text.append("• ").append(li.text()).append("\n");
                }
                text.append("\n");
            }
        }
        return text.toString();
    }
}
