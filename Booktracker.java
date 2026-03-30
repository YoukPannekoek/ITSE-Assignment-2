
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.PreparedStatement;

public class Booktracker {

    public static void main(String[] args) {

        String url = "jdbc:sqlite:Booktracker.db";

        try {


            Connection conn = DriverManager.getConnection(url);

            Statement stmt = conn.createStatement();

            stmt.execute("DROP TABLE IF EXISTS ReadingHabit;");
            stmt.execute("DROP TABLE IF EXISTS User;");

            String sqlCreateUser = "CREATE TABLE IF NOT EXISTS User ("
                            + "userID INTEGER PRIMARY KEY,"
                            + "age INTEGER,"
                            + "gender TEXT"
                            + ");";
    
            stmt.execute(sqlCreateUser);

            String sqlCreateHabit = "CREATE TABLE IF NOT EXISTS ReadingHabit ("
                            + "habitID INTEGER PRIMARY KEY,"
                            + "book TEXT,"
                            + "pagesRead INTEGER,"
                            + "submissionMoment DATETIME,"
                            + "user INTEGER,"
                            + "FOREIGN KEY(user) REFERENCES User(userID)"
                            + ");";

            stmt.execute(sqlCreateHabit);

            System.out.println("Succes! The database and tables have been created!");

            loadUsers();
            loadHabits();

            stmt.close();
            conn.close();

            } catch (Exception e) {
                System.out.println("Something went wrong: " + e.getMessage());
        }
    } 
    
    public static void loadUsers() {
        String url = "jdbc:sqlite:booktracker.db";

        String sqlInsert = "INSERT INTO User(userID, age, gender) VALUES(?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlInsert);

            BufferedReader br = new BufferedReader(new FileReader("User.csv"));

            br.readLine();

            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",");

                pstmt.setInt(1, Integer.parseInt(data[0]));
                pstmt.setInt(2,Integer.parseInt(data[1]));
                pstmt.setString(3, data[2]);

                pstmt.executeUpdate();
                count++;
            }

            System.out.println("Success! Inserted " + count + " users into the databse.");

            br.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    public static void loadHabits() {
        String url = "jdbc:sqlite:Booktracker.db";

        String sqlInsert = "INSERT INTO ReadingHabit(habitID, user, pagesRead, book, submissionMoment) VALUES(?, ?, ?, ?, ?)";

        try {
            Connection conn = DriverManager.getConnection(url);
            PreparedStatement pstmt = conn.prepareStatement(sqlInsert);

            BufferedReader br = new BufferedReader(new FileReader("ReadingHabit.csv"));

            br.readLine();

            String line;
            int count = 0;

            while ((line = br.readLine()) != null) {

                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

                pstmt.setInt(1, Integer.parseInt(data[0]));
                pstmt.setInt(2, Integer.parseInt(data[1]));
                pstmt.setInt(3, Integer.parseInt(data[2]));

                String cleanBookTitle = data[3].replace("\"", "");
                pstmt.setString(4, cleanBookTitle);

                pstmt.setString(5, data[4]);

                pstmt.executeUpdate();
                count++;
            }

            System.out.println("Success! Inserted " + count + " reading habits into the database.");

            br.close();
            pstmt.close();
            conn.close();

        }  catch (Exception e) {
            System.out.println("Error loading habits: " + e.getMessage());
        }
    }
}