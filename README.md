# 🍔 Food Ordering Backend


A production-style **Food Ordering Backend REST API** built using **Spring Boot**. The application provides secure JWT-based authentication, restaurant and menu management, shopping cart functionality, order processing, role-based authorization, API validation, exception handling, and interactive Swagger/OpenAPI documentation.


---


## 🚀 Features


### 🔐 Authentication & Authorization


* User registration

* User login

* JWT-based authentication

* Password encryption using BCrypt

* Role-based authorization

* `USER` and `ADMIN` roles

* Protected REST endpoints

* Stateless Spring Security configuration


### 🏪 Restaurant Management


* Create restaurant

* Update restaurant

* Delete restaurant

* Get restaurant by ID

* Search and filter restaurants

* Pagination and sorting

* Filter by:


    * Keyword

    * City

    * State

    * Open/closed status

    * Minimum rating


### 📂 Menu Category Management


* Create menu category

* Update menu category

* Delete menu category

* Get category by ID

* Get categories by restaurant


### 🍕 Menu Item Management


* Create menu items

* Update menu items

* Delete menu items

* Get menu item by ID

* Search and filter menu items

* Pagination and sorting

* Filter by:


    * Restaurant

    * Category

    * Vegetarian/non-vegetarian

    * Availability

    * Price range

    * Keyword


### 🛒 Shopping Cart


* Add item to cart

* View cart

* Update cart item quantity

* Remove cart item

* Clear cart

* Automatic subtotal and item count handling


### 📦 Order Management


* Place order from cart

* View user's orders

* Get order details

* Filter orders by status

* Cancel pending orders

* Admin order management

* Update order status

* View all orders

* Order status management

* Payment method and payment status tracking


### 🛡️ Validation & Exception Handling


* DTO-based request validation

* Jakarta Bean Validation

* Global exception handling

* Custom exceptions

* Standardized error responses


### 📚 API Documentation


* Swagger UI

* OpenAPI documentation

* JWT authorization through Swagger

* Interactive API testing


### 🧪 Testing


* Service layer unit tests

* Controller layer tests

* Spring Security testing

* MockMvc-based API tests

* Validation testing

* Authorization testing


---


# 🛠️ Tech Stack


| Technology         | Purpose                        |

| ------------------ | ------------------------------ |

| Java 17            | Programming language           |

| Spring Boot 3.5.4  | Backend framework              |

| Spring Web         | REST APIs                      |

| Spring Data JPA    | Database access                |

| Spring Security    | Authentication & authorization |

| JWT                | Token-based authentication     |

| MySQL              | Relational database            |

| Hibernate          | ORM                            |

| Lombok             | Boilerplate reduction          |

| Jakarta Validation | Request validation             |

| Swagger / OpenAPI  | API documentation              |

| Maven              | Dependency management          |

| JUnit 5            | Testing                        |

| Mockito            | Mocking                        |

| MockMvc            | Controller testing             |

| Git                | Version control                |


---


# 🏗️ Project Architecture


The project follows a layered architecture:


```text

Controller

    ↓

Service

    ↓

Repository

    ↓

Database

```


Supporting layers:


```text

Controller

   ↓

DTO

   ↓

Service

   ↓

Mapper

   ↓

Entity

   ↓

Repository

   ↓

MySQL

```


Security flow:


```text

Client

   ↓

JWT Token

   ↓

JwtAuthFilter

   ↓

Spring Security

   ↓

Controller

   ↓

Service

```


---


# 📁 Project Structure


```text

src

├── main

│   └── java

│       └── com.foodapp.food_ordering_backend

│

│           ├── config

│           │   ├── SecurityConfig

│           │   ├── JwtAuthFilter

│           │   ├── JwtAuthenticationEntryPoint

│           │   └── OpenApiConfig

│           │

│           ├── controller

│           │   ├── AuthController

│           │   ├── UserController

│           │   ├── RestaurantController

│           │   ├── MenuCategoryController

│           │   ├── MenuItemController

│           │   ├── CartController

│           │   └── OrderController

│           │

│           ├── dto

│           │   ├── request

│           │   └── response

│           │

│           ├── entity

│           │   ├── User

│           │   ├── Restaurant

│           │   ├── MenuCategory

│           │   ├── MenuItem

│           │   ├── Cart

│           │   ├── CartItem

│           │   ├── Order

│           │   └── OrderItem

│           │

│           ├── exception

│           │   ├── GlobalExceptionHandler

│           │   ├── BadRequestException

│           │   ├── ResourceNotFoundException

│           │   └── UnauthorizedException

│           │

│           ├── mapper

│           │

│           ├── repository

│           │

│           ├── security

│           │   ├── JwtUtil

│           │   └── UserPrincipal

│           │

│           ├── service

│           │   └── impl

│           │

│           └── specification

│

└── test

    └── java

        └── com.foodapp.food_ordering_backend

            └── controller

```


