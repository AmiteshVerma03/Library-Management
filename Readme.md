# 📚 Library Management System (Java + SQLite)

A console-based Library Management System built in Java, using SQLite as the database. It allows users to sign up, log in, borrow/return books, and allows an admin to manage the library inventory.

---

## 🚀 Features

- ✅ **Admin authentication**
- ✅ **Add, remove, and view books**
- ✅ **User signup and login**
- ✅ **Borrow and return books**
- ✅ **Track borrowed books per user**
- ✅ **SQLite-backed persistent storage**

---

## 🛠️ Technologies Used

- Java (JDK 8+)
- SQLite (via `sqlite-jdbc-3.50.2.0.jar`)
- JDBC for database connectivity

---

## 📦 Folder Structure

```
LibrarySystem/
├── LibrarySystemWithDB.java
├── sqlite-jdbc-3.50.2.0.jar
├── library.db (created automatically at runtime)
└── README.md
```

---

## 🖥️ How to Run

### ✅ Step 1: Prerequisites

- Java installed (`java -version`)
- SQLite JDBC driver: `sqlite-jdbc-3.50.2.0.jar`

### ✅ Step 2: Compile

Open terminal/command prompt in the project folder and run:

**Windows:**
```sh
javac -cp ".;sqlite-jdbc-3.50.2.0.jar" LibrarySystemWithDB.java
```

**Mac/Linux:**
```sh
javac -cp ".:sqlite-jdbc-3.50.2.0.jar" LibrarySystemWithDB.java
```

### ✅ Step 3: Run

**Windows:**
```sh
java -cp ".;sqlite-jdbc-3.50.2.0.jar" LibrarySystemWithDB
```

**Mac/Linux:**
```sh
java -cp ".:sqlite-jdbc-3.50.2.0.jar" LibrarySystemWithDB
```

---

## 👤 Admin Credentials

| Username | Password  |
|----------|-----------|
| admin    | admin123  |

---

## 📌 Functional Menu Overview

### Admin Panel

- Add a new book
- Remove a book by ID
- View all books in inventory

### User Menu

- View available books
- Borrow books
- Return borrowed books
- View user’s borrowed books

---

## 🗃️ Database Schema

```sql
CREATE TABLE books (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT,
    author TEXT,
    available INTEGER
);

CREATE TABLE users (
    username TEXT PRIMARY KEY,
    password TEXT
);

CREATE TABLE borrowed (
    username TEXT,
    book_id INTEGER,
    days INTEGER
);
```

---

## 🔒 Security Note

For simplicity, passwords are stored in plaintext.  
You can upgrade it using hashing (e.g., SHA-256) for better security.

---

## 📈 Future Enhancements (Optional Ideas)

- GUI using JavaFX or Swing
- Due date reminders and fines
- Search and filter books
- Pagination for large inventories
- Export reports (PDF/CSV)

---