import java.sql.*;
import java.util.Scanner;

public class LibrarySystemWithDB {

    static final String DB_URL = "jdbc:sqlite:library.db";

    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC"); // Load driver
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                createTables(conn);
                runSystem(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute(
                "CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, author TEXT, available INTEGER)");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS borrowed (username TEXT, book_id INTEGER, days INTEGER)");
    }

    static void runSystem(Connection conn) throws SQLException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Library Menu ---");
            System.out.println("1. Admin Login");
            System.out.println("2. User Signup");
            System.out.println("3. User Login");
            System.out.println("4. Exit");
            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> adminLogin(conn, sc);
                case 2 -> userSignup(conn, sc);
                case 3 -> userLogin(conn, sc);
                case 4 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void adminLogin(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Admin username: ");
        String u = sc.nextLine();
        System.out.print("Admin password: ");
        String p = sc.nextLine();
        if (u.equals("admin") && p.equals("admin123")) {
            adminMenu(conn, sc);
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    static void adminMenu(Connection conn, Scanner sc) throws SQLException {
        while (true) {
            System.out.println("\n--- Admin Panel ---");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. View Inventory");
            System.out.println("4. Logout");
            System.out.print("Choose: ");
            int c = sc.nextInt();
            sc.nextLine();

            switch (c) {
                case 1 -> {
                    System.out.print("Title: ");
                    String title = sc.nextLine();
                    System.out.print("Author: ");
                    String author = sc.nextLine();
                    PreparedStatement ps = conn
                            .prepareStatement("INSERT INTO books(title, author, available) VALUES (?, ?, 1)");
                    ps.setString(1, title);
                    ps.setString(2, author);
                    ps.executeUpdate();
                    System.out.println("Book added.");
                }
                case 2 -> {
                    System.out.print("Book ID to remove: ");
                    int id = sc.nextInt();
                    PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id = ?");
                    ps.setInt(1, id);
                    int rows = ps.executeUpdate();
                    System.out.println(rows > 0 ? "Book removed." : "Book not found.");
                }
                case 3 -> showBooks(conn);
                case 4 -> {
                    return;
                }
                default -> System.out.println("Invalid.");
            }
        }
    }

    static void showBooks(Connection conn) throws SQLException {
    ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM books");
    System.out.println("\n--- Book Inventory ---");
    while (rs.next()) {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String author = rs.getString("author");
        int available = rs.getInt("available");
        if (available == 1) {
            System.out.println("ID: " + id +
                ", Title: " + title +
                ", Author: " + author +
                ", Available: Yes\n");
        } else {
            PreparedStatement ps = conn.prepareStatement("SELECT username FROM borrowed WHERE book_id = ?");
            ps.setInt(1, id);
            ResultSet brs = ps.executeQuery();
            String borrower = brs.next() ? brs.getString("username") : "Unknown";
            System.out.println("ID: " + id +
                ", Title: " + title +
                ", Author: " + author +
                ", Available: No (Borrowed by: " + borrower + ")\n");
        }
    }
}

    static void userSignup(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Choose username: ");
        String u = sc.nextLine();
        System.out.print("Choose password: ");
        String p = sc.nextLine();

        PreparedStatement check = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
        check.setString(1, u);
        ResultSet rs = check.executeQuery();
        if (rs.next()) {
            System.out.println("Username already exists.");
        } else {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO users VALUES (?, ?)");
            ps.setString(1, u);
            ps.setString(2, p);
            ps.executeUpdate();
            System.out.println("User registered.");
        }
    }

    static void userLogin(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Username: ");
        String u = sc.nextLine();
        System.out.print("Password: ");
        String p = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
        ps.setString(1, u);
        ps.setString(2, p);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            userMenu(conn, sc, u);
        } else {
            System.out.println("Login failed.");
        }
    }

    static void userMenu(Connection conn, Scanner sc, String user) throws SQLException {
        while (true) {
            System.out.println("\n--- User Menu (" + user + ") ---");
            System.out.println("1. View Available Books");
            System.out.println("2. Borrow Book");
            System.out.println("3. Return Book");
            System.out.println("4. My Borrowed Books");
            System.out.println("5. Logout");
            System.out.print("Choose: ");
            int c = sc.nextInt();
            sc.nextLine();

            switch (c) {
                case 1 -> showBooks(conn);
                case 2 -> borrowBook(conn, sc, user);
                case 3 -> returnBook(conn, sc, user);
                case 4 -> viewMyBooks(conn, user);
                case 5 -> {
                    return;
                }
                default -> System.out.println("Invalid.");
            }
        }
    }

    static void borrowBook(Connection conn, Scanner sc, String user) throws SQLException {
        System.out.print("Book ID to borrow: ");
        int id = sc.nextInt();
        System.out.print("Days to borrow: ");
        int days = sc.nextInt();

        PreparedStatement check = conn.prepareStatement("SELECT available FROM books WHERE id = ?");
        check.setInt(1, id);
        ResultSet rs = check.executeQuery();
        if (rs.next() && rs.getInt("available") == 1) {
            PreparedStatement update = conn.prepareStatement("UPDATE books SET available = 0 WHERE id = ?");
            update.setInt(1, id);
            update.executeUpdate();

            PreparedStatement addBorrow = conn.prepareStatement("INSERT INTO borrowed VALUES (?, ?, ?)");
            addBorrow.setString(1, user);
            addBorrow.setInt(2, id);
            addBorrow.setInt(3, days);
            addBorrow.executeUpdate();

            System.out.println("Book borrowed for " + days + " days.");
        } else {
            System.out.println("Book not available.");
        }
    }

    static void returnBook(Connection conn, Scanner sc, String user) throws SQLException {
        System.out.print("Book ID to return: ");
        int id = sc.nextInt();
        PreparedStatement del = conn.prepareStatement("DELETE FROM borrowed WHERE username = ? AND book_id = ?");
        del.setString(1, user);
        del.setInt(2, id);
        int rows = del.executeUpdate();
        if (rows > 0) {
            PreparedStatement update = conn.prepareStatement("UPDATE books SET available = 1 WHERE id = ?");
            update.setInt(1, id);
            update.executeUpdate();
            System.out.println("Book returned.");
        } else {
            System.out.println("You didn’t borrow this book.");
        }
    }

    static void viewMyBooks(Connection conn, String user) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM borrowed WHERE username = ?");
        ps.setString(1, user);
        ResultSet rs = ps.executeQuery();
        boolean found = false;
        while (rs.next()) {
            found = true;
            int bid = rs.getInt("book_id");
            int days = rs.getInt("days");
            PreparedStatement binfo = conn.prepareStatement("SELECT title FROM books WHERE id = ?");
            binfo.setInt(1, bid);
            ResultSet b = binfo.executeQuery();
            if (b.next()) {
                System.out.println("Book ID: " + bid + ", Title: " + b.getString("title") + ", Days: " + days);
            }
        }
        if (!found) {
            System.out.println("You didn't borrow books.");
        }
    }
}
