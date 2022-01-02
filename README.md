# Reservation Service using Spring Boot H2 Database CRUD: Building Rest API with Spring Data JPA

Authored by:
> [Peter Eng](https://www.linkedin.com/in/peter-eng-4029305/)

This application is used to create reservation for small island owned by upgrade.com.

Technology used in building this application includes:
Spring-boot, Swagger, Lombok, H2 database, JPA

The application include a rest client build with swagger code gen which I used to call the rest apis in my integration tests.

## Assumption I had to make:
I assume the check in and checkout times are incorrect and therefore changed them from 12am to 12pm.  If currect time is greater then 12pm then I add 2 days for start of checkin and also the date range to return.  If time is less then 12pm then I add 1 day.  I also assume a month is 30 days.

## Improvement I could have made.
I could have added a caching piece to hold the dates until the dates are processed and stored.

Add logging (did not add because of security concerns with log4j)


## Run Spring Boot application
```
mvn spring-boot:run
```

## Running integration tests
```
mvn '-Dtest=com.upgrade.reservation.api.*IT' test
```



