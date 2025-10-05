# ChronoCritters: A Microservices-Based Battling Game

ChronoCritters is a full-stack, turn-based creature battling game built with a modern microservices architecture. It features user authentication, real-time matchmaking, and a dynamic battle arena where trainers can pit their teams of "Critters" against each other.

This document provides a comprehensive overview of the project's architecture, components, and instructions for setting it up and running it locally.

## Table of Contents
- [Architecture Overview](#architecture-overview)
- [Technology Stack](#technology-stack)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
  - [1. Clone the Repository](#1-clone-the-repository)
  - [2. Build Java Modules](#2-build-java-modules)
  - [3. Configure Frontend Environment](#3-configure-frontend-environment)
- [Running the Application](#running-the-application)
  - [1. Start the Database](#1-start-the-database)
  - [2. Run Backend Microservices](#2-run-backend-microservices)
  - [3. Run the Frontend Client](#3-run-the-frontend-client)
- [Service Breakdown](#service-breakdown)
  - [User Service](#user-service)
  - [Lobby Service](#lobby-service)
  - [GameLogic Service](#gamelogic-service)
  - [Client](#client)

---

## Architecture Overview

ChronoCritters is designed as a distributed system, with each microservice handling a distinct domain of the application. This separation of concerns enhances scalability and maintainability.

The services communicate through a combination of GraphQL, REST APIs, gRPC, and WebSockets.

**Application Flow:**
1.  A user registers or logs in via the **Client**. The request is handled by the **User Service** (GraphQL), which returns a JWT upon success.
2.  The **Client** establishes a WebSocket connection with the **Lobby Service** for real-time communication, authenticating with the JWT.
3.  The user joins the matchmaking queue. The **Lobby Service** pairs two players and initiates a battle by sending an HTTP request to the **GameLogic Service**.
4.  The **GameLogic Service** fetches detailed player and critter data from the **User Service** using high-performance gRPC calls.
5.  During the battle, player actions (like using an ability or switching a critter) are sent from the **Client** to the **GameLogic Service** via a REST API.
6.  After each turn, the **GameLogic Service** processes the logic, updates the battle state, and sends the new state to the **Lobby Service**.
7.  The **Lobby Service** broadcasts the updated battle state to the participating players over the WebSocket connection, ensuring both clients are synchronized in real-time.
8.  When the battle concludes, the results are processed, experience is awarded, and match history is saved via gRPC calls to the **User Service**.

  

---

## Technology Stack

The project utilizes a diverse and modern technology stack:

| Component         | Technologies                                                              |
| ----------------- | ------------------------------------------------------------------------- |
| **Backend**       | Java 21, Spring Boot 3, Maven                                             |
| **Frontend**      | React 19, TypeScript, Vite, TailwindCSS                                   |
| **Database**      | MongoDB                                                                   |
| **API & Comms**   | GraphQL, REST, gRPC, WebSockets (STOMP)                                   |
| **State Mgmt**    | Zustand (Client-side)                                                     |
| **Authentication**| JSON Web Tokens (JWT)                                                     |

---

## Features

- **User Authentication**: Secure user registration and login system.
- **Real-time Matchmaking**: Players are placed in a queue and automatically matched with an opponent.
- **Turn-Based Battle System**: A dynamic battle arena with abilities, type advantages, and status effects.
- **Live Battle Updates**: Real-time synchronization of battle state between players using WebSockets.
- **Player Profiles**: View battle statistics, match history, and manage your collection of Critters.
- **Data Persistence**: All user, critter, and match data is stored in a MongoDB database.

---

## Prerequisites

Before you begin, ensure you have the following software installed on your machine:
*   **Java (JDK) 21+**
*   **Apache Maven 3.8+**
*   **Node.js 18+** and **npm** (or yarn)
*   **Docker** and **Docker Compose** (for running MongoDB)
*   A Git client

---

## Getting Started

Follow these steps to get the project set up on your local machine.

### 1. Clone the Repository
```bash
git clone https://github.com/aspectfv/chronocritters
cd chronocritters
```

### 2. Build Java Modules
The project is a multi-module Maven project. You need to build it from the root directory. This compiles the code and installs the shared `proto` and `lib` modules into your local Maven repository, making them available to the other services.

```bash
mvn clean install
```

### 3. Configure Frontend Environment
Navigate to the `client` directory, create a `.env` file from the example, and install the necessary dependencies.

```bash
cd client
cp .env.example .env
npm install
```
The default values in the `.env` file are configured for local development and should work without changes if you follow the run instructions below.

---

## Running the Application

To run the full application, you need to start the database and each of the four microservices in separate terminal windows.

### 1. Set Up and Run MongoDB

The `user` service requires a running MongoDB instance. You can either install it directly on your machine or use a free cloud-hosted service like MongoDB Atlas.

#### Option A: Local Installation (Recommended)

1.  **Download MongoDB Community Server**: Go to the [official MongoDB website](https://www.mongodb.com/try/download/community) and download the version appropriate for your operating system (Windows, macOS, or Linux).

2.  **Install and Configure**: Follow the detailed installation instructions for your specific OS:
    *   [Installation Guide for Windows](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-windows/)
    *   [Installation Guide for macOS](https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-os-x/)
    *   [Installation Guide for Linux](https://www.mongodb.com/docs/manual/administration/install-on-linux/)

3.  **Run MongoDB**: After installation, you need to start the MongoDB service. The command may vary based on your OS and installation method (e.g., using `brew` on macOS or as a systemd service on Linux).

    A common way to start it manually is by running the `mongod` command in a new terminal:
    ```bash
    # This command starts the MongoDB daemon.
    # You may need to specify a data directory path if not configured.
    mongod
    ```
    Ensure the database is running and accessible on its default port, `27017`. The `user` service is pre-configured to connect to this address.

#### Option B: MongoDB Atlas (Cloud-Based Alternative)

If you prefer not to install software locally, you can use a free-tier database from MongoDB Atlas.

1.  **Sign Up**: Create a free account at [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register).
2.  **Create a Cluster**: Follow the on-screen instructions to create a free-tier cluster.
3.  **Get Connection String**: Once your cluster is ready, go to the "Connect" section, choose "Connect your application," and copy the connection string. It will look something like this:
    `mongodb+srv://<username>:<password>@clustername.mongodb.net/?retryWrites=true&w=majority`
4.  **Update Configuration**: Open the `application.properties` file in the user service:
    `user/src/main/resources/application.properties`
5.  Replace the existing `spring.data.mongodb.uri` with your new connection string. Remember to replace `<password>` with your actual database user password.
    ```properties
    spring.data.mongodb.uri=mongodb+srv://myuser:mypassword@mycluster.abcde.mongodb.net/chronocritters?retryWrites=true&w=majority
    ```

### 2. Run Backend Microservices

With your database running, open a separate terminal for each of the three backend services and run them from the project's **root directory**.

**Terminal 1: User Service**
```bash
# From the project's root directory
./user/mvnw spring-boot:run
```
*   Listens on HTTP Port: `8080` (for GraphQL)
*   Listens on gRPC Port: `9090`

**Terminal 2: Lobby Service**
```bash
# From the project's root directory
./lobby/mvnw spring-boot:run
```
*   Listens on HTTP Port: `8081` (for WebSocket connections)

**Terminal 3: GameLogic Service**
```bash
# From the project's root directory
./gamelogic/mvnw spring-boot:run
```
*   Listens on HTTP Port: `8082` (for battle actions)

At this point, all backend services are running. The `user` service will seed the database with initial data on its first successful startup.

### 3. Run the Frontend Client

Finally, in a new terminal, start the React client application.

**Terminal 4: Client**
```bash
# From the project's root directory
cd client
npm run dev
```
*   The Vite development server will typically start on `http://localhost:5173`.

You can now open two browser windows to `http://localhost:5173`, register two different users, and start a battle

---

## Service Breakdown

### User Service
*   **Purpose**: Manages all user and player-related data. Handles authentication, stores critter and ability definitions, and tracks player statistics and match history.
*   **Location**: `/user`
*   **Ports**: `8080` (GraphQL), `9090` (gRPC)
*   **Primary Consumers**:
    *   **Client**: For authentication and fetching profile/roster data.
    *   **GameLogic Service**: For fetching player data needed to initialize a battle.

### Lobby Service
*   **Purpose**: Manages real-time communication, including matchmaking and broadcasting battle state updates.
*   **Location**: `/lobby`
*   **Port**: `8081`
*   **Technology**: WebSockets (STOMP)
*   **Primary Consumers**:
    *   **Client**: Establishes a persistent connection for matchmaking and receiving live battle data.
    *   **GameLogic Service**: Receives battle state updates to be broadcast to clients.

### GameLogic Service
*   **Purpose**: Contains the core business logic for battles. It processes player actions (abilities, switches), calculates damage, applies status effects, and determines the battle's outcome.
*   **Location**: `/gamelogic`
*   **Port**: `8082`
*   **Primary Consumers**:
    *   **Client**: Sends requests to perform actions within a battle.
    *   **Lobby Service**: Triggers the start of a new battle.

### Client
*   **Purpose**: The user interface of the application. Provides a rich, interactive experience for logging in, finding matches, and participating in battles.
*   **Location**: `/client`
*   **Port**: `5173` (Vite development server)
*   **Technology**: React, Vite, TypeScript
*   **Connects to**:
    *   User Service (Port `8080`)
    *   Lobby Service (Port `8081`)
    *   GameLogic Service (Port `8082`)
