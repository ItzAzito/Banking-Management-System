import java.sql.*;
import java.util.*;

public class Bankapp {

    static final String URL = "jdbc:mysql://localhost:3306/bank_db";
    static final String USER = "root";
    static final String PASS = "your_password";

    static Connection con;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            con = DriverManager.getConnection(URL, USER, PASS);

            while (true) {
                System.out.println("\n===== BANK MENU =====");
                System.out.println("1. Create Account");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Check Balance");
                System.out.println("5. Transaction History");
                System.out.println("6. Exit");
                System.out.print("Enter choice: ");

                int ch = sc.nextInt();

                switch (ch) {
                    case 1: createAccount(); break;
                    case 2: deposit(); break;
                    case 3: withdraw(); break;
                    case 4: checkBalance(); break;
                    case 5: transactionHistory(); break;
                    case 6:
                        System.out.println("Thank you!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice!");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    static void createAccount() throws Exception {
        sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO accounts(name, balance) VALUES(?, 0)");
        ps.setString(1, name);
        ps.executeUpdate();

        System.out.println("Account Created Successfully!");
    }

    static void deposit() throws Exception {
        System.out.print("Enter Account ID: ");
        int id = sc.nextInt();

        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();

        PreparedStatement ps = con.prepareStatement(
                "UPDATE accounts SET balance = balance + ? WHERE id = ?");
        ps.setDouble(1, amount);
        ps.setInt(2, id);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            PreparedStatement tr = con.prepareStatement(
                    "INSERT INTO transactions(acc_id, type, amount) VALUES(?, 'DEPOSIT', ?)");
            tr.setInt(1, id);
            tr.setDouble(2, amount);
            tr.executeUpdate();

            System.out.println("Deposit Successful!");
        } else {
            System.out.println("Account not found!");
        }
    }

    static void withdraw() throws Exception {
        System.out.print("Enter Account ID: ");
        int id = sc.nextInt();

        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();

        PreparedStatement check = con.prepareStatement(
                "SELECT balance FROM accounts WHERE id = ?");
        check.setInt(1, id);
        ResultSet rs = check.executeQuery();

        if (rs.next()) {
            double balance = rs.getDouble("balance");

            if (balance >= amount) {
                PreparedStatement ps = con.prepareStatement(
                        "UPDATE accounts SET balance = balance - ? WHERE id = ?");
                ps.setDouble(1, amount);
                ps.setInt(2, id);
                ps.executeUpdate();

                PreparedStatement tr = con.prepareStatement(
                        "INSERT INTO transactions(acc_id, type, amount) VALUES(?, 'WITHDRAW', ?)");
                tr.setInt(1, id);
                tr.setDouble(2, amount);
                tr.executeUpdate();

                System.out.println("Withdrawal Successful!");
            } else {
                System.out.println("Insufficient Balance!");
            }
        } else {
            System.out.println("Account not found!");
        }
    }

    static void checkBalance() throws Exception {
        System.out.print("Enter Account ID: ");
        int id = sc.nextInt();

        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM accounts WHERE id = ?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            System.out.println("Name: " + rs.getString("name"));
            System.out.println("Balance: " + rs.getDouble("balance"));
        } else {
            System.out.println("Account not found!");
        }
    }

    static void transactionHistory() throws Exception {
        System.out.print("Enter Account ID: ");
        int id = sc.nextInt();

        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM transactions WHERE acc_id = ?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        System.out.println("\n--- Transaction History ---");
        while (rs.next()) {
            System.out.println(
                    rs.getString("type") + " | " +
                    rs.getDouble("amount") + " | " +
                    rs.getTimestamp("date"));
        }
    }
}
