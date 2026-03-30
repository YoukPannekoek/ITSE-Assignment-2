import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.ResultSet;

public class main {

    private static final String DB_URL = "jdbc:sqlite:Booktracker.db";

    public static void main(String[] args) {

        runMenu();
    }

    public static void runMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== BOOKTRACKER MENU ===");
            System.out.println("1. Add a user to the database");
            System.out.println("2. Get a user's reading habits");
            System.out.println("3. Change a book title");
            System.out.println("4. Delete a reading habit");
            System.out.println("5. Mean age of users");
            System.out.println("6. Total users who read a specific book");
            System.out.println("7. Total pages read by all users");
            System.out.println("8. Total users who read more than one book");
            System.out.println("9. Add 'Name' column to User table");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addUser(scanner);
                    break;
                case 2:
                    getUserHabits(scanner);
                    break;
                case 3:
                    updateBookTitle(scanner);
                    break;
                case 4:
                    deleteHabit(scanner);
                    break;
                case 5:
                    getMeanAge();
                    break;
                case 6:
                    getUsersForBook(scanner);
                    break;
                case 7:
                    getTotalPagesRead();
                    break;
                case 8:
                    getMultiBookUser();
                    break;
                case 9:
                    addNameColumn();
                    break;
                case 0:
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
    
    public static void addUser(Scanner scanner) {
        System.out.println("\n--- Add a New User ---");
        System.out.print("Enter user's age: ");
        int age = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter user's gender (e.g., m, f, other): ");
        String gender = scanner.nextLine();

        String sql = "INSERT INTO User(age, gender) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, age);
                pstmt.setString(2, gender);
                pstmt.executeUpdate();

                System.out.println("Success! User added to the database.");
             } catch (Exception e) {
                System.out.println("Error adding user: " + e.getMessage());
             }     
        }
    
    public static void getUserHabits(Scanner scanner) {
        System.out.println("\n--- Get User's Reading Habits ---");
        System.out.println("Enter the userID ou want to search for: ");
        int searchId = scanner.nextInt();
        scanner.nextLine();

        String sql = "SELECT * FROM ReadingHabit WHERE user = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, searchId);

                var rs = pstmt.executeQuery();

                System.out.println("\nReading Habits for User #" + searchId + ":");
                System.out.println("------------------------------------------------");

                boolean foundAny = false;

                while (rs.next()) {
                    foundAny = true;
                    int habitID = rs.getInt("habitID");
                    String bookTitle = rs.getString("book");
                    int pages = rs.getInt("pagesRead");
                    String date = rs.getString("submissionMoment");

                    System.out.println("Habit ID: " + habitID + " | Book: " + bookTitle + " | Pages: " + pages + " | Date: " + date);

                }

                if (!foundAny) {
                    System.out.println("No reading habits found for this user.");

                }

             } catch (Exception e) {
                System.out.println("Error retrieving reading habits: " + e.getMessage());
             }

    }

    public static void updateBookTitle(Scanner scanner) {
        System.out.println("\n--- Change a Book Title ---");
        System.out.println("Enter the CURRENT title of the book: ");
        String oldTitle = scanner.nextLine();

        System.out.print("Enter the NEW title of the book: ");
        String newTitle = scanner.nextLine();

        String sql = "UPDATE ReadingHabit SET book = ? WHERE book = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, newTitle);
                pstmt.setString(2, oldTitle);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Success! Changed the title of " + rowsAffected + " record(s).");    
                } else {
                    System.out.println("No books found with the title: '" + oldTitle + "'. Did you spell it correctly?");
                }

             } catch (Exception e) {
                System.out.println("Error changing book title: " + e.getMessage());
             }
    }

    public static void deleteHabit(Scanner scanner) {
        System.out.println("\n--- Delete a Reading Habit ---");
        System.out.println("Enter the habitID of the record you want to delete: ");
        int habitId = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM ReadingHabit WHERE habitID = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, habitId);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Success! Deleted habit record #" + habitId + " from the database.");
                } else {
                    System.out.println("No record found with habitID: " + habitId + ".");
                }


             } catch (Exception e) {
                System.out.println("Error deleting record: " + e.getMessage());
             }   
    }

    public static void getMeanAge() {
        System.out.println("\n--- Mean Age of Users ---");

        String sql = "SELECT AVG(age) AS meanAge FROM User";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    double meanAge = rs.getDouble("meanAge");
                    System.out.println("The average age of all users is: " + Math.round(meanAge * 10.0) / 10.0 + " years old.");
                }

             } catch (Exception e) {
                System.out.println("Error calculating mean age: " + e.getMessage());
             }  
    }

    public static void getUsersForBook(Scanner scanner) {
        System.out.println("\n--- Count Users Who Read a Specific Book ---");
        System.out.print("Enter the exact title of the book: ");
        String bookTitle = scanner.nextLine();

        String sql = "SELECT COUNT(DISTINCT \"user\") AS userCount FROM ReadingHabit WHERE book = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, bookTitle);
                
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt("userCount");
                    System.out.println("A total of " + count + " user(s) have read pages from '" + bookTitle + "'.");
                }
             } catch (Exception e) {
                System.out.println("Error counting users: " + e.getMessage());
             }
    }

    public static void getTotalPagesRead() {
        System.out.println("\n--- Total Pages Read ---");

        String sql = "SELECT SUM(pagesRead) AS totalPages FROM ReadingHabit";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int totalPages = rs.getInt("totalPages");
                    System.out.println("The total number of pages read by all users combined is: " + totalPages + " pages.");   
                }
             } catch (Exception e) {
                System.out.println("Error calculating total pages: " + e.getMessage());
             }
    }

    public static void getMultiBookUser() {
        System.out.println("\n--- Total Users Who Read More Than One Book ---");

        String sql = "SELECT COUNT(*) AS totalMultiReaders FROM (" +
                     "SELECT \"user\" FROM ReadingHabit " +
                     "GROUP BY \"user\" " +
                     "HAVING COUNT(DISTINCT book) > 1)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int count = rs.getInt("totalMultiReaders");
                    System.out.println("A total of " + count + " user(s) have read more than one book.");
                }
             } catch (Exception e) {
                System.out.println("Error calculating users: " + e.getMessage());
             }
    }

    public static void addNameColumn() {
        System.out.println("\n--- Add 'Name' Column to User Table ---");

        String sql = "ALTER TABLE User ADD COLUMN Name TEXT";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.executeUpdate();

                System.out.println("Success! The 'Name' column has been added to the User table.");
             } catch (Exception e) {
                if (e.getMessage().contains("duplicate column name")) {
                    System.out.println("The 'Name' column already exists in the User table!");
                } else {
                    System.out.println("Error adding column: " + e.getMessage());
                }

             }
    }
}

