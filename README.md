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
- [Running with Docker](#running-with-docker)
- [Deployment](#deployment)

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

- **User Authentication**: Secure user registration and login system. Every new
  trainer is given a starter team of three Critters so they can battle straight away.
- **Real-time Matchmaking**: Players are placed in a queue and automatically matched
  with an opponent, and can cancel a search at any time.
- **Turn-Based Battle System**: A dynamic battle arena with abilities, type advantages,
  status effects, and a per-turn timer.
- **Live Battle Updates**: Real-time synchronization of battle state between players
  using WebSockets.
- **Forfeit and Disconnect Handling**: A player can concede, and closing the tab
  mid-battle awards the win to their opponent instead of stalling the match.
- **Player Profiles**: View battle statistics, match history, and manage your collection
  of Critters.
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

There is no Maven wrapper at the repository root, so this step needs a system
Maven (3.8+). The per-service wrappers (`user/mvnw`, `lobby/mvnw`,
`gamelogic/mvnw`) are used for running the individual services.

To run the test suite:

```bash
mvn test
```

### 3. Configure Frontend Environment
Navigate to the `client` directory, create a `.env` file from the example, and install the necessary dependencies.

```bash
cd client
cp .env.example .env
npm install
```
The default values in the `.env` file are configured for local development and should work without changes if you follow the run instructions below.

### 4. Configure Service Secrets

The backend services read two shared secrets from the environment:

| Variable | Purpose |
| --- | --- |
| `JWT_SECRET` | HMAC key used to sign and verify JWTs. Must be at least 32 bytes and identical across all three services. |
| `SERVICE_TOKEN` | Shared token the services use to authenticate calls to each other. |

Both fall back to development defaults when unset, which is fine locally but must
never be relied on in a deployment — `docker compose` requires them explicitly.
The VS Code launch configurations load them from a root `.env` file.

---

## Running the Application

To run the full application, you need to start the database and each of the three backend services in separate terminal windows.

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
4.  **Update Configuration**: Set the `MONGODB_URI` environment variable to your
    connection string before starting the `user` service. Remember to replace
    `<password>` with your actual database user password.
    ```bash
    export MONGODB_URI="mongodb+srv://myuser:mypassword@mycluster.abcde.mongodb.net/chronocritters?retryWrites=true&w=majority"
    ```

### 2. Run Backend Microservices

With your database running, open a separate terminal for each of the three backend services and run them from the project's **root directory**.

**Terminal 1: User Service**
```bash
# From the project's root directory
./user/mvnw -f user/pom.xml spring-boot:run
```
*   Listens on HTTP Port: `8080` (for GraphQL)
*   Listens on gRPC Port: `9090`

**Terminal 2: Lobby Service**
```bash
# From the project's root directory
./lobby/mvnw -f lobby/pom.xml spring-boot:run
```
*   Listens on HTTP Port: `8081` (for WebSocket connections)

**Terminal 3: GameLogic Service**
```bash
# From the project's root directory
./gamelogic/mvnw -f gamelogic/pom.xml spring-boot:run
```
*   Listens on HTTP Port: `8082` (for battle actions)

At this point, all backend services are running. On every startup the `user`
service upserts the reference data — the effects, abilities and Critters the game
is built from — and creates two demo accounts if they do not already exist.
Registered accounts and their match history are never touched.

### 3. Run the Frontend Client

Finally, in a new terminal, start the React client application.

**Terminal 4: Client**
```bash
# From the project's root directory
cd client
npm run dev
```
*   The Vite development server will typically start on `http://localhost:5173`.

You can now open two browser windows to `http://localhost:5173`, register two
different users, and start a battle. Each new account is given a starter team of
three Critters automatically.

Two demo accounts are also seeded if you would rather not register:

| Username | Password |
| --- | --- |
| `BlueOak` | `password1` |
| `RedAsh` | `password2` |

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

---

## Running with Docker

Instead of the four-terminal workflow above, the entire stack — all three
services, MongoDB, and the client — can be started with a single command. This
mirrors exactly how the application runs in production.

```bash
cp .env.example .env
# set JWT_SECRET and SERVICE_TOKEN in .env
# generate each with: openssl rand -base64 48

docker compose up -d --build
```

The app is then served at `http://localhost` on port 80. A Caddy reverse proxy
serves the built client and routes `/graphql`, `/ws`, and `/battle/*` to the
appropriate service, so everything shares a single origin and no backend port is
exposed. Use `docker compose logs -f` to follow the services and
`docker compose down` to stop them.

Note that the client reads its API URLs from `VITE_*` environment variables and
falls back to same-origin paths when they are unset — which is what the
production image relies on. The `client/.env` file is only used by `npm run dev`.

## Deployment

See **[DEPLOYMENT.md](DEPLOYMENT.md)** for a step-by-step guide to deploying this
stack to a free Oracle Cloud VM, including firewall configuration, HTTPS via a
custom domain, backups, and the scaling constraints to be aware of.