---


# 🔑 Authentication Flow


The application uses JWT authentication.


### Registration


```text

POST /api/auth/register

```


The user provides registration details.


The backend:


1. Validates the request.

2. Checks whether email already exists.

3. Checks whether phone number already exists.

4. Encrypts the password using BCrypt.

5. Saves the user.

6. Generates a JWT.

7. Returns the authentication response.


### Login


```text

POST /api/auth/login

```


The backend:


1. Authenticates the credentials.

2. Loads the user.

3. Generates a JWT.

4. Returns the token.


Authenticated requests use:


```text

Authorization: Bearer <JWT_TOKEN>

```


---


# 👥 Roles


The application supports role-based authorization.


### USER


Users can:


* Browse restaurants

* Browse menu categories

* Browse menu items

* Manage their shopping cart

* Place orders

* View their orders

* Cancel eligible orders


### ADMIN


Administrators can additionally:


* Create/update/delete restaurants

* Create/update/delete menu categories

* Create/update/delete menu items

* View all orders

* Update order status

* Manage restaurant/menu data


---


# 📚 API Endpoints


## Authentication


| Method | Endpoint             | Access |

| ------ | -------------------- | ------ |

| POST   | `/api/auth/register` | Public |

| POST   | `/api/auth/login`    | Public |


## Restaurants


| Method | Endpoint                | Access        |

| ------ | ----------------------- | ------------- |

| POST   | `/api/restaurants`      | ADMIN         |

| GET    | `/api/restaurants`      | Authenticated |

| GET    | `/api/restaurants/{id}` | Authenticated |

| PUT    | `/api/restaurants/{id}` | ADMIN         |

| DELETE | `/api/restaurants/{id}` | ADMIN         |


## Menu Categories


| Method | Endpoint                                         | Access        |

| ------ | ------------------------------------------------ | ------------- |

| POST   | `/api/menu-categories`                           | ADMIN         |

| GET    | `/api/menu-categories/{id}`                      | Authenticated |

| GET    | `/api/menu-categories/restaurant/{restaurantId}` | Authenticated |

| PUT    | `/api/menu-categories/{id}`                      | ADMIN         |

| DELETE | `/api/menu-categories/{id}`                      | ADMIN         |


## Menu Items


| Method | Endpoint               | Access        |

| ------ | ---------------------- | ------------- |

| POST   | `/api/menu-items`      | ADMIN         |

| GET    | `/api/menu-items`      | Authenticated |

| GET    | `/api/menu-items/{id}` | Authenticated |

| PUT    | `/api/menu-items/{id}` | ADMIN         |

| DELETE | `/api/menu-items/{id}` | ADMIN         |


## Cart


| Method | Endpoint                       | Access        |

| ------ | ------------------------------ | ------------- |

| POST   | `/api/cart/items`              | Authenticated |

| GET    | `/api/cart`                    | Authenticated |

| PATCH  | `/api/cart/items/{cartItemId}` | Authenticated |

| DELETE | `/api/cart/items/{cartItemId}` | Authenticated |

| DELETE | `/api/cart/clear`              | Authenticated |


## Orders


| Method | Endpoint                       | Access        |

| ------ | ------------------------------ | ------------- |

| POST   | `/api/orders`                  | Authenticated |

| GET    | `/api/orders`                  | Authenticated |

| GET    | `/api/orders/{orderId}`        | Authenticated |

| PATCH  | `/api/orders/{orderId}/cancel` | Authenticated |


## Admin Orders


| Method | Endpoint                             | Access |

| ------ | ------------------------------------ | ------ |

| GET    | `/api/admin/orders`                  | ADMIN  |

| PATCH  | `/api/admin/orders/{orderId}/status` | ADMIN  |


---


# 🗄️ Database


