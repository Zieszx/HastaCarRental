# HASTA Car Rental Application

HASTA Car Rental Application is a web-based application designed to manage car rental services, including customer registration, reservations, maintenance, and reporting. A Web application for HASTA Car Rental that enables administrative personnel to administer the maintenance of the HASTA's vehicles and make reservations for specific vehicles.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Usage](#usage)

## Features
- Customer Registration
- Car Reservation
- Maintenance Management
- Reporting and Management

## Prerequisites
- Java 17
- Maven 3.6+
- MySQL Database

## Installation
1. Clone the repository:
    ```sh
    git clone https://github.com/Zieszx/HastaCarRental
    ```
2. Navigate to the project directory:
    ```sh
    cd HastaCarRental
    ```
3. Configure the MySQL database:
    - Create a database named `hastacarrental`.
    - Update the database configuration in `src/main/resources/application.properties`:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost/hastacarrental?createDatabaseIfNotExist=true
        spring.datasource.username=root
        spring.datasource.password=
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
        spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
        spring.jpa.show-sql=true
        spring.jpa.hibernate.ddl-auto=update
        ```

## Running the Application
1. Build the project using Maven:
    ```sh
    mvn clean install
    ```
2. Run the application:
    ```sh
    mvn spring-boot:run
    ```

## Usage
- Access the application at `http://localhost:8080`.
- Use the customer registration module to add new customers.
- Manage car reservations, maintenance, and reporting through the respective modules.

## Application Properties
The project uses the following application properties:
```properties
# Application name
spring.application.name=hams

# Thymeleaf settings
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML
spring.thymeleaf.encoding=UTF-8

# Static resources settings
spring.mvc.static-path-pattern=/**
spring.web.resources.static-locations=classpath:/static/

# Spring Boot DevTools settings
spring.devtools.restart.enabled=true
spring.devtools.livereload.enabled=true

# Database settings
spring.datasource.url=jdbc:mysql://localhost/hastacarrental?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

# File upload settings
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# MVC settings
spring.mvc.servlet.path: /

# Error handling settings
server.error.whitelabel.enabled=false
server.error.path=/error
```

## Dependencies
### The project uses the following dependencies:

- Spring Boot Starter Data JPA
- Spring Boot Starter Thymeleaf
- Spring Boot Starter Web
- Spring Boot DevTools
- MySQL Connector/J
- Lombok
- Spring Boot Starter Test
