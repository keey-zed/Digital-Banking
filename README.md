# Bank Account Management Application

This project aims to create an application for managing bank accounts. Each account belongs to a customer and can undergo various types of operations, such as DEBIT or CREDIT. There are two types of accounts: Current Accounts and Savings Accounts.

## Part 1: DAO Layer

### 1. Project Setup
- Create a Spring Boot project.

### 2. JPA Entities
- Create the following JPA entities:
    - Customer
    - BankAccount
    - Saving Account
    - Current Account
    - AccountOperation

### 3. JPA Repositories
- Create JPA repositories/interfaces using Spring Data for each entity.

### 4. DAO Testing
- Implement tests to ensure the functionality of the DAO layer.

## Part 2: Services, DTOs, and Mappers

- Develop the service layer for managing bank accounts.
- Create Data Transfer Objects (DTOs) for efficient data transfer.
- Implement mappers to transform entities into DTOs and vice versa.

## Part 3: Web Layer (RestControllers)

- Build RestControllers to expose APIs for managing customer accounts.
- Define endpoints for creating, updating, and retrieving account information.

## Part 4: Angular Frontend

- Develop the frontend using Angular to provide a user interface for customers to interact with their accounts.

## Part 5: Security with Spring Security and JWT

- Implement security measures using Spring Security and JWT for authentication and authorization.
- Ensure that the application is secure and only accessible by authenticated users.

[Demo.webm](https://github.com/khawla-dev/Digital-Banking/assets/63119273/50bbc73d-d735-4d1b-b88a-e4d28cf96e69)
