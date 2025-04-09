# **LabOrganized**

A backend system for laboratory inventory management, built with **Spring Boot**, **MySQL**, and secured with **Spring Security & JWT authentication**.

## **Table of Contents**

- [About The Project](#about-the-project)
- [Built With](#built-with)
- [Getting Started](#getting-started)
   - [Prerequisites](#prerequisites)
   - [Installation](#installation)
- [Usage](#usage)
- [Contact](#contact)

---

## **About The Project**

LabOrganized is a backend API designed for laboratories to manage their reagent inventory efficiently.

### **Key Features**
✅ User authentication with JWT tokens  
✅ Role-based authorization (ADMIN & USER)  
✅ CRUD operations for reagent stock management  
✅ Secured endpoints with Spring Security  
✅ Admin-exclusive endpoints for user management

---

## **Built With**

- ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
- ![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=flat-square&logo=Spring&logoColor=white)
- ![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

---

## **Getting Started**

To set up and run LabOrganized locally, follow these steps.

### **Prerequisites**
- Install **[Java 21](https://www.oracle.com/java/technologies/downloads/#java21)**
- Install **[OpenSSL](https://openssl-library.org/)**
- Install **[Docker](https://docs.docker.com/get-started/get-docker/)**

### **Installation**

1️⃣ Clone the repository
```sh
git clone https://github.com/martiyen/laborganized.git
cd laborganized
```

2️⃣ Navigate to `src/main/resources/` and create a `certs` folder
```sh
mkdir -p src/main/resources/certs
```

3️⃣ Generate an RSA key pair for JWT authentication
```sh
# Generate RSA key pair
openssl genrsa -out keypair.pem 2048

# Extract public key
openssl rsa -in keypair.pem -pubout -out public.pem

# Convert private key to PKCS#8 format
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt -in keypair.pem -out private.pem
```

4️⃣ Remove the unnecessary `keypair.pem` file
```sh
rm keypair.pem
```

---

## **Usage**

1️⃣ **Start the Application**  
Run the project from your preferred IDE or execute:
```sh
./mvnw spring-boot:run
```
By default, the server runs at **[http://localhost:8080](http://localhost:8080)**.

2️⃣ **User Authentication**
- Register a new account via `POST /register`
- Obtain a JWT token by sending a `POST` request to `/token` with Basic Authentication

3️⃣ **Default Test Users** (Preloaded in the database)  

| Username | Password | Role |
|----------|---------|------|
| `admin` | `password` | ADMIN |
| `jdoe`  | `password` | USER  |
| `asmith` | `password` | USER  |

4️⃣ **Using the API**
- All endpoints (except `/register`) require authentication
- Include the JWT token as a Bearer Token in requests
- Admin-only actions require ROLE_ADMIN authorization

📌 Example: Login & Retrieve JWT Token
```sh
curl -X POST "http://localhost:8080/token" -u "jdoe:password"
```
This returns a token, which should be used in all further requests:
```sh
curl -X GET "http://localhost:8080/api/v1/reagents" -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

## **Contact**
📧 **Martin Doyen** - doyenmartin@gmail.com

---
