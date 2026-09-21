import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MemberManager {

    // Add a new member from Librarian Portal
    public static void addMember(
            String memberName,
            String department) {

        try {

            Connection connection =
                    DBConnection.getConnection();

            String sql =
                    "INSERT INTO members " +
                    "(member_name, department) " +
                    "VALUES (?, ?)";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, memberName);
            statement.setString(2, department);

            statement.executeUpdate();

            statement.close();
            connection.close();

            System.out.println("Member added successfully!");

        } catch (Exception e) {

            System.out.println("Error adding member:");
            e.printStackTrace();
        }
    }


    // Register a student from Student Portal
    public static String registerMember(
            String studentName,
            String studentId,
            String department) {

        try {

            Connection connection =
                    DBConnection.getConnection();

            // Check whether Student ID already exists
            String checkSql =
                    "SELECT * FROM members " +
                    "WHERE student_id = ?";

            PreparedStatement checkStatement =
                    connection.prepareStatement(checkSql);

            checkStatement.setString(1, studentId);

            ResultSet result =
                    checkStatement.executeQuery();

            if (result.next()) {

                result.close();
                checkStatement.close();
                connection.close();

                return "Student ID is already registered.";

            }

            result.close();
            checkStatement.close();


            // Insert new student
            String sql =
                    "INSERT INTO members " +
                    "(member_name, student_id, department) " +
                    "VALUES (?, ?, ?)";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(1, studentName);
            statement.setString(2, studentId);
            statement.setString(3, department);

            statement.executeUpdate();

            statement.close();
            connection.close();

            return "Registration Successful!";


        } catch (Exception e) {

            e.printStackTrace();

            return "Error registering student.";

        }
    }


    // Search for a member
    public static void searchMember(String memberName) {

        try {

            Connection connection =
                    DBConnection.getConnection();

            String sql =
                    "SELECT * FROM members " +
                    "WHERE member_name LIKE ?";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            statement.setString(
                    1,
                    "%" + memberName + "%"
            );

            ResultSet result =
                    statement.executeQuery();

            boolean found = false;

            while (result.next()) {

                found = true;

                System.out.println(
                        "Member Name: " +
                        result.getString("member_name")
                );

                System.out.println(
                        "Student ID: " +
                        result.getString("student_id")
                );

                System.out.println(
                        "Department: " +
                        result.getString("department")
                );

                System.out.println("----------------------");
            }

            if (!found) {
                System.out.println("Member not found.");
            }

            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            System.out.println("Error searching member:");
            e.printStackTrace();
        }
    }


    // Display all members
    public static void viewAllMembers() {

        try {

            Connection connection =
                    DBConnection.getConnection();

            String sql =
                    "SELECT * FROM members";

            PreparedStatement statement =
                    connection.prepareStatement(sql);

            ResultSet result =
                    statement.executeQuery();

            while (result.next()) {

                System.out.println(
                        "Member Name: " +
                        result.getString("member_name")
                );

                System.out.println(
                        "Student ID: " +
                        result.getString("student_id")
                );

                System.out.println(
                        "Department: " +
                        result.getString("department")
                );

                System.out.println("----------------------");
            }

            result.close();
            statement.close();
            connection.close();

        } catch (Exception e) {

            System.out.println(
                    "Error displaying members:"
            );

            e.printStackTrace();
        }
    }
}