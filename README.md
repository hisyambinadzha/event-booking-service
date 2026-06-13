# Event Booking Service

## 📌 Overview
The **Event Booking Service** is a backend application built with **Spring Boot** that provides user authentication, event booking, and administrative management features. It is designed to support an event management platform where users can register, log in, book events, and view their profiles, while administrators can manage bookings and users.

---

## ⚙️ Tech Stack
- **Java 21+**
- **Spring Boot** (REST API)
- **MongoDB** (data persistence)
- **Jakarta Validation** (input validation)
- **Custom Exception Handling**
- **ApiResponseBuilder** for standardized API responses

---

## 🚀 Features
- **[User Registration](ca://s?q=Explain_user_registration_flow)**: Create new accounts with validation.
- **[User Login](ca://s?q=Explain_user_login_flow)**: Authenticate users and return a session object (JWT Tokens).
- **[Profile Management](ca://s?q=Explain_profile_management)**: Fetch user details by email or ID.
- **[Event Booking](ca://s?q=Explain_event_booking_flow)**: Users can book seats for events with validation on availability and event status.
- **[Admin Bookings](ca://s?q=Explain_admin_booking_page)**: Admins can view all bookings in the system.

---

## 📂 API Endpoints

### 🔐 Authentication
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/register` | POST | Register a new user |
| `/api/auth/login` | POST | Authenticate user and return session |
| `/api/auth/profile/me?email={email}` | GET | Get user profile by email |
| `/api/auth/profile/{id}` | GET | Get user profile by ID |

### 🎭 Event Management
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/events/categories` | GET | Get all distinct event categories |
| `/api/events` | GET | Get all events (non-paginated) |
| `/api/events/page?page={page}&size={size}&category={category}` | GET | Get paginated events, optionally filtered by category |
| `/api/events/{id}` | GET | Get event details by ID |
| `/api/events` | POST (multipart/form-data) | Create a new event with image upload |
| `/api/events/{id}` | PUT (multipart/form-data) | Update an existing event |
| `/api/events/{id}` | DELETE | Delete an event by ID |

---

### 🎟️ Bookings
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/bookings` | POST | Create a new booking (requires Authorization header) |
| `/api/bookings/admin` | GET | Get all bookings (admin only) |
| `/api/bookings/user/{id}` | GET | Get bookings by user ID |

---

## 🧑‍💻 Context
This application is part of an **event management system** where:
- Users can register, log in, and book seats for events.
- Events have constraints such as **date validity**, **status (DRAFT/OPEN/CLOSED)**, and **seat availability**.
- Bookings are linked to both **users** and **events**, ensuring proper tracking.
- Admins have visibility into all bookings for monitoring and reporting.

---

## 📦 Models
### User
- `id` (read-only)
- `fullname`
- `email` (unique)
- `password` (write-only)
- `role` (USER/ADMIN)
- `createdAt`

### Event
- `id` (read-only)
- `title` (unique, indexed)
- `description`
- `category`
- `venue`
- `eventDate`
- `price`
- `capacity`
- `seatsAvailable`
- `status` (DRAFT/OPEN/CLOSED)
- `createdAt`
- `updatedAt`
- `image` (stored file path)
- `version` (optimistic locking)

### Booking
- `id` (read-only)
- `userId`
- `eventId`
- `numberOfSeats`
- `bookingDate`
- `totalPrice`
- `bookingStatus` (PENDING/APPROVED/etc.)

---

## 🛠️ Error Handling
The application uses a **GlobalExceptionHandler** with custom exceptions:
- `InternalServerException`
- `EventExpiredException`
- `EventClosedException`
- `EventFullException`
- `SeatsNotAvailableException`
- `EmailNotFoundException`

---

## 📖 Usage Flow (Create Booking)
1. **Register** → User creates an account.
2. **Login** → User authenticates and receives a session token.
3. **Book Event** → User selects an event, seats are validated, and booking is created.
4. **View Profile** → User retrieves their profile by email or ID.
5. **Admin Access** → Admin views all bookings for system oversight.

## 📖 Usage Flow (Create Event)
1. **Admin creates event** → Uploads metadata + image.
2. **Frontend fetches categories** → Populates filters.
3. **Frontend fetches paginated events** → Displays event list with category filter.
4. **User views event details** → Fetches event by ID.
5. **Admin updates/deletes event** → Maintains event lifecycle.

---

## 🔒 Security
- Authentication handled via **JWT tokens**.
- Authorization required for booking endpoints.
- Passwords stored securely with restricted access.
- Event creation, update, and deletion endpoints should be **restricted to admins**.
- Public endpoints (`GET`) are accessible to all users.
---

# 📖 Standard API Response

## 📌 Overview
The Event Booking Service uses a **standardized API response format** to ensure consistency across all endpoints.  
Every response—whether success or error—follows the same structure, making it predictable and easy for frontend clients to consume.

---

## 📂 Response Structure

### 1. **Header**
Contains metadata about the response.
- `responseId`: Unique UUID for tracking the response.
- `timestamp`: Time the response was generated (timezone: `Asia/Kuala_Lumpur`).

### 2. **Body**
Contains the actual response payload.
- `responseInfo`: Includes code and message.
- `data`: Optional, holds objects or lists (e.g., user, event, booking).
- `fields`: Optional, holds extra key-value pairs (e.g., error details).

### 3. **ResponseInfo**
Provides status information.
- `code`: Response code (e.g., `"000"` for success).
- `message`: Human-readable message from **ErrorCatalog**.

---

## ✅ Success Example
```json
{
  "response": {
    "header": {
      "responseId": "b7f9c6a2-1234-4d9a-9f8a-56789abcde",
      "timestamp": "2026-06-10T11:32:45.123+08:00"
    },
    "body": {
      "responseInfo": {
        "code": "000",
        "message": "Success."
      },
      "data": {
        "id": "user123",
        "fullname": "John Doe",
        "email": "john@example.com"
      }
    }
  }
}
```

## 📊 Error Codes Reference

| Code | Message |
| --- | --- |
| **000** | Success. |
| **100** | Validation failed. |
| **101** | The email address is already in use. |
| **102** | Incorrect email or password. |
| **103** | Email not registered. |
| **104** | Event does not exist. |
| **105** | Event already past. |
| **106** | Event not open for booking. |
| **107** | Event is full. |
| **108** | Not enough seats available. |
| **109** | Booking update not allowed. |
| **110** | Empty result returned. |
| **111** | User has already booked this event. |
| **112** | No booking records found. |
| **900** | Operation not allowed. |
| **901** | Unauthorized. |
| **902** | Forbidden. |
| **999** | Unexpected error occurred. |


## 📂 Seed Data

The `seed` directory contains sample data or seed data that can be used to populate your MongoDB database with initial data.

### 📌 Overview

The seed data is stored in the [seed/events.json](cci:7://file:///c:/Users/Admin/Documents/Certified%20Full%20Stack%20Java%20with%20AI/Projects/seed/events.json:0:0-0:0) file and consists of a JSON array of event objects. Each event object contains the following fields:

- `title`: The title of the event
- `description`: A brief description of the event
- `category`: The category of the event (e.g., `conference`, `concert`, `expo`)
- `venue`: The venue or location of the event
- `eventDate`: The date and time of the event
- `price`: The price of the event
- `capacity`: The maximum capacity of the event
- `seatsAvailable`: The number of available seats
- `status`: The status of the event (e.g., `DRAFT`, `OPEN`, `CLOSED`)
- `createdAt`: The timestamp when the event was created
- `image`: The URL of the event image

### 🎛️ Importing Seed Data

To import the seed data into your MongoDB database, you can use the following command:

```bash
./init-db-script.sh
```

## 📝 License
This project is for educational and development purposes. Extendable for production use with proper security and scalability enhancements.