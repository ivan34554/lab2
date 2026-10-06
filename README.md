# Lab 2. REST API для публикации подкастов (вариант 12)

Лабораторная работа по дисциплине «Разработка безопасного программного обеспечения».
REST API на Spring Boot для системы публикации подкастов. Данные хранятся в памяти приложения.

## Стек

- Java 21
- Spring Boot 4.1.1 (Spring MVC)
- Bean Validation (`spring-boot-starter-validation`)
- Lombok
- Maven (через `mvnw`)

## Запуск

```bash
./mvnw spring-boot:run
```

Приложение стартует на `http://localhost:8080`.
Тесты: `./mvnw test`.

Данные хранятся в памяти, поэтому после перезапуска все списки пустые.

## Архитектура

Код разделён на слои:

- `controller` принимает запросы, проверяет вход и преобразует сущности в DTO (маппинг);
- `service` содержит бизнес-логику: проверки, каскадное удаление, расчёты со временем;
- `repository` отвечает за хранение (коллекции в памяти);
- `dto`, `model`, `exception`, `config` содержат DTO, сущности, исключения и конфигурацию.

## Сущности

| Сущность | Путь | Поля |
|---|---|---|
| Пользователь | `/api/users` | `id`, `username`, `email`, `createdAt` |
| Подкаст | `/api/podcasts` | `id`, `title`, `authorId`, `status` (`ACTIVE`, `ARCHIVED`), `createdAt` |
| Эпизод | `/api/episodes` | `id`, `podcastId`, `title`, `durationSeconds`, `state` (`DRAFT`, `SCHEDULED`, `PUBLISHED`), `createdAt` |
| Аудиофайл | `/api/audio-files` | `id`, `episodeId`, `path`, `format` (`MP3`, `WAV`, `OGG`, `FLAC`, `AAC`), `sizeBytes`, `main`, `createdAt` |
| Публикационный слот | `/api/publication-slots` | `id`, `podcastId`, `episodeId`, `scheduledAt`, `status` (`PLANNED`, `PUBLISHED`, `CANCELLED`), `createdAt` |

## Операции

Для каждого ресурса реализовано пять операций:

| Метод | Путь | Ответ |
|---|---|---|
| GET | `/api/{resources}` | `200` и массив ресурсов |
| GET | `/api/{resources}/{id}` | `200` и ресурс |
| POST | `/api/{resources}` | `201` и созданный ресурс с `id` |
| PATCH | `/api/{resources}/{id}` | `200` и обновлённый ресурс |
| DELETE | `/api/{resources}/{id}` | `204` |

В PATCH передаются только изменяемые поля.

## Бизнес-правила

- **Время задаёт сервер.** Поле `createdAt` ставится на сервере из единого `Clock`, из запросов оно не принимается.
- **Каскадное удаление без висячих ссылок.** Удаление пользователя удаляет его подкасты, удаление подкаста удаляет его эпизоды, удаление эпизода удаляет его аудиофайлы и слоты.
- **Проверка связей.** Автор, подкаст и эпизод, на которые ссылается объект, должны существовать. Эпизод в слоте должен принадлежать указанному подкасту.
- **Основной аудиофайл.** У эпизода может быть только один основной файл: при назначении нового предыдущий перестаёт быть основным.
- **Слоты публикации.**
  - время слота должно быть в будущем по часам сервера;
  - опубликовать слот можно, когда его время наступило и у эпизода есть основной аудиофайл;
  - при публикации эпизод переходит в состояние `PUBLISHED`;
  - опубликованный слот изменять нельзя.

## Безопасность и обработка ошибок

- Входные данные валидируются (обязательные поля, длины, формат e-mail, положительные числа).
- Путь аудиофайла должен быть относительным и не содержать `..`, что защищает от path traversal.
- Ошибки возвращаются в едином формате, без внутренних деталей реализации:

```json
{"status": 404, "message": "Podcast 1 not found"}
```

- `400` означает неверные данные или нарушение бизнес-правила, `404` означает, что объект не найден.

## Примеры запросов

```bash
# создать пользователя
curl -i -X POST localhost:8080/api/users -H 'Content-Type: application/json' \
  -d '{"username":"ivan","email":"ivan@example.com"}'

# создать подкаст
curl -i -X POST localhost:8080/api/podcasts -H 'Content-Type: application/json' \
  -d '{"title":"Безопасный код","authorId":1}'

# создать эпизод
curl -i -X POST localhost:8080/api/episodes -H 'Content-Type: application/json' \
  -d '{"podcastId":1,"title":"Выпуск 1","durationSeconds":1800}'

# добавить основной аудиофайл
curl -i -X POST localhost:8080/api/audio-files -H 'Content-Type: application/json' \
  -d '{"episodeId":1,"path":"audio/ep1.mp3","format":"MP3","sizeBytes":1048576,"main":true}'

# запланировать публикацию
curl -i -X POST localhost:8080/api/publication-slots -H 'Content-Type: application/json' \
  -d '{"podcastId":1,"episodeId":1,"scheduledAt":"2030-01-01T00:00:00Z"}'

# изменить статус подкаста
curl -i -X PATCH localhost:8080/api/podcasts/1 -H 'Content-Type: application/json' \
  -d '{"status":"ARCHIVED"}'

# удалить пользователя (каскадно удалятся подкасты, эпизоды, файлы и слоты)
curl -i -X DELETE localhost:8080/api/users/1
```