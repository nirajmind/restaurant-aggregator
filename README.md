# **🚀 High-Performance Restaurant Aggregator**

A production-grade, horizontally scalable backend system designed for high-volume food delivery platforms. Built with **Spring Boot 3**, **Docker**, and **Nginx**.

## **🏗️ System Architecture**

This project demonstrates a **Hexagonal Architecture** focusing on resilience, security, and low latency.

graph TD  
User\[Client / Postman\] \--\>|Port 80| Nginx\[🌐 Nginx Load Balancer\]  
Nginx \--\>|Round Robin| App1\[🚀 App Replica 1\]  
Nginx \--\>|Round Robin| App2\[🚀 App Replica 2\]  
Nginx \--\>|Round Robin| App3\[🚀 App Replica 3\]

    subgraph Cluster \[Docker Network\]  
        App1 & App2 & App3 \--\>|Read/Write| DB\[(🗄️ Postgres)\]  
        App1 & App2 & App3 \--\>|Cache| Redis\[(🔴 Redis)\]  
    end

### **Key Engineering Concepts Implemented**

| Concept | Implementation Details | Architecture Goal |
| :---- | :---- | :---- |
| **Horizontal Scaling** | Nginx Load Balancer \+ 3 Spring Boot Replicas | Handling High Concurrency |
| **Security** | Stateless JWT \+ RBAC (Role-Based Access Control) | Zero-Trust / Scalable Auth |
| **Fault Tolerance** | Resilience4j Circuit Breaker on External APIs | Preventing Cascade Failures |
| **Performance** | Redis Caching (L2) \+ Async Thread Pools | Low Latency (\<50ms reads) |
| **Data Integrity** | Deduplication Service \+ ACID Transactions | Idempotent Data Ingestion |

## **🛠️ Tech Stack**

* **Core:** Java 17, Spring Boot 3.4
* **Data:** PostgreSQL, Redis (Lettuce/Redisson)
* **DevOps:** Docker, Docker Compose, Nginx
* **Security:** Spring Security, JWT (JJwt)
* **Testing:** JUnit 5, Mockito, Testcontainers

## **🚀 Quick Start (The "Clean Slate" Protocol)**

**Prerequisites:** Docker & Maven installed.

### **1\. Build & Launch Cluster**

Run this single command to clean old state, rebuild the JAR, and launch the 3-node cluster.

\# Windows PowerShell  
mvn clean package \-DskipTests; docker-compose down; cls; docker-compose up \-d \--build \--scale app=3

### **2\. Verify Deployment**

Check the logs to ensure all 3 replicas started and connected to Redis/Postgres.

docker-compose logs \-f app

## **🧪 Testing the API**

**Base URL:** http://localhost (Managed by Nginx on Port 80\)

### **1\. Authentication Flow**

* **Register (Create User):**
    * POST http://localhost/auth/register
    * Body: {"username": "admin", "password": "password"}
* **Login (Get Token):**
    * POST http://localhost/auth/login
    * Response: {"token": "eyJhbGci..."}
    * *⚠️ Copy this token for subsequent requests.*

### **2\. High-Performance Read (Redis Cache)**

* **Get Restaurants:**
    * GET http://localhost/api/v1/restaurants/1
    * Header: Authorization: Bearer \<TOKEN\>
* **Observe:** First request takes \~50ms (DB Hit). Second request takes \~5ms (Redis Hit).

### **3\. Resilient Ingestion (Circuit Breaker)**

* **Trigger Sync:**
    * POST http://localhost/api/v1/ingestion/sync
* **Observe Logs:** You will see "ASYNC START" immediately (Non-blocking), followed by "ATTEMPT" or "FALLBACK" logs from the background thread pool.

## **📂 Project Structure**

restaurant-aggregator/  
├─ docker/  
│  ├─ docker-compose.yaml  \# Infrastructure Definition  
│  ├─ nginx.conf           \# Load Balancer Config  
│  └─ Dockerfile           \# Multi-stage Java Build  
├─ src/main/java/com/niraj/restaurant/  
│  ├─ config/              \# Async, OpenApi, Security Configs  
│  ├─ domain/              \# JPA Entities (Rich Domain Model)  
│  ├─ ingestion/           \# Hexagonal Ports & Adapters (Circuit Breakers)  
│  ├─ repository/          \# Spring Data JPA Repositories  
│  ├─ security/            \# JWT Filters & UserDetails  
│  ├─ service/             \# Business Logic (@Transactional)  
│  └─ web/                 \# REST Controllers  
└─ pom.xml                 \# Dependencies

## **👨‍💻 Maintainer Notes (Interview Cheat Sheet)**

* **Why Stateless JWT?** To allow round-robin load balancing without sticky sessions.
* **Why Async Ingestion?** To prevent HTTP thread starvation during long-running 3rd party API calls.
* **Why DTOs?** To decouple the internal Database Schema (User) from the external API Contract (LoginRequest).