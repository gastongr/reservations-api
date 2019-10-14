# Reservations API for Volcano Island
Provides availability checking and reservation management for the island campsite.

## Running the application

Start the application using an in memory database
```
./mvnw spring-boot:run
```

Start the application using a MySQL database (update connection details on application-mysql.properties)
```
./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
```

For a complete list of operations please check the Swagger documentation available at http://localhost:8080/swagger-ui.html