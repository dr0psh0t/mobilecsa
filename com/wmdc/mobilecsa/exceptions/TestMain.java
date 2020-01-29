package wmdc.mobilecsa.exceptions;

import java.net.InetAddress;
import java.util.Scanner;

public class TestMain {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String email;

        System.out.println(isInvalidEmail(""));
        System.out.println(isInvalidEmail("asd"));
        System.out.println(isInvalidEmail("asd@asdas.com"));

        /*
        do {
            System.out.print("Enter email: ");

            email = scanner.next();

            if (isInvalidEmail(email)) {
                System.out.println("Email invalid.");
            } else {
                System.out.println("Email valid.");
            }

        } while (!email.equals("end"));

        scanner.close();*/
    }

    public static boolean isInvalidEmail(String email) {
        if (email.isEmpty()) {
            return false;
        } else {
            return !email.matches(EMAIL_PATTERN);
        }
    }

    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddress = InetAddress.getByName("google.com");
            //You can replace it with your name
            //return !ipAddress.equals("");

            boolean result = !ipAddress.getHostAddress().equals("google.com");
            System.out.println("hostAddress= "+ipAddress.getHostAddress());
            System.out.println("getHostName= "+ipAddress.getHostName());
            System.out.println("result= "+result);

            return result;
        } catch (Exception e) {
            return false;
        }
    }
}
