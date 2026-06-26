# FeedSystem

Scalable Instagram/Twitter-like news feed system built with Spring Boot 3, PostgreSQL, and Redis.

## Local Infrastructure

Start PostgreSQL and Redis:

```powershell
docker compose up -d
```

Run the application:

```powershell
.\mvnw.cmd spring-boot:run
```

Run tests:

```powershell
.\mvnw.cmd test
```

## Default Local Services

PostgreSQL:

- URL: `jdbc:postgresql://localhost:5432/feed_system`
- Username: `feed_user`
- Password: `feed_password`

Redis:

- Host: `localhost`
- Port: `6379`

Swagger UI:

- `http://localhost:8080/swagger-ui.html`

## Configuration Groups

- `feedsystem.jwt`: JWT issuer, signing secret, and token lifetime.
- `feedsystem.feed`: feed pagination and Redis feed size.
- `feedsystem.fanout`: fan-out batch size, worker count, and hot-user threshold.
- `feedsystem.rate-limit`: post creation and feed read limits.
