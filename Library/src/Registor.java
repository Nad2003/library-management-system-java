import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class Registor {


    public void userDelete(Connection connection,Scanner scanner){

        final String delQuery="DELETE FROM STUDENT WHERE NAME = ? AND PASSWORD =?";
        final String QueryJoin="SELECT * FROM student AS S join issue AS I WHERE S.ROLL_NUMBER= I.ROLL_NUMBER";
        final String check="SELECT * FROM STUDENT WHERE NAME = ? AND PASSWORD = ?";
        scanner.nextLine();

        System.out.println("Enter your Name:");
        String name = scanner.nextLine();

        System.out.println("Enter your Password:");
        String mypass = scanner.nextLine();
String hashPass=hashedPassword(mypass);
        try {
            PreparedStatement stat=connection.prepareStatement(check);
            stat.setString(1,name);
            stat.setString(2,hashPass);
            ResultSet set=stat.executeQuery();
            int roll=0;
            Boolean found=false;
            if (set.next()){
                roll=set.getInt("ROLL_NUMBER");
                found=true;
            }

            if(found){
                PreparedStatement join=connection.prepareStatement(QueryJoin);
//                join.setInt(1,roll);
//                join.setInt(2,roll);
                ResultSet res=join.executeQuery();
                if(res.next()){
                    System.out.println("User Can not Be Deleted Because You Have a Book of ISBN : "+ res.getInt("ISBN") );
                    System.out.println("Please Return The Book ");

                }else{
                    PreparedStatement del=connection.prepareStatement(delQuery);
                    del.setString(1,name);
                    del.setString(2,hashPass);
                   int result= del.executeUpdate();
                   if (result>0){
                       System.out.println("User Deleted Success fully !");
                   }
                }
            }





        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    public void  Registor(Connection connection, Scanner scanner) {

        final String query = "INSERT INTO STUDENT (NAME, COURSE, PASSWORD) VALUES (?, ?, ?)";

        scanner.nextLine(); // 🔥 FIX: clear buffer

        System.out.println("Enter your Name:");
        String name = scanner.nextLine();

        System.out.println("Enter your Course:");
        String course = scanner.nextLine();

        System.out.println("Enter your Password:");
        String mypass = scanner.nextLine();

        try (PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, name.toUpperCase());
            statement.setString(2, course);
            statement.setString(3,hashedPassword(mypass));

            int val = statement.executeUpdate();

            if (val > 0) {
                System.out.println("Registration successful!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
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


    public void logIn(Connection connection,Scanner scanner){
        final String query="SELECT * FROM STUDENT WHERE NAME = ? AND PASSWORD = ?";
        scanner.nextLine(); // 🔥 clear buffer

        System.out.println("Enter your Name:");
        String name = scanner.nextLine().toUpperCase();

        System.out.println("Enter your Password:");
        String pass = scanner.nextLine();
boolean found=false;
        int roll=0;
        try {
            PreparedStatement stat=connection.prepareStatement(query);
            stat.setString(1,name);
            stat.setString(2,hashedPassword(pass));
            ResultSet set=stat.executeQuery();
            if (set.next()) {
 roll= set.getInt("ROLL_NUMBER");
              found=true;


            }else{
                System.out.println("Not Registered Student");
            }
if (found){

   while(true){
       System.out.println("Issue Book :1 ");
       System.out.println("Return a Book : 2");
       System.out.println("Log Out : 3");
       int choice =scanner.nextInt();
       if(choice==1){
           issueBook(connection,roll,scanner);
           while (true){
               System.out.println("want more book : 1");
               System.out.println("Exit : 2");
               int ch=scanner.nextInt();
               if(ch==1){
                   issueBook(connection,roll,scanner);
               }else {
                   loading();
                   break;
               }
           }
       } else if (choice==2) {

           returnBook(connection,scanner,roll);

       }else{
           logOut();
           break;
       }
   }

}




        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void returnBook(Connection connection, Scanner scanner,int roll) {
        final String query = "SELECT * FROM BOOKS WHERE ISBN = ?";
        final String updateQuery = "UPDATE BOOKS SET QUANTITY = ? WHERE ISBN = ?";
        final String removeIssued = "DELETE FROM ISSUE WHERE ROLL_NUMBER = ? AND ISBN = ?";

        System.out.println("Enter the ISBN of Issued book:");
        int isbn = scanner.nextInt();

        int quant = 0;

        try {
            // Step 1: Check book exists
            PreparedStatement stat = connection.prepareStatement(query);
            stat.setInt(1, isbn);
            ResultSet set = stat.executeQuery();

            if (!set.next()) {
                System.out.println("Book not found");
                return;
            }
            quant = set.getInt("QUANTITY");

            // Step 2: Update quantity
            PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setInt(1, quant + 1);
            updateStmt.setInt(2, isbn);
            updateStmt.executeUpdate();

            // Step 3: Remove from ISSUE table
            PreparedStatement removeStmt = connection.prepareStatement(removeIssued);
            removeStmt.setInt(1, roll);
            removeStmt.setInt(2, isbn);

            if (removeStmt.executeUpdate() > 0) {
                System.out.println("Book Returned Successfully");
            } else {
                System.out.println("No record found in ISSUE table");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void issueBook(Connection connection ,int roll,Scanner scanner) {
String query="INSERT INTO ISSUE (ROLL_NUMBER,ISBN,QUENTITY) VALUES(?,?,?)";
String queryUpdate="UPDATE BOOKS SET QUANTITY = ? where ISBN= ?";
        System.out.println("Here are some books are available ");
        showBooks(connection);
        System.out.println("Enter the ISBN number to Issue ");
        int isbn=scanner.nextInt();
if(!checkBook(connection,roll,isbn)){
    try {
        Statement stat=connection.createStatement();
        ResultSet set=stat.executeQuery( "Select * from BOOKS WHERE ISBN="+isbn);

        if( set.next()){
            int quant=set.getInt("QUANTITY");
            if(quant>0){
                PreparedStatement statement=connection.prepareStatement(query);
                statement.setInt(1,roll);
                statement.setInt(2,isbn);
                statement.setInt(3,1);//  give a book single book
                statement.executeUpdate();
            }
            // Update in the book table decrease the quantity of the book that is issued to a student

            PreparedStatement statement=connection.prepareStatement(queryUpdate);
            statement.setInt(1,(quant-1));
            statement.setInt(2,isbn);
            statement.executeUpdate();

        }

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}else{
    System.out.println("Book is already issued to You ");
}


    }

    private boolean checkBook(Connection connection,int roll, int isbn) {
        final String query="SELECT * FROM ISSUE WHERE ROLL_NUMBER = ? AND ISBN = ?";
        try {
            PreparedStatement stat=connection.prepareStatement(query);
            stat.setInt(1,roll);
            stat.setInt(2,isbn);
           ResultSet set= stat.executeQuery();
           if(set.next()){
               return true;
           }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
return false;
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

    public static void logOut(){
        System.out.print("Logging Out ");
        for (int i = 0; i < 4; i++) {
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.print(".");
        }
    }
    public static void loading(){
        System.out.print("Leaving from Library");
        for (int i = 0; i < 4; i++) {
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.print(".");
        }
        System.out.println();
    }

}




