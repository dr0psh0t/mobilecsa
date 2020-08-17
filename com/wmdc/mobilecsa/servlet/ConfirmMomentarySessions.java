package wmdc.mobilecsa.servlet;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/confirmmomentarysessions")
public class ConfirmMomentarySessions extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        try {
            HttpSession httpSession = request.getSession(false);

            if(httpSession.getAttribute("interimUsername") == null) {
                responseJson.put("success", false); //  temporary username null
            } else {
                responseJson.put("success", true);  //  temporary username not null
            }

            out.println(responseJson);

        } catch (Exception e) {
            Utils.printJsonException(responseJson, e.toString(), out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", e.toString(),
                    getServletContext(), null);

        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}