import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MemberManager {

    // Add a new member
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