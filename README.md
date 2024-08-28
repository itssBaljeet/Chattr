# Chattr

Chattr is a video chat web application built using Java 17 LTS, Spring Boot 3.3.3, and a range of modern web technologies including Thymeleaf, Spring Security, WebSocket, WebRTC, and REST APIs. The app enables real-time video communication, offering a simple, secure, and scalable platform for users to connect.

## Features:

- **Real-Time Video Chat**: Utilizes WebRTC for high-quality video and audio communication between users.

- **WebSocket Integration**: Implements WebSocket for real-time, full-duplex communication, ensuring fast and efficient message delivery.

- **Secure Authentication**: Integrated with Spring Security to provide user authentication and authorization.

- **Dynamic Web Pages**: Thymeleaf is used for server-side rendering of dynamic web content, enhancing user experience.

- **RESTful APIs**: Exposes REST APIs for user management and other core functionalities.

- **Responsive UI**: A clean and responsive user interface designed for a seamless experience across devices.

# Getting Started
## Prerequisites

Before you begin, ensure you have the following installed on your system:

    Java 17 LTS
    Maven 3.8+
    Git

## Installation

**Clone the repository**:

    git clone https://github.com/itssBaljeet/chattr.git
    cd chattr

**Build the project**:

Use Maven to compile the project and resolve dependencies:

    mvn clean install

## Run the application:

Start the Spring Boot application using Maven:

    mvn spring-boot:run

## Access the application:

Open your web browser and navigate to:

    http://localhost:8080

# Project Structure

The project follows a standard Spring Boot structure:


    chattr/
    ├── src/
    │   ├── main/
    │   │   ├── java/com/example/chattr/
    │   │   ├── resources/
    │   │   │   ├── static/
    │   │   │   ├── templates/
    │   │   │   └── application.properties
    │   └── test/
    ├── .gitignore
    ├── pom.xml
    └── README.md

    Java Code: All Java source code is under src/main/java/com/example/chattr/.
    Static Resources: CSS, JavaScript, and images are located in src/main/resources/static/.
    Thymeleaf Templates: HTML templates are in src/main/resources/templates/.
    Configuration: Application properties are managed in src/main/resources/application.properties.

# Technologies Used

- **Java 17 LTS**: The backbone of the application, providing modern language features.
- **Spring Boot 3.3.3**: Framework for building Java-based web applications.
- **Thymeleaf**: Template engine for dynamic web content.
- **Spring Security**: Provides authentication and access control.
- **WebSocket**: Enables real-time, full-duplex communication channels over a single TCP connection.
- **WebRTC**: Facilitates peer-to-peer audio, video, and data sharing between browsers.
- **REST APIs**: Implements RESTful services for client-server communication.
- **Maven**: Dependency management and build automation.


This project is licensed under the MIT License - see the LICENSE file for details.
Contact

For any inquiries or suggestions, feel free to reach out to me via noah.kendallh@gmail.com.
