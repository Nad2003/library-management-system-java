import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;

public class Admin {


    public void adminLogIn(Connection connection, Scanner scanner){

        final String query="SELECT * FROM ADMIN WHERE NAME = ? AND PASSWORD = ?";

        scanner.nextLine();

        System.out.println(" Enter your Usr ID :");
        String name = scanner.nextLine();

        System.out.println("Enter your Password:");
        String mypass = scanner.nextLine();


        try {
            PreparedStatement stat=connection.prepareStatement(query);
            stat.setString(1,hashedPassword(name));
            stat.setString(2,hashedPassword(mypass));
            ResultSet set=stat.executeQuery();
            if(set.next()){
             while (true){
                 System.out.println("Show All Available Book : 1");
                 System.out.println("Add New  Book : 2");
                 System.out.println("Exit : 3");
                 int choice =scanner.nextInt();
                 switch(choice){
                     case 1: showBooks(connection);
                         break;
                     case 2:AddBook(connection,scanner);
                         break;
                     case 3: return;
                 }
             }

            }else{
                System.out.println("Invalid Admin");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void AddBook(Connection connection,Scanner scanner) {
        final String query="INSERT INTO BOOKS (BOOK_NAME ,AUTHOR,QUANTITY) VALUE (?,?,?) ";
        scanner.nextLine();
        System.out.println("Enter the Book Name ");
        String Bookname =scanner.nextLine();
        System.out.println("Enter the Author Name ");
        String name =scanner.nextLine();
        System.out.println("Enter the quantity of the Book ");
        int quant=scanner.nextInt();

        try {
            PreparedStatement stat=connection.prepareStatement(query);
            stat.setString(1,Bookname);
            stat.setString(2,name);
            stat.setInt(3,quant);
            int val =stat.executeUpdate();
            if(val>0){
                System.out.println("Book Added Successfully ! ");
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static void showBooks(Connection connection) {
        final String query= "Select * from BOOKS";
        try {
            Statement stat=connection.createStatement();

            ResultSet set=stat.executeQuery(query);
            while (set.next()){
                int isbn=set.getInt("ISBN");
                String book=set.getString("BOOK_NAME").trim();
                String author =set.getString("AUTHOR").trim();
                int qu=set.getInt("QUANTITY");

                System.out.println("|--------|---------------------|---------------------|------|");
                System.out.printf("| %-7d| %-19s | %-19s | %-5d|\n", isbn,book,author,qu);



            }
            System.out.println("-------------------------------------------------------------");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public String hashedPassword(String mypass) {
        try {
            MessageDigest md=MessageDigest.getInstance("SHA-256");
            byte[] hash=md.digest(mypass.getBytes());
            StringBuilder hex=new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
