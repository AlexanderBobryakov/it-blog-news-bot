<h1 align="center"> IT Blog News Bot </h1> <br>

Проект представляет собой telegram-бота, который сканирует различный блоги из IT и публикует их в telegram-канале.

Ссылка на канал - [IT Blog News](https://t.me/it_blog_news) 
Связаться с автором через Telegram - [@appp_master](https://t.me/appp_master) 

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Поддерживаемые блоги 
### [IT Blog News](https://t.me/it_blog_news)
- [Apache Foundation](https://news.apache.org/)
- [Flink](https://flink.apache.org/posts/)
- [Kafka](https://kafka.apache.org/blog)
- [Microservices io](https://microservices.io/)
- [Spring](https://spring.io/blog)
- [TestContainers](https://www.atomicjar.com/category/testcontainers/)
- [ThorbenJanssen](https://thorben-janssen.com/blog/)
- [VladMihalcea](https://vladmihalcea.com/blog/)
- [Decodable](https://www.decodable.co/blog)
- [Quastor](https://blog.quastor.org/archive?page=1)
- [Confluent](https://www.confluent.io/blog/)
- [AlgoMaster (system-design)](https://blog.algomaster.io/t/system-design)

### [Identification Blog News]()
- [Webkit (web browser engine)](https://webkit.org/blog/category/privacy/)
- [Fingerprint](https://fingerprint.com/blog/)

## Описание работы

Бот регулярно сканирует блоги, формирует из них публикации на каждый отдельный пост
блога и публикует на них ссылки в канале с описанием, превью (где возможно), датой и тэгом.

Бот устойчив к перезапуску и не дублирует уже опубликованные в канале статьи. Состояние хранится в
закрепленном сообщении канала.

Реализация парсинга блогов осуществлена через протокол [RSS](https://ru.wikipedia.org/wiki/RSS) (где возможно)
либо через ручной парсинг страниц с помощью бибилиотеки [Jsoup](https://github.com/jhy/jsoup)

## Дополнительные возможности
- (отключено по просьбам сообщества) Перевод статей на русский язык через Google-переводчик. Для 
включения функции необходимо передать переменную окружения `USE_TRANSLATOR` = `true`, а также google-токен
`GOOGLE_TRANSLATE_TOKEN` (см. [stackoverflow](https://stackoverflow.com/a/48159904/13196276))

## Переменные окружения
Необходимые переменные перечислены в файле `com.asbobryakov.bot.blognews.config.Env`:
- `CHANNEL_ID` - идентификатор telegram-канала, к которому подключен бот
- `INFO_MESSAGE_ID` - номер закрепленного сообщения в канале, в котором содержится информация об уже
опубликованных статьях (state для бота). **Важно** - это сообщение должно быть создано ботом первоначально
- `BOT_TOKEN` - секретный токен бота, полученный из Telegram при его создании
- `GOOGLE_TRANSLATE_TOKEN` - секретный токен от google для предоставления возможности использовать перевод
- `USE_TRANSLATOR` - флаг перевод описания статей

## Требования
- Java 21 (+ Gradle)
- Telegram API

## Локальный запуск
Сборка проекта:
```bash
$ ./gradlew shadowJar
```
Запуск проекта через Docker:
```bash
docker build -t it-blog-news-bot-app .
```
```bash
docker run -p 8080:8080 --name it-blog-news-bot-container it-blog-news-bot-app
```
Порт `8080` настроен для healthcheck запущенной java-программы (см `Dockerfile` и `healthcheck.py`)

## CI/CD
Деплой осуществляется в облако [Amvera](https://amvera.ru/) через реализацию web-хука.
Ссылка на проект [it-blog-news-bot](https://cloud.amvera.ru/projects/compute/it-blog-news-bot)
Владелец проекта `appp-master`

## Тестирование
Тестов пока в репозитории нет, фактическое тестирование осуществляется через второстепенный тестовый канал
[IT Blog News TEST](https://t.me/it_blog_news_test) 
