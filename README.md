# FeedSystem

Scalable Instagram/Twitter-like news feed system built with Spring Boot 3, PostgreSQL, and Redis.

## Architecture Overview

This system implements a **Fan-out on Write** (Push) model for news feed generation:

- **Post Creation**: When a user creates a post, it's asynchronously pushed to all followers' Redis feeds
- **Redis Storage**: Each user's feed is stored as a Redis Sorted Set (ZSET) with timestamp as score
- **Hybrid Feed Retrieval**: GET /api/feed first checks Redis cache, falls back to database query
- **Hot User Handling**: Users with >10,000 followers get partial fan-out to recent followers only
- **Batch Processing**: Fan-out operations are processed in configurable batches using async workers
- **Rate Limiting**: Redis-based sliding window rate limiting for post creation and feed reads

## Tech Stack

- **Spring Boot 3.3.5** with Java 21
- **Spring Data JPA** for database persistence
- **PostgreSQL** for relational data storage
- **Redis** for feed caching and rate limiting
- **Spring Security** with JWT authentication
- **MapStruct** for DTO mapping
- **Lombok** for reducing boilerplate
- **Swagger/OpenAPI** for API documentation

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

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token

### Posts
- `POST /api/posts` - Create a new post (requires authentication)
- `GET /api/posts/{postId}` - Get post by ID
- `GET /api/posts/users/{userId}` - Get user's posts with pagination

### Feed
- `GET /api/feed` - Get personalized feed for authenticated user (Redis cache + DB fallback)

### Follow
- `POST /api/users/{userId}/follow` - Follow a user (requires authentication)
- `DELETE /api/users/{userId}/follow` - Unfollow a user (requires authentication)
- `GET /api/users/{userId}/followers` - Get user's followers
- `GET /api/users/{userId}/following` - Get users that user follows

## Configuration Groups

- `feedsystem.jwt`: JWT issuer, signing secret, and token lifetime
- `feedsystem.feed`: feed pagination and Redis feed size
- `feedsystem.fanout`: fan-out batch size, worker count, and hot-user threshold
- `feedsystem.rate-limit`: post creation and feed read limits

## Key Features

1. **Scalable Fan-out**: Async batch processing prevents blocking on large follower counts
2. **Hot User Optimization**: Partial fan-out for users with many followers
3. **Hybrid Caching**: Redis-first with database fallback ensures reliability
4. **Rate Limiting**: Protects against abuse with configurable limits
5. **Comprehensive Testing**: Unit tests for core business logic
