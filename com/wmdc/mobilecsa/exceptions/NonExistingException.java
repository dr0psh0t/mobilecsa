package wmdc.mobilecsa.exceptions;

/**
 * Created by wmdcprog on 4/27/2017.
 */
public class NonExistingException extends Exception
{
    public NonExistingException(String msg){
        super(msg);
    }

    public NonExistingException(){
        super("Object does not exist.");
    }
}
