package wmdc.mobilecsa.tests;

import wmdc.mobilecsa.utils.BCrypt;

public class PasswordGen {

    public static void main(String[] args) {
        System.out.println(BCrypt.hashpw("mcsa12345", BCrypt.gensalt()));
        String hash = "$2a$10$GbCR9QrZ1Ww43UDHbigzDOd6xtdFCnfZ6UhPio3aipoSdNwanvnly";


        System.out.println(BCrypt.checkpw("mcsa12345", hash));
    }
}
