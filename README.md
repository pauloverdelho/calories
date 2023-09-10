# Getting Started


The example repository contains 3 sub-modules
- product service, to register products and their calories
- order service to register user meals, with one main course and one beverage and the total calories. This service only register snapshots of the meals, isolated from the products in the database.
- calorie counter is a web app, implemented use the Vaadin platform, that allows the user to register products and meals based on the existing products

Unfortunately there was not enough time to implement a user service, either to have a user database or even for authentication/authorization.
CI/CD was also not implemented.

Some integration tests were implemented for the product service and order service using JUnit5.

Docker is required to run the code challenge services.
Maven and Java 17 are also requirements.

## Executing and testing the code


In order to run the service, please execute the following commands on the terminal, on the base folder of the project

#### mvn clean package -Pproduction
#### docker compose up --build


After this, you can open the web application on

* [Web application](http://localhost:8080/)


It's also possible to test the product service api using swagger on

* [Product service swagger](http://localhost:8081/swagger-ui/index.html)

and the order service on

* [Order service swagger](http://localhost:8082/swagger-ui/index.html)


To stop all services in the end just run the command on the terminal

#### docker compose down