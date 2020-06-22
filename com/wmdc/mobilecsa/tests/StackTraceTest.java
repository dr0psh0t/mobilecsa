package wmdc.mobilecsa.tests;

import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/stacktracetest")

public class StackTraceTest extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        Connection conn = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            test1();
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), "wmdc.mobilecsa.tests", "Exception", e.toString(),
                    getServletContext(), conn);
        }
    }

    public void test1() throws NullPointerException {
        test2();
    }

    public void test2() throws NullPointerException {
        test3();
    }

    public void test3() throws NullPointerException {
        throw new NullPointerException("test3");
    }
}
