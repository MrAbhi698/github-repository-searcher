# GitHub Repository Searcher

A Spring Boot REST API application that searches GitHub repositories using the GitHub REST API, stores repository information in PostgreSQL, and allows users to retrieve stored repositories using filters and sorting options.

## Project Overview

The GitHub Repository Searcher allows users to:

- Search GitHub repositories by repository name or keyword.
- Filter GitHub search results by programming language.
- Sort search results by stars, forks, or last updated date.
- Store repository details in a PostgreSQL database.
- Update existing repository records instead of creating duplicates.
- Retrieve stored repositories using language and minimum-star filters.
- Sort stored repositories by stars, forks, or last updated date.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 4.1.2 | Backend framework |
| Spring Web | REST API development |
| Spring Data JPA | Database operations |
| PostgreSQL | Database |
| Hibernate | ORM |
| GitHub REST API | Repository search |
| Maven | Build and dependency management |
| Swagger / OpenAPI | API documentation |
| JUnit 5 | Unit testing |
| Mockito | Mocking dependencies |
| Postman | API testing |

## Project Structure

```text
github-repository-searcher/
│
├── src/
│   ├── main/
│   │   ├── java/com/freightfox/github_repository_searcher/
│   │   │   ├── client/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   └── resources/
│   │       └── application.properties/
│   │
│   └── test/
│       └── java/com/freightfox/github_repository_searcher/
│           └── service/
│               └── GithubSearchServiceTest.java
│
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Prerequisites

Make sure the following software is installed:

- Java Development Kit (JDK) 17
- PostgreSQL
- IntelliJ IDEA or another Java IDE
- Postman (for API testing)
- Git

## Database Configuration

1. Open PostgreSQL or pgAdmin.
2. Create a database named:

```sql
CREATE DATABASE github_repository_db;
```

3. Open `src/main/resources/application.properties`.
4. Configure the database connection:

```properties
spring.application.name=github-repository-searcher

spring.datasource.url=jdbc:postgresql://localhost:5432/github_repository_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

server.port=8080
```

Replace `YOUR_PASSWORD` with your local PostgreSQL password.

## Running the Application

Clone the repository:

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

Navigate to the project directory:

```bash
cd github-repository-searcher
```

Run the application on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The application will start at:

```text
http://localhost:8080
```

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## API Endpoints

### 1. Search GitHub Repositories

**Endpoint:**

```http
POST /api/github/search
```

**Full URL:**

```text
http://localhost:8080/api/github/search
```

**Request Body:**

```json
{
    "query": "spring boot",
    "language": "Java",
    "sort": "stars"
}
```

Supported sorting values:

- `stars`
- `forks`
- `updated`

**Sample Response:**

```json
{
    "message": "Repositories fetched and saved successfully",
    "repositories": [
        {
            "id": 123456,
            "name": "spring-boot-example",
            "description": "An example repository for Spring Boot",
            "owner": "user123",
            "language": "Java",
            "stars": 450,
            "forks": 120,
            "lastUpdated": "2024-01-01T12:00:00Z"
        }
    ]
}
```

### 2. Retrieve Stored Repositories

**Endpoint:**

```http
GET /api/github/repositories
```

**Full URL:**

```text
http://localhost:8080/api/github/repositories
```

Optional query parameters:

| Parameter | Description |
|---|---|
| language | Filter by programming language |
| minStars | Minimum repository star count |
| sort | Sort by stars, forks, or updated |

**Example Requests:**

Retrieve all stored repositories:

```http
GET /api/github/repositories
```

Filter by language:

```http
GET /api/github/repositories?language=Java
```

Filter by minimum stars:

```http
GET /api/github/repositories?minStars=100
```

Combine filters:

```http
GET /api/github/repositories?language=Java&minStars=100
```

Sort by forks:

```http
GET /api/github/repositories?sort=forks
```

Sort by last updated date:

```http
GET /api/github/repositories?sort=updated
```

The default sorting option is stars in descending order.

## Error Handling

The application handles:

- Invalid request parameters.
- Request validation errors.
- GitHub API rate limits.
- GitHub API HTTP errors.
- Invalid GitHub JSON responses.
- Empty search results.

Meaningful HTTP status codes and error messages are returned through centralized exception handling.

## Testing

The project uses JUnit 5 and Mockito for unit testing.

Run all tests:

```powershell
.\mvnw.cmd clean test
```

The current test suite covers:

- Retrieving all repositories.
- Filtering by programming language.
- Filtering by minimum stars.
- Sorting by forks.
- Sorting by last updated date.
- Invalid sorting values.
- Combined language and minimum-star filters.
- GitHub API response processing and repository saving.
- Empty GitHub API responses.
- Invalid GitHub JSON responses.

**Latest verified test result:** 11 tests, 0 failures, 0 errors.

## Author

Developed as a Backend Assignment using Java, Spring Boot, PostgreSQL, and the GitHub REST API.