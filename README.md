# HASTA Car Rental Application

The HASTA Car Rental Application is a web-based application designed to manage car rental services. It provides features such as customer registration, car reservation, maintenance management, and reporting.

## Table of Contents
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Usage](#usage)
- [Application Properties](#application-properties)
- [Dependencies](#dependencies)

## Features
- Customer Registration: Allows users to register as customers.
- Car Reservation: Enables users to reserve cars for rental.
- Maintenance Management: Provides tools for managing car maintenance.
- Reporting and Management: Generates reports and facilitates administrative tasks.

## Prerequisites
Before running the application, make sure you have the following installed:
- Java 17
- Maven 3.8+
- MySQL Database
- Xampp or any other application that enabled port opening for MySQL and Apache

## Installation
To install and set up the application, follow these steps:
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
    - Update the database configuration in `src/main/resources/application.properties`.

## Running the Application
To run the application, follow these steps:
1. Open and run port for MySQL and Apache
   #### Port
   - MySQL port: 3306
   - Apache port: 80
3. Build the project using Maven:
    ```sh
    mvn clean install
    ```
4. Run the application:
    ```sh
    mvn spring-boot:run
    ```

## Usage
Once the application is running, you can access it at `http://localhost:8080`. Use the customer registration module to add new customers and manage car reservations, maintenance, and reporting through the respective modules.

## Application Properties
The project uses the following application properties:
```properties
# Application name
spring.application.name=hams

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
```

## Dependencies
The project uses the following dependencies:
- Spring Boot Starter Data JPA
- Spring Boot Starter Web
- MySQL Connector/J
- Lombok
- Spring Boot Starter Test

