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
import java.sql.*;

/**
 * Created by wmdcprog on 2/24/2017.
 */
@WebServlet("/scanloggedinsession")

public class ScanLoggedinSession extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject responseJson = new JSONObject();
        PrintWriter out = response.getWriter();

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            HttpSession httpSession = request.getSession(false);

            if (httpSession.getAttribute("username") == null) {
                Utils.printJsonException(responseJson, "No online users", out);
                return;
            }

            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT COUNT(*) as userAdminCount FROM admins WHERE username = ?");
            prepStmt.setString(1, (String) httpSession.getAttribute("username"));
            resultSet = prepStmt.executeQuery();

            int userAdminCount = 0;
            if (resultSet.next()) {
                userAdminCount = resultSet.getInt("userAdminCount");
            }

            if (userAdminCount > 0) {
                responseJson.put("isAdmin", true);
            }
            else {
                responseJson.put("isAdmin", false);
            }

            responseJson.put("success", true);
            responseJson.put("reason", "session is active");

            out.println(responseJson);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot scan session at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    getServletContext(), conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot scan session at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(),
                    getServletContext(), conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, getServletContext());
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}