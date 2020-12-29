package wmdc.mobilecsa.servlet;

import wmdc.mobilecsa.utils.BCrypt;
import wmdc.mobilecsa.utils.Utils;
import org.json.JSONObject;

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

/**
 * Created by wmdcprog on 9/19/2017.
 */
@WebServlet("/changeadminpass")

public class ChangeAdminPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject responseJson = new JSONObject();
        PrintWriter out = response.getWriter();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(responseJson, "Login first.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            String admin = request.getParameter("admin");
            String password = request.getParameter("password");

            if (admin == null) {
                Utils.logError("\"admin\" parameter is null.", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (admin.isEmpty()) {
                Utils.logError("\"admin\" parameter is empty.", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (password == null) {
                Utils.logError("\"password\" parameter is null.", ctx);
                Utils.printJsonException(responseJson, "Password is required.", out);
                return;
            } else if (password.isEmpty()) {
                Utils.logError("\"password\" parameter is empty.", ctx);
                Utils.printJsonException(responseJson, "Password is required.", out);
                return;
            }

            if (password.length() > 7 && password.length() < 33) {
                int upCaseLen = password.replaceAll("[^A-Z]", "").length();
                int lowCaseLen = password.replaceAll("[^a-z]", "").length();
                int specialCharsLen = password.replaceAll("[A-Za-z0-9\\s+]", "").length();

                if (upCaseLen < 1 || lowCaseLen < 1 || specialCharsLen < 1) {
                    Utils.printJsonException(responseJson,
                            "Password must contain upper and lower case and special characters.", out);
                    return;
                }

                prepStmt = conn.prepareStatement("SELECT admin_id FROM admins WHERE username = ?");
                prepStmt.setString(1, admin);
                resultSet = prepStmt.executeQuery();

                int adminId = 0;
                if (resultSet.next()) {
                    adminId = resultSet.getInt("admin_id");
                }

                if (adminId < 1) {
                    Utils.printJsonException(responseJson, "No admin found with that username", out);
                    return;
                }

                prepStmt = conn.prepareStatement("UPDATE admins SET admin_password = ? WHERE admin_id = ?");
                prepStmt.setString(1, BCrypt.hashpw(password, BCrypt.gensalt()));
                prepStmt.setInt(2, adminId);
                prepStmt.executeUpdate();

                responseJson.put("success", true);
                responseJson.put("reason", "Successfully changed password");
                out.println(responseJson);
                //request.getRequestDispatcher("/logout").forward(request, response);
            } else {
                Utils.printJsonException(responseJson, "Password should be 8-32 characters in length.", out);
            }

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot change password at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot change password at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}