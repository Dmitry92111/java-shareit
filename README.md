# Explore With Me (EWM)

Backend-приложение для публикации и поиска событий с возможностью участия пользователей и сбором статистики просмотров.

## Стек
- Java 21
- Spring Boot
- Spring Data JPA (Hibernate)
- PostgreSQL
- Docker / Docker Compose
- MapStruct
- REST API
- Postman / Newman (тесты)

## Архитектура
Проект состоит из двух сервисов:
- **ewm-main-service** — основной сервис (события, пользователи, заявки)
- **ewm-stats-service** — сервис статистики (просмотры)

## Основной функционал
- CRUD событий (создание, редактирование, публикация)
- Участие в событиях (Participation Requests)
- Ограничение по количеству участников
- Модерация заявок
- Поиск событий с фильтрами
- Подсчёт просмотров через stats-service

## Особенности
- Батчевые запросы для статистики (без N+1)
- Offset-based пагинация (`from`, `size`)
- Глобальный обработчик ошибок (409 / 400 / 404)
- Валидация входных данных

## Запуск
docker-compose up

Приложения будут доступны:

main-service: http://localhost:8080
stats-service: http://localhost:9090

## Тестирование

Тесты выполняются через Postman/Newman:

newman run ./tests/postman/ewm-main-service.json

## Структура
ewm-main-service/
ewm-stats-service/
docker-compose.yml

## Автор
Dmitry Karfidov
