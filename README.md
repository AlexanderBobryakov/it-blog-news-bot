<h1 align="center"> IT Blog News Bot </h1> <br>

The project is a telegram bot that scans various IT blogs and publishes them in a telegram channel.

Channel link - [IT Blog News](https://t.me/it_blog_news)    
Contact the author via Telegram - [@appp_master](https://t.me/appp_master)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Supported Blogs 
### [IT Blog News](https://t.me/it_blog_news)
#### Technical blogs
- [Kafka](https://kafka.apache.org/blog)
- [Flink](https://flink.apache.org/posts/)
- [Spring](https://spring.io/blog)
- [Apache Foundation](https://news.apache.org/)
- [VladMihalcea](https://vladmihalcea.com/blog/)
- [TestContainers](https://www.atomicjar.com/category/testcontainers/)
- [Microservices io](https://microservices.io/)
- [Decodable](https://www.decodable.co/blog)
- [Quastor](https://blog.quastor.org/archive?page=1)
- [Confluent](https://www.confluent.io/blog/)
- [AlgoMaster (system-design)](https://blog.algomaster.io/t/system-design)
- [DanVega (Java Champion)](https://www.danvega.dev/blog/)
  
#### Fingerprint blogs
- [Webkit (web browser engine)](https://webkit.org/blog/category/privacy/)
- [Fingerprint](https://fingerprint.com/blog/)
- [ScrapFly](https://scrapfly.io/blog/tag/blocking/)
- [Akamai](https://www.akamai.com/blog/security)

## Description

The bot regularly scans blogs, forms publications from them for each individual blog post and publishes
links to them in the channel with a description, preview (where possible), date and tag.

The bot is resistant to restarts and does not duplicate articles already published in the channel.
The state is stored in the channel's pinned message.

Blog parsing is implemented via the [RSS](https://ru.wikipedia.org/wiki/RSS) protocol (where possible)
or via manual parsing of pages using the [Jsoup](https://github.com/jhy/jsoup) library

## Additional features
- (disabled by community requests) Translation of articles into Russian via Google Translate. 
To enable the function, you must pass the environment variable `USE_TRANSLATOR` = `true`, as well as the google token
  `GOOGLE_TRANSLATE_TOKEN` (see [stackoverflow](https://stackoverflow.com/a/48159904/13196276))

## Environment variables
The required variables are listed in the `com.asbobryakov.bot.blognews.config.Env` file:
- `CHANNEL_ID` - the identifier of the telegram channel to which the bot is connected
- `BOT_TOKEN` - the secret token of the bot, received from Telegram when it was created
- `GOOGLE_TRANSLATE_TOKEN` - a secret token from Google to provide the ability to use translation
- `USE_TRANSLATOR` - the flag for translating the description of articles

## Requirements
- Java 21 (+ Gradle)
- Telegram API

## Local launch
Build:
```bash
$ ./gradlew shadowJar
```
Running a project via Docker:
```bash
docker build -t it-blog-news-bot-app .
```
```bash
docker run -p 8080:8080 --name it-blog-news-bot-container it-blog-news-bot-app
```
Port `8080` is configured for healthcheck running java program (see `Dockerfile` and `healthcheck.py`)

## CI/CD
Deployment is carried out to the [Amvera](https://amvera.ru/) cloud via web hook implementation.
Link to the [it-blog-news-bot](https://cloud.amvera.ru/projects/compute/it-blog-news-bot) project
Project owner `appp-master`

## Testing
There are no tests in the repository yet, actual testing is done through a secondary testing channel
[IT Blog News TEST](https://t.me/it_blog_news_test)
