## Setup Instructions

### Prerequisites

- Java Development Kit (JDK) version 8 or higher
- Apache Maven
- PostgreSQL database installed and running

### Steps to Setup

1. **Clone the repository**

   ```
   git clone github.com/Zieszx/coding-exercise
   cd coding-exercise
   ```
   
2. **Set up PostgreSQL Database**

- Create a database named user_registration.
- Configure database connection settings in application.properties (found in src/main/resources):

  ```
  spring.datasource.url=jdbc:postgresql://localhost:5432/user_registration?createDatabaseIfNotExist=true
  spring.datasource.username=postgres
  spring.datasource.password=postgres
  ```

3. **Build and Run the Application**
   
   You can run the application using Maven:
   ```
   mvn spring-boot:run
   ```

Alternatively, you can build a JAR file and run it:
   ```
   mvn clean package
   java -jar target/user-registration-0.0.1-SNAPSHOT.jar
   ```

The application will start and be accessible at http://localhost:8080.
