import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class LibraryServer {

    public static void main(String[] args) throws Exception {

        int port = 8080;

        String envPort = System.getenv("PORT");

        if (envPort != null) {
            port = Integer.parseInt(envPort);
        }

        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(port),
                        0
                );

        server.createContext("/", LibraryServer::home);

        server.createContext(
                "/student.html",
                LibraryServer::studentPage
        );

        server.createContext(
                "/librarian.html",
                LibraryServer::librarianPage
        );

        server.createContext(
                "/style1.css",
                LibraryServer::style
        );

        server.createContext(
                "/addBook",
                LibraryServer::addBook
        );

        server.createContext(
                "/searchBook",
                LibraryServer::searchBook
        );

        server.createContext(
                "/getBooks",
                LibraryServer::getBooks
        );

        server.createContext(
                "/deleteBook",
                LibraryServer::deleteBook
        );

        server.createContext(
                "/addMember",
                LibraryServer::addMember
        );

        // NEW: Student Registration
        server.createContext(
                "/registerMember",
                LibraryServer::registerMember
        );

        server.createContext(
                "/issueBook",
                LibraryServer::issueBook
        );

        server.createContext(
                "/returnBook",
                LibraryServer::returnBook
        );

        server.createContext(
                "/requestBook",
                LibraryServer::requestBook
        );

        server.createContext(
                "/getRequests",
                LibraryServer::getRequests
        );

        server.createContext(
                "/updateRequest",
                LibraryServer::updateRequest
        );

        server.start();

        System.out.println(
                "Library Management System"
        );

        System.out.println(
                "Server started successfully"
        );

        System.out.println(
                "Open: http://localhost:" + port
        );
    }


    // =========================
    // HOME PAGE
    // =========================

    private static void home(
            HttpExchange exchange) throws IOException {

        sendFile(
                exchange,
                "index.html",
                "text/html"
        );
    }


    // =========================
    // STUDENT PAGE
    // =========================

    private static void studentPage(
            HttpExchange exchange) throws IOException {

        sendFile(
                exchange,
                "student.html",
                "text/html"
        );
    }


    // =========================
    // LIBRARIAN PAGE
    // =========================

    private static void librarianPage(
            HttpExchange exchange) throws IOException {

        sendFile(
                exchange,
                "librarian.html",
                "text/html"
        );
    }


    // =========================
    // CSS
    // =========================

    private static void style(
            HttpExchange exchange) throws IOException {

        sendFile(
                exchange,
                "style1.css",
                "text/css"
        );
    }


    // =========================
    // ADD BOOK
    // =========================

    private static void addBook(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String title = data.get("title");
        String author = data.get("author");
        String category = data.get("category");

        if (title == null ||
                author == null ||
                category == null ||
                title.isEmpty() ||
                author.isEmpty() ||
                category.isEmpty()) {

            sendResponse(
                    exchange,
                    "Please fill all fields",
                    "text/plain"
            );

            return;
        }

        try (Connection con =
                     DBConnection.getConnection()) {

            String sql =
                    "INSERT INTO books " +
                    "(title, author, category, status) " +
                    "VALUES (?, ?, ?, 'Available')";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, title);
            ps.setString(2, author);
            ps.setString(3, category);

            ps.executeUpdate();

            sendResponse(
                    exchange,
                    "Book added successfully!",
                    "text/plain"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error adding book: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // SEARCH BOOK
    // =========================

    private static void searchBook(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String bookName =
                data.get("bookName");

        if (bookName == null ||
                bookName.isEmpty()) {

            sendResponse(
                    exchange,
                    "Please enter book name",
                    "text/plain"
            );

            return;
        }

        try (Connection con =
                     DBConnection.getConnection()) {

            String sql =
                    "SELECT title, author, category, status " +
                    "FROM books " +
                    "WHERE title LIKE ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(
                    1,
                    "%" + bookName + "%"
            );

            ResultSet rs =
                    ps.executeQuery();

            StringBuilder result =
                    new StringBuilder();

            while (rs.next()) {

                result.append(
                        rs.getString("title")
                );

                result.append(" | ");

                result.append(
                        rs.getString("author")
                );

                result.append(" | ");

                result.append(
                        rs.getString("category")
                );

                result.append(" | ");

                result.append(
                        rs.getString("status")
                );

                result.append("\n");
            }

            if (result.length() == 0) {

                result.append(
                        "Book not found."
                );
            }

            sendResponse(
                    exchange,
                    result.toString(),
                    "text/plain"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error searching book: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // GET BOOKS
    // =========================

    private static void getBooks(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        try (Connection con =
                     DBConnection.getConnection()) {

            String sql =
                    "SELECT title, author, category, status " +
                    "FROM books";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            StringBuilder json =
                    new StringBuilder();

            json.append("[");

            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                json.append("{");

                json.append("\"title\":\"")
                        .append(
                                escapeJson(
                                        rs.getString("title")
                                )
                        )
                        .append("\",");

                json.append("\"author\":\"")
                        .append(
                                escapeJson(
                                        rs.getString("author")
                                )
                        )
                        .append("\",");

                json.append("\"category\":\"")
                        .append(
                                escapeJson(
                                        rs.getString("category")
                                )
                        )
                        .append("\",");

                json.append("\"status\":\"")
                        .append(
                                escapeJson(
                                        rs.getString("status")
                                )
                        )
                        .append("\"");

                json.append("}");
            }

            json.append("]");

            sendResponse(
                    exchange,
                    json.toString(),
                    "application/json"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error getting books: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // DELETE BOOK
    // =========================

    private static void deleteBook(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String bookName =
                data.get("bookName");

        if (bookName == null ||
                bookName.isEmpty()) {

            sendResponse(
                    exchange,
                    "Please enter book name",
                    "text/plain"
            );

            return;
        }

        try (Connection con =
                     DBConnection.getConnection()) {

            String sql =
                    "DELETE FROM books WHERE title = ?";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, bookName);

            int rows =
                    ps.executeUpdate();

            if (rows > 0) {

                sendResponse(
                        exchange,
                        "Book deleted successfully!",
                        "text/plain"
                );

            } else {

                sendResponse(
                        exchange,
                        "Book not found.",
                        "text/plain"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error deleting book: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // ADD MEMBER - LIBRARIAN
    // =========================

    private static void addMember(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String memberName =
                data.get("memberName");

        String department =
                data.get("department");

        if (memberName == null ||
                department == null ||
                memberName.isEmpty() ||
                department.isEmpty()) {

            sendResponse(
                    exchange,
                    "Please fill all fields",
                    "text/plain"
            );

            return;
        }

        try (Connection con =
                     DBConnection.getConnection()) {

            String sql =
                    "INSERT INTO members " +
                    "(member_name, department) " +
                    "VALUES (?, ?)";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, memberName);
            ps.setString(2, department);

            ps.executeUpdate();

            sendResponse(
                    exchange,
                    "Member added successfully!",
                    "text/plain"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error adding member: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // REGISTER MEMBER - STUDENT
    // =========================

    private static void registerMember(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String studentName =
                data.get("studentName");

        String studentId =
                data.get("studentId");

        String department =
                data.get("department");

        if (studentName == null ||
                studentId == null ||
                department == null ||
                studentName.isEmpty() ||
                studentId.isEmpty() ||
                department.isEmpty()) {

            sendResponse(
                    exchange,
                    "Please fill all fields",
                    "text/plain"
            );

            return;
        }

        try {

            String result =
                    MemberManager.registerMember(
                            studentName,
                            studentId,
                            department
                    );

            sendResponse(
                    exchange,
                    result,
                    "text/plain"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error registering student: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // ISSUE BOOK
    // =========================

    private static void issueBook(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String bookName =
                data.get("bookName");

        String memberName =
                data.get("memberName");

        if (bookName == null ||
                memberName == null ||
                bookName.isEmpty() ||
                memberName.isEmpty()) {

            sendResponse(
                    exchange,
                    "Please fill all fields",
                    "text/plain"
            );

            return;
        }

        try {

            String result =
                    IssueManager.issueBook(
                            bookName,
                            memberName
                    );

            sendResponse(
                    exchange,
                    result,
                    "text/plain"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error issuing book: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // RETURN BOOK
    // =========================

    private static void returnBook(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String bookName =
                data.get("bookName");

        String memberName =
                data.get("memberName");

        if (bookName == null ||
                memberName == null ||
                bookName.isEmpty() ||
                memberName.isEmpty()) {

            sendResponse(
                    exchange,
                    "Please fill all fields",
                    "text/plain"
            );

            return;
        }

        try {

            String result =
                    IssueManager.returnBook(
                            bookName,
                            memberName
                    );

            sendResponse(
                    exchange,
                    result,
                    "text/plain"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error returning book: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // REQUEST BOOK
    // =========================

    private static void requestBook(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String studentName =
                data.get("studentName");

        String studentId =
                data.get("studentId");

        String bookName =
                data.get("bookName");

        if (studentName == null ||
                studentId == null ||
                bookName == null ||
                studentName.isEmpty() ||
                studentId.isEmpty() ||
                bookName.isEmpty()) {

            sendResponse(
                    exchange,
                    "Please fill all fields",
                    "text/plain"
            );

            return;
        }

        try (Connection con =
                     DBConnection.getConnection()) {

            String sql =
                    "INSERT INTO book_requests " +
                    "(student_name, student_id, book_name, status) " +
                    "VALUES (?, ?, ?, 'Pending')";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ps.setString(1, studentName);
            ps.setString(2, studentId);
            ps.setString(3, bookName);

            ps.executeUpdate();

            sendResponse(
                    exchange,
                    "Book Request Submitted Successfully!",
                    "text/plain"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error requesting book: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // GET REQUESTS
    // =========================

    private static void getRequests(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("GET")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        try (Connection con =
                     DBConnection.getConnection()) {

            String sql =
                    "SELECT request_id, student_name, " +
                    "student_id, book_name, status " +
                    "FROM book_requests " +
                    "WHERE status = 'Pending'";

            PreparedStatement ps =
                    con.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery();

            StringBuilder json =
                    new StringBuilder();

            json.append("[");

            boolean first = true;

            while (rs.next()) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                json.append("{");

                json.append("\"requestId\":")
                        .append(
                                rs.getInt("request_id")
                        )
                        .append(",");

                json.append("\"studentName\":\"")
                        .append(
                                escapeJson(
                                        rs.getString(
                                                "student_name"
                                        )
                                )
                        )
                        .append("\",");

                json.append("\"studentId\":\"")
                        .append(
                                escapeJson(
                                        rs.getString(
                                                "student_id"
                                        )
                                )
                        )
                        .append("\",");

                json.append("\"bookName\":\"")
                        .append(
                                escapeJson(
                                        rs.getString(
                                                "book_name"
                                        )
                                )
                        )
                        .append("\",");

                json.append("\"status\":\"")
                        .append(
                                escapeJson(
                                        rs.getString(
                                                "status"
                                        )
                                )
                        )
                        .append("\"");

                json.append("}");
            }

            json.append("]");

            sendResponse(
                    exchange,
                    json.toString(),
                    "application/json"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error getting requests: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // UPDATE REQUEST
    // =========================

    private static void updateRequest(
            HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "Invalid request",
                    "text/plain"
            );

            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String requestId =
                data.get("requestId");

        String status =
                data.get("status");

        if (requestId == null ||
                status == null ||
                requestId.isEmpty() ||
                status.isEmpty()) {

            sendResponse(
                    exchange,
                    "Invalid request data",
                    "text/plain"
            );

            return;
        }

        try (Connection con =
                     DBConnection.getConnection()) {

            // =========================
            // APPROVE REQUEST
            // =========================

            if (status.equalsIgnoreCase("Approved")) {

                String selectSql =
                        "SELECT student_name, book_name " +
                        "FROM book_requests " +
                        "WHERE request_id = ?";

                PreparedStatement selectPs =
                        con.prepareStatement(selectSql);

                selectPs.setInt(
                        1,
                        Integer.parseInt(requestId)
                );

                ResultSet rs =
                        selectPs.executeQuery();

                if (!rs.next()) {

                    rs.close();
                    selectPs.close();

                    sendResponse(
                            exchange,
                            "Request not found.",
                            "text/plain"
                    );

                    return;
                }

                String studentName =
                        rs.getString("student_name");

                String bookName =
                        rs.getString("book_name");

                rs.close();
                selectPs.close();

                // Automatically issue the requested book
                String issueResult =
                        IssueManager.issueBook(
                                bookName,
                                studentName
                        );

                // If issuing failed, do not approve the request
                if (!issueResult.contains(
                        "Book issued successfully")) {

                    sendResponse(
                            exchange,
                            issueResult,
                            "text/plain"
                    );

                    return;
                }

                // Update request status to Approved
                String updateSql =
                        "UPDATE book_requests " +
                        "SET status = ? " +
                        "WHERE request_id = ?";

                PreparedStatement updatePs =
                        con.prepareStatement(updateSql);

                updatePs.setString(
                        1,
                        "Approved"
                );

                updatePs.setInt(
                        2,
                        Integer.parseInt(requestId)
                );

                updatePs.executeUpdate();

                updatePs.close();

                sendResponse(
                        exchange,
                        "Request approved and book issued successfully!",
                        "text/plain"
                );

                return;
            }


            // =========================
            // REJECT REQUEST
            // =========================

            if (status.equalsIgnoreCase("Rejected")) {

                String sql =
                        "UPDATE book_requests " +
                        "SET status = ? " +
                        "WHERE request_id = ?";

                PreparedStatement ps =
                        con.prepareStatement(sql);

                ps.setString(
                        1,
                        "Rejected"
                );

                ps.setInt(
                        2,
                        Integer.parseInt(requestId)
                );

                int rows =
                        ps.executeUpdate();

                ps.close();

                if (rows > 0) {

                    sendResponse(
                            exchange,
                            "Request rejected successfully!",
                            "text/plain"
                    );

                } else {

                    sendResponse(
                            exchange,
                            "Request not found.",
                            "text/plain"
                    );
                }

                return;
            }


            // =========================
            // INVALID STATUS
            // =========================

            sendResponse(
                    exchange,
                    "Invalid status.",
                    "text/plain"
            );

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error updating request: "
                            + e.getMessage(),
                    "text/plain"
            );
        }
    }


    // =========================
    // READ FORM DATA
    // =========================

    private static Map<String, String> readFormData(
            HttpExchange exchange) throws IOException {

        String body =
                new String(
                        exchange.getRequestBody().readAllBytes(),
                        StandardCharsets.UTF_8
                );

        Map<String, String> data =
                new HashMap<>();

        if (body.isEmpty()) {
            return data;
        }

        String[] pairs =
                body.split("&");

        for (String pair : pairs) {

            String[] keyValue =
                    pair.split("=", 2);

            String key =
                    URLDecoder.decode(
                            keyValue[0],
                            StandardCharsets.UTF_8
                    );

            String value = "";

            if (keyValue.length > 1) {

                value =
                        URLDecoder.decode(
                                keyValue[1],
                                StandardCharsets.UTF_8
                        );
            }

            data.put(key, value);
        }

        return data;
    }


    // =========================
    // SEND FILE
    // =========================

    private static void sendFile(
            HttpExchange exchange,
            String fileName,
            String contentType) throws IOException {

        try {

            byte[] content =
                    Files.readAllBytes(
                            Paths.get(fileName)
                    );

            exchange.getResponseHeaders()
                    .set(
                            "Content-Type",
                            contentType +
                                    "; charset=UTF-8"
                    );

            exchange.sendResponseHeaders(
                    200,
                    content.length
            );

            OutputStream os =
                    exchange.getResponseBody();

            os.write(content);
            os.close();

        } catch (Exception e) {

            sendResponse(
                    exchange,
                    "File not found: "
                            + fileName,
                    "text/plain"
            );
        }
    }


    // =========================
    // SEND RESPONSE
    // =========================

    private static void sendResponse(
            HttpExchange exchange,
            String response,
            String contentType) throws IOException {

        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        contentType +
                                "; charset=UTF-8"
                );

        exchange.sendResponseHeaders(
                200,
                bytes.length
        );

        OutputStream os =
                exchange.getResponseBody();

        os.write(bytes);
        os.close();
    }


    // =========================
    // JSON ESCAPE
    // =========================

    private static String escapeJson(
            String text) {

        if (text == null) {
            return "";
        }

        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}