import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final String url="jdbc:mysql://localhost:3306/LIBRARY";
        final String user="root";
        final String Password="Nadeem@786";
        Scanner scanner=new Scanner(System.in);
        Registor registor=new Registor();
        Admin admin=new Admin();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try {
                Connection connection= DriverManager.getConnection(url,user,Password);
                System.out.println(" ******** WELCOME TO THE LIBRARY MANAGEMENT SYSTEM *********");
                System.out.println("Student : 1");
                System.out.println("Admin : 2");
                int ch=scanner.nextInt();
                if(ch==1){
                    while (true){
                        System.out.println("Register : 1");
                        System.out.println("Log IN : 2");
                        System.out.println("Delete User : 3");
                        System.out.println("Exit : 4");
                        int choice =scanner.nextInt();
                        switch (choice){
                            case 1: registor.Registor(connection,scanner);
                                break;
                            case 2:registor.logIn(connection,scanner);
                                break;
                            case 3: registor.userDelete(connection,scanner);
                                break;
                            case 4:
                                connection.close();
                                System.out.println("Thank You !");
                                return;
                        }

                    }
                } else if (ch==2) {
                    admin.adminLogIn(connection,scanner);
                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
            }




        } catch (ClassNotFoundException e) {
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




}

//@*V?_Q!?