package wmdc.mobilecsa.servlet;

import org.json.simple.JSONObject;
import wmdc.mobilecsa.utils.BCrypt;
import wmdc.mobilecsa.utils.Utils;

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
 * Created by wmdcprog on 7/14/2017.
 */
@WebServlet("/authorizetransfer")
public class AuthorizeTransfer extends HttpServlet {
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(responseJson, "Login first.", out);
            return;
        }

        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement prepStmt = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            String username = request.getParameter("username");
            String rawPassword = request.getParameter("password");

            if (username == null) {
                Utils.logError("\"username\" parameter is null.");
                Utils.printJsonException(responseJson, "Username is required.", out);
                return;
            } else if (username.isEmpty()) {
                Utils.logError("\"username\" parameter is empty.");
                Utils.printJsonException(responseJson, "Username is required.", out);
                return;
            }

            if (rawPassword == null) {
                Utils.logError("\"rawPassword\" parameter is null.");
                Utils.printJsonException(responseJson, "Password is required.", out);
                return;
            } else if (rawPassword.isEmpty()) {
                Utils.logError("\"rawPassword\" parameter is empty.");
                Utils.printJsonException(responseJson, "Password is required.", out);
                return;
            }

            int passwordLen = rawPassword.length();
            if (passwordLen > 7 && passwordLen < 33) {
                int upCaseLen = rawPassword.replaceAll("[^A-Z]", "").length();
                int lowCaseLen = rawPassword.replaceAll("[^a-z]", "").length();
                int specialCharsLen = rawPassword.replaceAll("[A-Za-z0-9\\s+]", "").length();

                if (upCaseLen < 1 || lowCaseLen < 1 || specialCharsLen < 1) {
                    Utils.printJsonException(responseJson,
                            "Password must contain upper and lower case and special characters.", out);
                    return;
                }

                int adminId = getAdministratorId(username, conn);
                if (adminId < 1) {
                    Utils.logError("No administrator found with id: "+adminId);
                    Utils.printJsonException(responseJson, "No administrator found", out);
                    return;
                }

                String adminPass = getAdministratorPassword(adminId, conn);
                if (!adminPass.equals("")) {
                    if (BCrypt.checkpw(rawPassword, adminPass)) {
                        Utils.printSuccessJson(responseJson, "Correct", out);
                    } else {
                        Utils.printJsonException(responseJson,
                                "Invalid credentials. Not authorized to transfer contact.", out);
                    }
                } else {
                    Utils.logError("Administrator password is empty.");
                    Utils.printJsonException(responseJson, "Administrator password problem", out);
                }
            } else {
                Utils.printJsonException(responseJson, "Password should be 8-32 characters in length.", out);
            }
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private String getAdministratorPassword(int adminId, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT admin_password FROM admins WHERE admin_id = ?");
        prepStmt.setInt(1, adminId);
        ResultSet resultSet = prepStmt.executeQuery();

        String pass = "";
        if (resultSet.next()) {
            pass = resultSet.getString("admin_password");
        }

        prepStmt.close();
        resultSet.close();

        return pass;
    }

    private int getAdministratorId(String username, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT admin_id FROM admins WHERE username = ?");
        prepStmt.setString(1, username);
        ResultSet resultSet = prepStmt.executeQuery();

        int adminId = 0;
        if (resultSet.next()) {
            adminId = resultSet.getInt("admin_id");
        }

        prepStmt.close();
        resultSet.close();

        return adminId;
    }
}