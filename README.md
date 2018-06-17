# Upday

This is the situation: We are a publishing company that created an app for reading news articles.
To serve data to the app we need a backend to provide RESTful APIs for the following use cases:

* allow an editor to create/update/delete an article
* display one article
* list all articles for a given author
* list all articles for a given period
* find all articles for a specific keyword

Each API should only return the data that is really needed to fulfill the use case.
An article usually consists of the following information:

* header
* short description
* text
* publish date
* author(s)
* keyword(s)

## Development

This project is built using the following tools:

1. JDK 1.8
2. [Spring Boot 2](https://spring.io/projects/spring-boot)
3. Spring Data JPA and Spring Data REST
4. [Maven](https://maven.apache.org/)
5. [Swagger](https://swagger.io/) (for API documentation; using Springfox)
6. [MapStruct](http://mapstruct.org/) for DAO/DTO mapping.

## Building and running the project

As Maven is used as the build system, run:

    ./mvnw clean install

To run the project, just run one of the following commands:

    java -jar target/news-0.0.1-SNAPSHOT.jar
    
    ./mvnw spring-boot:run

Then navigate to [http://localhost:8080](http://localhost:8080) in your favorite browser.

To view the API documentation or available REST endpoints, navigate to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Testing

To run the tests, use:

    ./mvnw clean test

## ToDo

1. Test coverage is not that great.
2. APIs are not secured, it would be nicer if we use Spring Security with JWT.
3. Domain Model doesn't include any business constraints at the moment (for example, unique constraints) and hence there is a possibility of duplication. But this can be solved while using a real database and handling the CRUD operations appropriately along with error handling.
4. Error/Exception handling is not that great.
5. No CI/CD or usage of Docker.