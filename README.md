# 🛒 EasyShop — Capstone 3 (E-Commerce API)

## Overview
EasyShop is a RESTful e-commerce application built for **Capstone 3**. The focus is the **Java Spring Boot backend API** (auth, products, cart, checkout). A **provided frontend** is used only for testing/demo.

---

## Tech Stack

### Backend
- Java
- Spring Boot
- Spring Security (JWT)
- MySQL
- JDBC / DAO pattern

### Frontend (Provided)
- HTML / CSS / JavaScript (capstone client)
- Not custom-built or modified (used only for testing)

### Testing
- Insomnia (provided collections)

---

## Features Implemented

### Authentication
- `POST /register`
- `POST /login` (JWT)

### Profile
- `GET /profile`
- `PUT /profile`

### Categories
- `GET /categories`
- Admin:
  - `POST /categories`
  - `DELETE /categories/{id}`

### Products
- `GET /products`
- Search / filter supported

### Cart
- `POST /cart/products/{id}`
- `GET /cart`
- `DELETE /cart`

### Checkout (Final Required Feature)
- `POST /orders`
  - Inserts into `orders`
  - Inserts into `order_line_items`
  - Clears cart after checkout
  - Transaction ensures consistency
- `GET /cart` after checkout returns empty

> **Note:** Order history (`GET /orders`) is **not required** by the Capstone 3 spec and is not implemented.

---

## API Testing (Insomnia)
Two collections are included (per course docs):
- **capstone 3 api** (required endpoints)
- **capstone 3 api (optional)** (advanced / extra tests)

For demo: required collection is used for core endpoints; checkout/cart may be shown from the optional collection.

---

## Frontend Notes
- Frontend is provided by the course and is used to test the API.
- Known frontend issues are considered optional to fix per the documentation.
- No frontend modifications were required for this capstone.

---

## Running the Project

### Backend
1. Open `capstone-api-starter` in IntelliJ
2. Configure MySQL connection in `application.properties`
3. Run `EasyshopApplication`
4. API runs at:
   - `http://localhost:8080`

### Frontend
1. Open the provided client
2. Run/serve `index.html`
3. Login and use the UI to browse products/cart (API is the main deliverable)

---

## Author
**Thomas Shumet**
