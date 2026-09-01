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

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        // Home page
        server.createContext("/", LibraryServer::home);
        server.createContext("/style1.css", LibraryServer::css);

        // Book operations
        server.createContext("/addBook", LibraryServer::addBook);
        server.createContext("/searchBook", LibraryServer::searchBook);
        server.createContext("/getBooks", LibraryServer::getBooks);
        server.createContext("/deleteBook", LibraryServer::deleteBook);

        // Member operation
        server.createContext("/addMember", LibraryServer::addMember);

        // Issue and return
        server.createContext("/issueBook", LibraryServer::issueBook);
        server.createContext("/returnBook", LibraryServer::returnBook);

        server.start();

        System.out.println("================================");
        System.out.println("Library Management System");
        System.out.println("Server started successfully!");
        System.out.println("Open: http://localhost:8080");
        System.out.println("================================");
    }
       static void css(HttpExchange exchange) throws IOException {

    byte[] response =
            Files.readAllBytes(Paths.get("style1.css"));

    exchange.getResponseHeaders().set(
            "Content-Type",
            "text/css; charset=UTF-8"
    );

    exchange.sendResponseHeaders(
            200,
            response.length
    );

    OutputStream output =
            exchange.getResponseBody();

    output.write(response);
    output.close();
}


    // =========================
    // HOME PAGE
    // =========================

    static void home(HttpExchange exchange)
            throws IOException {

        byte[] response =
                Files.readAllBytes(Paths.get("lib.html"));

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                200,
                response.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }


    // =========================
    // ADD BOOK
    // =========================

    static void addBook(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "POST method required.",
                    405
            );
            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String bookName =
                data.get("bookName");

        String author =
                data.get("author");

        String category =
                data.get("category");

        if (bookName == null ||
            author == null ||
            category == null ||
            bookName.trim().isEmpty() ||
            author.trim().isEmpty() ||
            category.trim().isEmpty()) {

            sendResponse(
                    exchange,
                    "Please enter all book details.",
                    400
            );
            return;
        }

        BookManager.addBook(
                bookName,
                author,
                category
        );

        sendResponse(
                exchange,
                "<h2>Book Added Successfully! 📚</h2>" +
                "<a href='/'>Go Back to Library</a>",
                200
        );
    }


    // =========================
    // SEARCH BOOK
    // =========================

    static void searchBook(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "POST method required.",
                    405
            );
            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String bookName =
                data.get("searchBook");

        if (bookName == null ||
            bookName.trim().isEmpty()) {

            sendResponse(
                    exchange,
                    "Please enter a book name.",
                    400
            );
            return;
        }

        String result =
                BookManager.searchBook(bookName);

        sendResponse(
                exchange,
                "<h2>Search Result</h2>" +
                "<pre>" + result + "</pre>" +
                "<br><a href='/'>Go Back</a>",
                200
        );
    }


    // =========================
    // ADD MEMBER
    // =========================

    static void addMember(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "POST method required.",
                    405
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
            memberName.trim().isEmpty() ||
            department.trim().isEmpty()) {

            sendResponse(
                    exchange,
                    "Please enter all member details.",
                    400
            );
            return;
        }

        MemberManager.addMember(
                memberName,
                department
        );

        sendResponse(
                exchange,
                "<h2>Member Added Successfully! 👨‍🎓</h2>" +
                "<a href='/'>Go Back to Library</a>",
                200
        );
    }


    // =========================
    // ISSUE BOOK
    // =========================

    static void issueBook(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "POST method required.",
                    405
            );
            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String bookName =
                data.get("issueBookName");

        String memberName =
                data.get("issueMemberName");

        if (bookName == null ||
            memberName == null ||
            bookName.trim().isEmpty() ||
            memberName.trim().isEmpty()) {

            sendResponse(
                    exchange,
                    "Please enter book and member name.",
                    400
            );
            return;
        }

        String result =
                IssueManager.issueBook(
                        bookName,
                        memberName
                );

        sendResponse(
                exchange,
                "<h2>" + result + "</h2>" +
                "<a href='/'>Go Back to Library</a>",
                200
        );
    }


    // =========================
    // RETURN BOOK
    // =========================

    static void returnBook(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "POST method required.",
                    405
            );
            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String bookName =
                data.get("returnBookName");

        String memberName =
                data.get("returnMemberName");

        if (bookName == null ||
            memberName == null ||
            bookName.trim().isEmpty() ||
            memberName.trim().isEmpty()) {

            sendResponse(
                    exchange,
                    "Please enter book and member name.",
                    400
            );
            return;
        }

        String result =
                IssueManager.returnBook(
                        bookName,
                        memberName
                );

        sendResponse(
                exchange,
                "<h2>" + result + "</h2>" +
                "<a href='/'>Go Back to Library</a>",
                200
        );
    }


    // =========================
    // GET ALL BOOKS
    // =========================

    static void getBooks(HttpExchange exchange)
            throws IOException {

        StringBuilder books =
                new StringBuilder();

        try {

            Connection connection =
                    DBConnection.getConnection();

            String sql =
                    "SELECT book_id, title, author, category, status FROM books";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                books.append(result.getInt("book_id"))
                     .append("|")
                     .append(result.getString("title"))
                     .append("|")
                     .append(result.getString("author"))
                     .append("|")
                     .append(result.getString("category"))
                     .append("|")
                     .append(result.getString("status"))
                     .append("\n");
            }

            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

        byte[] response =
                books.toString().getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                200,
                response.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }


    // =========================
    // DELETE BOOK
    // =========================

    static void deleteBook(HttpExchange exchange)
            throws IOException {

        if (!exchange.getRequestMethod()
                .equalsIgnoreCase("POST")) {

            sendResponse(
                    exchange,
                    "POST method required.",
                    405
            );
            return;
        }

        Map<String, String> data =
                readFormData(exchange);

        String bookId =
                data.get("bookId");

        if (bookId == null ||
                bookId.trim().isEmpty()) {

            sendResponse(
                    exchange,
                    "Book ID is required.",
                    400
            );
            return;
        }

        try {

            Connection connection =
                    DBConnection.getConnection();

            String sql =
                    "DELETE FROM books WHERE book_id = ?";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setInt(
                    1,
                    Integer.parseInt(bookId)
            );

            int rowsDeleted =
                    statement.executeUpdate();

            statement.close();
            connection.close();

            if (rowsDeleted > 0) {

                sendResponse(
                        exchange,
                        "<h2>Book deleted successfully! 🗑️</h2>" +
                        "<a href='/'>Go Back to Library</a>",
                        200
                );

            } else {

                sendResponse(
                        exchange,
                        "Book not found.",
                        404
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            sendResponse(
                    exchange,
                    "Error deleting book.",
                    500
            );
        }
    }


    // =========================
    // READ FORM DATA
    // =========================

    static Map<String, String> readFormData(
            HttpExchange exchange)
            throws IOException {

        String body =
                new String(
                        exchange.getRequestBody()
                                .readAllBytes(),
                        StandardCharsets.UTF_8
                );

        Map<String, String> data =
                new HashMap<>();

        String[] pairs =
                body.split("&");

        for (String pair : pairs) {

            String[] keyValue =
                    pair.split("=", 2);

            if (keyValue.length == 2) {

                String key =
                        URLDecoder.decode(
                                keyValue[0],
                                StandardCharsets.UTF_8
                        );

                String value =
                        URLDecoder.decode(
                                keyValue[1],
                                StandardCharsets.UTF_8
                        );

                data.put(key, value);
            }
        }

        return data;
    }


    // =========================
    // SEND RESPONSE
    // =========================

    static void sendResponse(
            HttpExchange exchange,
            String message,
            int statusCode)
            throws IOException {

        byte[] response =
                message.getBytes(
                        StandardCharsets.UTF_8
                );

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/html; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                statusCode,
                response.length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(response);
        output.close();
    }
}
