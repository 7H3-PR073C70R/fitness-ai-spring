# Fitness Microservices Project

This repository contains a microservices-based fitness application built with Spring Boot, Spring Cloud, and supporting technologies. The system is designed to track user activities, generate AI-powered recommendations, and manage user data, all orchestrated via an API gateway and service discovery.

---

## Architecture Overview

- **activityservice**: Tracks and stores user fitness activities in MongoDB. Publishes activity events to RabbitMQ for AI processing.
- **aiservice**: Listens for activity events from RabbitMQ, generates recommendations using AI (Gemini API), and stores them.
- **userservice**: Manages user profiles and authentication, backed by PostgreSQL.
- **gateway**: API Gateway for routing, security (OAuth2/JWT), and CORS configuration.
- **configserver**: Centralized configuration management for all services.
- **eureka**: Service discovery using Netflix Eureka.

---

## Technologies Used

- Spring Boot, Spring Cloud (Gateway, Eureka, Config Server)
- MongoDB (activityservice)
- PostgreSQL (userservice, aiservice)
- RabbitMQ (event-driven communication)
- OpenAPI/Swagger (API documentation)
- OAuth2/JWT (security)
- Docker (optional for deployment)

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- MongoDB
- PostgreSQL
- RabbitMQ
- KeyCloak
- Docker (optional)

### Configuration

All service configurations are managed via the `config` directory:

- `activity-service.yml`
- `ai-service.yml`
- `user-service.yml`
- `api-gateway.yml`

Update database and RabbitMQ credentials as needed.

---

## Running the Services

Start the services in the following order:

1. **Eureka Server**
2. **Config Server**
3. **Gateway**
4. **User Service**
5. **Activity Service**
6. **AI Service**

---

## API Documentation

Swagger UI is available for each service:

- **Activity Service:** [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- **User Service:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **AI Service:** [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)
- **Gateway:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Event Flow

1. User tracks an activity via Activity Service.
2. Activity is saved to MongoDB and published to RabbitMQ.
3. AI Service consumes the event, generates a recommendation, and stores it.

---

## Contributing

1. Fork the repository.
2. Create your feature branch (`git checkout -b feature/YourFeature`).
3. Commit your changes.
4. Push to the branch.
5. Open a pull request.

---

## License

MIT License. See LICENSE for details.

---

For more details, see the configuration files and source code in each microservice directory.
