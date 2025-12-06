# Restaurant Aggregator


restaurant-aggregator/
├─ aggregator-app/
│  ├─ pom.xml
│  ├─ src/main/java/com/mercor/restaurant/
│  │  ├─ RestaurantAggregatorApplication.java
│  │  ├─ config/
│  │  │  └─ OpenApiConfig.java
│  │  ├─ domain/
│  │  │  ├─ City.java
│  │  │  ├─ Cuisine.java
│  │  │  ├─ MenuItem.java
│  │  │  ├─ Review.java
│  │  │  ├─ Source.java
│  │  │  ├─ Tag.java
│  │  │  ├─ Restaurant.java
│  │  │  └─ RestaurantSourceMap.java
│  │  ├─ repository/
│  │  │  ├─ CityRepository.java
│  │  │  ├─ CuisineRepository.java
│  │  │  ├─ MenuItemRepository.java
│  │  │  ├─ ReviewRepository.java
│  │  │  ├─ SourceRepository.java
│  │  │  ├─ TagRepository.java
│  │  │  ├─ RestaurantRepository.java
│  │  │  └─ RestaurantSourceMapRepository.java
│  │  ├─ dto/
│  │  │  ├─ RestaurantResponse.java
│  │  │  ├─ RestaurantSearchRequest.java
│  │  │  ├─ ExternalRestaurantDto.java
│  │  │  └─ UpsertResult.java
│  │  ├─ service/
│  │  │  ├─ RestaurantService.java
│  │  │  ├─ RestaurantSpecs.java
│  │  │  ├─ IngestionService.java
│  │  │  └─ DedupService.java
│  │  ├─ web/
│  │  │  ├─ RestaurantController.java
│  │  │  ├─ IngestionController.java
│  │  │  └─ GlobalExceptionHandler.java
│  │  └─ mapper/
│  │     └─ RestaurantMapper.java
│  ├─ src/main/resources/
│  │  ├─ application.yml
│  │  └─ db/migration/V1__init.sql
├─ docker/
│  ├─ Dockerfile
│  └─ docker-compose.yaml
└─ README.md

## Run locally
- Start Postgres: docker compose -f docker/docker-compose.yaml up -d db
- Build: (from aggregator-app) mvn clean package
- Run: mvn spring-boot:run

## Test API
- GET http://localhost:8080/api/v1/restaurants?page=0&size=10
- POST http://localhost

## To prove "Horizontal Scaling" in an interview, we will run 3 instances of your Spring Boot app and put Nginx in front of them to distribute traffic.

docker-compose up -d --build --scale app=3
docker-compose up -d --build --force-recreate --scale app=3
1. **Verify Scaling:**
* Watch the logs: `docker-compose logs -f app`
* Make 5 requests to `http://localhost:80/api/public/ping` (or any endpoint).
* You will see logs scrolling from **3 different container IDs**.
* How to Verify (The "Impact" Moment)
  1.  **Rebuild & Run:** `mvn clean package -DskipTests` -> `docker-compose up -d --build`.
  2.  **Trigger:** Place an order via Postman/Curl.
  3.  **Watch Logs:**
      * You will see the HTTP Response come back instantly (e.g., in 100ms).
      * **5 seconds later**, you will see the log: `COMPLETED: Email sent...`.
      * The Thread Name in the log will be `RestAsync-1` (not `http-nio-8080-exec-1`).
      **This proves you are handling tasks in parallel, maximizing the throughput of your HTTP threads.**


| Concept        | Your Implementation                 | Buzzword to Drop             |
| -------------- | ----------------------------------- | ---------------------------- |
| Handling Load  | Nginx + 3 Replicas + Stateless JWT  | Horizontal Scaling           |
| Slow Tasks     | @Async + Thread Pool Config         | Non-blocking I/O             |
| Failures       | Resilience4j Circuit Breaker        | Fault Tolerance              |
| Data Quality   | DedupService + SourceMap            | Data Normalization           |
| Security       | RBAC (ROLE_USER vs ROLE_ADMIN)      | Principle of Least Privilege |
| DB Performance | Entity Indexes + Connection Pooling | Query Optimization           |

