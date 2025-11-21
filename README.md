# NiagaNow - Modern E-Commerce System

## Overview

NiagaNow is a comprehensive e-commerce system designed to streamline the buying and selling process for users, sellers, and administrators. The system supports various functionalities such as user authentication, product browsing, order management, payment integration, and administrative tasks. It is built with modularity in mind, ensuring scalability and ease of maintenance.

---

## Features

### For Customers (Pelanggan)
- **Authentication**: Secure registration, login, and session management using JWT tokens.
- **Product Discovery**: Browse and search for products seamlessly.
- **Cart Management**: Add, modify, or remove items from the shopping cart.
- **Checkout Process**: Create orders by grouping items per store, validate stock and prices, calculate totals, and proceed to payment.
- **Order Tracking**: View order statuses and transaction history.
- **Product Reviews**: Submit reviews for purchased products.

### For Sellers (Penjual)
- **Store Management**: Create and configure your store.
- **Product Management**: Perform CRUD operations on products, including managing images and metadata.
- **Inventory Control**: Update stock levels and pricing.
- **Order Processing**: Manage orders by processing shipments and updating statuses.
- **Financial Insights**: Monitor store finances, view balances, and initiate withdrawals.

### For Managers (Manajer)
- **Product Oversight**: Manage product listings and inventory.
- **Order Supervision**: Oversee order fulfillment and status updates.

### For Admins (Admin / SuperAdmin)
- **User Management**: Handle user roles, permissions, and access control.
- **Transaction Oversight**: Reconcile transactions and manage system-wide financial records.
- **Session Management**: Revoke or inspect active JWT sessions.
- **System Tasks**: Automate recurring tasks like expiring unpaid transactions, reconciliation, and notifications.

### External Integrations
- **Payment Gateway**: Secure payment processing via external gateways.
- **Shipping Providers**: Integration with third-party shipping services for order fulfillment.

---

## Use Case Diagram

Below is a high-level use case diagram illustrating the interactions between actors and the system:

![Use Case Diagram](https://via.placeholder.com/800x600?text=Insert+Your+Diagram+Here)

*(Replace the placeholder with an actual image of your diagram when publishing the repository.)*

---

## Installation

### Prerequisites
- Node.js >= 16.x
- Python >= 3.8 (if backend services are implemented in Python)
- PostgreSQL or MySQL database
- Redis (for caching and session management)

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/NiagaNow.git
   cd NiagaNow
