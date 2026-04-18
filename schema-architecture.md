# Smart Clinic Management System Architecture

## 1. Architecture Summary

The Smart Clinic Management System is built using a three-tier architecture consisting of the Presentation Layer, Application Layer, and Data Layer. The Presentation Layer includes Thymeleaf-based web pages and REST API consumers that allow users such as admins, doctors, and patients to interact with the system.

The Application Layer is implemented using Spring Boot and contains MVC controllers for rendering HTML views and REST controllers for handling API requests. All business logic and validation are handled within the service layer, ensuring a clear separation of concerns.

The Data Layer uses MySQL for structured relational data such as patients, doctors, appointments, and admins, and MongoDB for flexible document-based data such as prescriptions. Spring Data JPA is used to interact with MySQL, while Spring Data MongoDB is used for MongoDB operations.

---

## 2. Numbered Flow of Data and Control

1. A user interacts with the system through a web page or REST API request.
2. The request is routed to the appropriate controller based on the request type.
3. MVC controllers handle HTML page rendering, while REST controllers handle JSON API responses.
4. The controller forwards the request to the service layer.
5. The service layer processes business logic and validation.
6. The service interacts with repositories to access MySQL (via JPA) or MongoDB (via Spring Data).
7. The response is returned through the controller as either an HTML page or JSON response.
