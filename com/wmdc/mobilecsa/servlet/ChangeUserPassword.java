package wmdc.mobilecsa.servlet;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.BCrypt;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/changepass")
public class ChangeUserPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject resJson = new JSONObject();
        PrintWriter out = response.getWriter();
        ServletContext ctx = getServletContext();

        String newPassword = request.getParameter("newPassword");
        String csaId = request.getParameter("csaId");

        if (newPassword == null) {
            Utils.logError("\"newPassword\" parameter is null.", ctx);
            Utils.printJsonException(resJson, "Password is required.", out);
            return;
        } else if (newPassword.isEmpty()) {
            Utils.logError("\"newPassword\" parameter is empty.", ctx);
            Utils.printJsonException(resJson, "Password is required.", out);
            return;
        }

        if (csaId == null) {
            Utils.logError("\"csaId\" parameter is null.", ctx);
            Utils.printJsonException(resJson, "CSA is required.", out);
            return;
        } else if (csaId.isEmpty()) {
            Utils.logError("\"csaId\" parameter is empty.", ctx);
            Utils.printJsonException(resJson, "CSA is required.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            int csaIdInt = Integer.parseInt(csaId);

            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT COUNT(*) AS csaCount FROM users WHERE csa_id = ?");
            prepStmt.setInt(1, csaIdInt);
            resultSet = prepStmt.executeQuery();

            int csaCount = 0;
            while (resultSet.next()) {
                csaCount = resultSet.getInt("csaCount");
            }

            if (csaCount < 1) {
                Utils.printJsonException(resJson, "No csa found with its id.", out);
            } else {
                prepStmt = conn.prepareStatement("UPDATE users SET password = ? WHERE csa_id = ?");
                prepStmt.setString(1, BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                prepStmt.setInt(2, csaIdInt);

                int updateCount = prepStmt.executeUpdate();
                if (updateCount > 0) {
                    Utils.printSuccessJson(resJson, "Successfully updated password for "+updateCount+" user/s.", out);
                } else {
                    Utils.printJsonException(resJson, updateCount+" records were updated.", out);
                }
            }

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(), ctx);
            Utils.printJsonException(resJson, "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx);
            Utils.printJsonException(resJson, "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
