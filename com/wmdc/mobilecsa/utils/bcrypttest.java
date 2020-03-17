package wmdc.mobilecsa.utils;

public class bcrypttest {

    public static void main(String[] args) {

        System.out.println(BCrypt.hashpw("Wmdc123!", BCrypt.gensalt()));
    }
}
