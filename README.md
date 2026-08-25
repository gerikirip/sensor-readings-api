# Sensor Readings API

REST API that queries a static CSV of IoT temperature and humidity readings. There is no database: the file is loaded at startup.

The required endpoint is `GET /api/readings`. Two extras: JWT so the query is not fully public, and `unit` so every temperature in the response is Celsius or Fahrenheit (the CSV mixes both).

## Requirements

- Docker (Compose), or
- Java 25 and the Maven wrapper for a local run

## Run

Docker (builds the image, runs tests during the build, then starts the API):

```bash
docker compose up --build
```

Maven, without Docker:

```bash
./mvnw spring-boot:run
```

On Windows: `.\mvnw.cmd spring-boot:run`

The app listens on [http://localhost:8080](http://localhost:8080). Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Sample data: `src/main/resources/readings.csv`. Override the file with `READINGS_LOCATION` (classpath or filesystem path).

## Authentication

Default user: `admin` / `admin`.

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

Use the returned token as `Authorization: Bearer <token>` on `/api/readings`. Swagger: **Authorize**, then paste the token.

## Query readings

`GET /api/readings`

| Parameter | Required | Description |
|-----------|----------|-------------|
| `unit` | yes | Response temperature scale: `C` or `F`. All readings are converted to this unit (the CSV mixes Celsius and Fahrenheit) |
| `devices` | no | Device ids. Omit for all devices |
| `from`, `to` | no | Inclusive ISO-8601 instants. If both are omitted, the **latest** reading per device is used |
| `metrics` | no | `TEMPERATURE`, `HUMIDITY`, or both. Omit for both |
| `statistic` | no | `MIN`, `MAX`, or `AVERAGE` (default `AVERAGE`) over the matching rows **per device** |

Without a date range, min / max / average are computed on a single latest row, so they are the same number.

Latest snapshot (both metrics, average, Celsius):

```bash
curl -s "http://localhost:8080/api/readings?unit=C" \
  -H "Authorization: Bearer <token>"
```

Range + min temperature for device 1:

```bash
curl -s "http://localhost:8080/api/readings?devices=1&from=2026-07-10T00:00:00Z&to=2026-07-10T23:59:59Z&metrics=TEMPERATURE&statistic=MIN&unit=C" \
  -H "Authorization: Bearer <token>"
```

## Tests

```bash
./mvnw test
```
