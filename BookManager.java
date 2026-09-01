import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BookManager {

    // Add a new book
    public static void addBook(
            String bookName,
            String author,
            String category) {

        try {

            Connection connection =
                    DBConnection.getConnection();

            String sql =
                    "INSERT INTO books " +
                    "(title, author, category, status) " +
                    "VALUES (?, ?, ?, 'Available')";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, bookName);
            statement.setString(2, author);
            statement.setString(3, category);

            statement.executeUpdate();

            statement.close();
            connection.close();

            System.out.println("Book added successfully!");

        } catch (Exception e) {

            System.out.println("Error adding book:");
            e.printStackTrace();
        }
    }


    // Search for a book
    public static String searchBook(String bookName) {

        StringBuilder books =
                new StringBuilder();

        try {

            Connection connection =
                    DBConnection.getConnection();

            String sql =
                    "SELECT * FROM books " +
                    "WHERE title LIKE ?";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(
                    1,
                    "%" + bookName + "%"
            );

            ResultSet result =
                    statement.executeQuery();

            boolean found = false;

            while (result.next()) {

                found = true;

                books.append(
                        "Book Name: " +
                        result.getString("title")
                );

                books.append("\n");

                books.append(
                        "Author: " +
                        result.getString("author")
                );

                books.append("\n");

                books.append(
                        "Category: " +
                        result.getString("category")
                );

                books.append("\n");

                books.append(
                        "Status: " +
                        result.getString("status")
                );

                books.append("\n");
                books.append("----------------------\n");
            }

            if (!found) {
                books.append("Book not found.");
            }

            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            books.append("Error searching book.");
            e.printStackTrace();
        }

        return books.toString();
    }


    // Display all books
    public static void viewAllBooks() {

        try {

            Connection connection =
                    DBConnection.getConnection();

            String sql =
                    "SELECT * FROM books";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                System.out.println(
                        "Book Name: " +
                        result.getString("title")
                );

                System.out.println(
                        "Author: " +
                        result.getString("author")
                );

                System.out.println(
                        "Category: " +
                        result.getString("category")
                );

                System.out.println(
                        "Status: " +
                        result.getString("status")
                );

                System.out.println(
                        "----------------------"
                );
            }

            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            System.out.println(
                    "Error displaying books:"
            );

            e.printStackTrace();
        }
    }
}