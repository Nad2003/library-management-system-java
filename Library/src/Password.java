import java.util.Random;

public class Password {
    public  String password(){
        String str="123456790abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_-<>/?";
        String pass="";
        Random rend=new Random();
        for (int i = 0; i < 8; i++) {
           pass=pass+str.charAt(rend.nextInt(0,str.length()));

        }
//        System.out.println(pass);





        return pass;
    }

//    public static void main(String[] args) {
//        System.out.println(password());
//    }

}
