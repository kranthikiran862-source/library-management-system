import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IssueManager {

    // Issue a book
    public static String issueBook(
            String bookName,
            String memberName) {

        try {
            Connection connection =
                    DBConnection.getConnection();

            // Check whether book exists
            String checkSql =
                    "SELECT status FROM books WHERE title = ?";

            PreparedStatement checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setString(1, bookName);

            ResultSet result =
                    checkStatement.executeQuery();

            if (!result.next()) {
                result.close();
                checkStatement.close();
                connection.close();

                return "Book not found.";
            }

            String status = result.getString("status");

            result.close();
            checkStatement.close();

            // Check whether book is available
            if (!status.equalsIgnoreCase("Available")) {
                connection.close();
                return "Book is already issued.";
            }

            // Check whether member exists
            String memberSql =
                    "SELECT * FROM members WHERE member_name = ?";

            PreparedStatement memberStatement =
                    connection.prepareStatement(memberSql);

            memberStatement.setString(1, memberName);

            ResultSet memberResult =
                    memberStatement.executeQuery();

            if (!memberResult.next()) {
                memberResult.close();
                memberStatement.close();
                connection.close();

                return "Member not found.";
            }

            memberResult.close();
            memberStatement.close();

            // Create issue record
            String issueSql =
                    "INSERT INTO issued_books " +
                    "(book_name, member_name, issue_date) " +
                    "VALUES (?, ?, CURRENT_DATE)";

            PreparedStatement issueStatement =
                    connection.prepareStatement(issueSql);

            issueStatement.setString(1, bookName);
            issueStatement.setString(2, memberName);

            issueStatement.executeUpdate();
            issueStatement.close();

            // Change book status
            String updateSql =
                    "UPDATE books SET status = 'Issued' " +
                    "WHERE title = ?";

            PreparedStatement updateStatement =
                    connection.prepareStatement(updateSql);

            updateStatement.setString(1, bookName);
            updateStatement.executeUpdate();
            updateStatement.close();

            connection.close();

            return "Book issued successfully!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error issuing book.";
        }
    }


    // Return a book
    public static String returnBook(
            String bookName,
            String memberName) {

        try {
            Connection connection =
                    DBConnection.getConnection();

            // Check active issue record
            String checkSql =
                    "SELECT * FROM issued_books " +
                    "WHERE book_name = ? " +
                    "AND member_name = ? " +
                    "AND return_date IS NULL";

            PreparedStatement checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setString(1, bookName);
            checkStatement.setString(2, memberName);

            ResultSet result =
                    checkStatement.executeQuery();

            if (!result.next()) {
                result.close();
                checkStatement.close();
                connection.close();

                return "No active issue record found.";
            }

            result.close();
            checkStatement.close();

            // Add return date
            String returnSql =
                    "UPDATE issued_books " +
                    "SET return_date = CURRENT_DATE " +
                    "WHERE book_name = ? " +
                    "AND member_name = ? " +
                    "AND return_date IS NULL";

            PreparedStatement returnStatement =
                    connection.prepareStatement(returnSql);

            returnStatement.setString(1, bookName);
            returnStatement.setString(2, memberName);

            returnStatement.executeUpdate();
            returnStatement.close();

            // Change book status back to Available
            String updateSql =
                    "UPDATE books SET status = 'Available' " +
                    "WHERE title = ?";

            PreparedStatement updateStatement =
                    connection.prepareStatement(updateSql);

            updateStatement.setString(1, bookName);
            updateStatement.executeUpdate();
            updateStatement.close();

            connection.close();

            return "Book returned successfully!";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error returning book.";
        }
    }
}