The application uses **MySQL**.


Main entities include:


```text

User

Restaurant

MenuCategory

MenuItem

Cart

CartItem

Order

OrderItem

```


Entity relationships are managed using JPA/Hibernate.


Auditing is enabled using:


```java


import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing


```


Common audit fields include:


```text

createdAt

updatedAt

```


---


# ⚙️ Configuration


The application uses environment variables for sensitive configuration.


Example:


```text

DB_URL=jdbc:mysql://localhost:3306/food_app

DB_USERNAME=root

DB_PASSWORD=your_password


JWT_SECRET=your_secure_jwt_secret

JWT_EXPIRATION=86400000

```


Do not commit real database passwords or JWT secrets to GitHub.


---


# ▶️ How to Run the Project


## 1. Clone the repository


```bash

git clone <your-github-repository-url>

```


## 2. Open the project


Open the project in IntelliJ IDEA or another Java IDE.


## 3. Configure MySQL


Create the database:


```sql

CREATE DATABASE food_app;

```


Configure the required environment variables.


## 4. Build the project


```bash

mvn clean install

```


## 5. Run the application


```bash

mvn spring-boot:run

```


Or run:


```text

FoodOrderingBackendApplication

```


from IntelliJ IDEA.


The application runs on:


```text

http://localhost:8081

```


---


# 📖 Swagger / OpenAPI


Once the application is running, open:


```text

http://localhost:8081/swagger-ui/index.html

```


Swagger provides interactive documentation for all REST APIs.


For protected endpoints:


1. Register or login.

2. Copy the JWT token.

3. Click **Authorize**.

4. Enter:


```text

Bearer <your-jwt-token>

```


5. Click **Authorize**.

6. Execute protected APIs.


---


# 🧪 Testing


The project contains automated tests for the application layers.


### Service Testing


Service tests verify:


* Business logic

* Repository interactions

* Validation of business rules

* Exception scenarios

* Successful operations


### Controller Testing


Controller tests verify:


* HTTP status codes

* Request validation

* JSON responses

* Authorization

* MockMvc request handling

* Service interaction


The test suite can be executed using:


```bash

mvn test

```


---


# 🔒 Security


Security is implemented using:


* Spring Security

* JWT

* BCrypt password hashing

* Stateless sessions

* Role-based authorization

* Method-level security


Public endpoints include:


```text

/api/auth/**

/swagger-ui/**

/v3/api-docs/**

```


Other protected endpoints require authentication.


---


# 🧩 Error Handling


The application uses a centralized exception handling mechanism through:


```text

GlobalExceptionHandler

```


Handled scenarios include:


* Resource not found

* Bad requests

* Unauthorized requests

* Validation failures

* General server errors


Example response:


```json

{

  "timestamp": "2026-08-08T15:00:00",

  "status": 404,

  "error": "Not Found",

  "message": "Restaurant not found"

}

```


---


# 🔄 Order Flow


The main customer order flow is:


```text

Register

   ↓

Login

   ↓

Receive JWT

   ↓

Browse Restaurants

   ↓

Browse Menu

   ↓

Add Items to Cart

   ↓

Review Cart

   ↓

Place Order

   ↓

Order Created

   ↓

Admin Updates Order Status

   ↓

Customer Tracks Order

```


---


# 📌 Future Improvements


Potential future enhancements include:


* Payment gateway integration

* Email/SMS notifications

* Restaurant owner role

* Delivery partner module

* Order tracking

* Redis caching

* Docker deployment

* CI/CD pipeline

* Cloud deployment

* Automated API integration tests

* Refresh token mechanism

* Rate limiting

* Production monitoring and logging


---


# 👨‍💻 Author


**Shriyanshu Yadav**


Backend Developer | Java | Spring Boot | REST APIs | SQL | DSA


---


# ⭐ Project Highlights


This project demonstrates practical backend development concepts including:


* RESTful API development

* Layered architecture

* Spring Boot

* Spring Security

* JWT authentication

* Role-based authorization

* JPA/Hibernate

* MySQL

* DTO pattern

* Mapper pattern

* Bean validation

* Global exception handling

* Pagination and sorting

* Dynamic filtering using JPA Specifications

* Unit testing

* Controller testing

* Swagger/OpenAPI documentation


If you find this project useful, consider giving the repository a ⭐.
