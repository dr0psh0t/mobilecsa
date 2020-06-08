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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
/**
 * Created by wmdcprog on 2/14/2017.
 */

@WebServlet("/activateuser")

public class ActivateUser extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject responseJson = new JSONObject();
        PrintWriter out = response.getWriter();
        ServletContext ctx = getServletContext();

        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            HttpSession httpSession = request.getSession(false);

            String lockedUsername = request.getParameter("lockedUsername");
            String password = request.getParameter("password");
            String retypePassword = request.getParameter("retypepassword");
            String username = (String) httpSession.getAttribute("interimUsername");

            checkParameters(lockedUsername, password, retypePassword, username, responseJson, out, ctx);

            if (password.equals(retypePassword)) {
                int passwordLen = password.length();

                if ((passwordLen > 7 && passwordLen < 33)) {
                    int upCaseLen = password.replaceAll("[^A-Z]", "").length();
                    int lowCaseLen = password.replaceAll("[^a-z]", "").length();
                    int specialCharsLen = password.replaceAll("[A-Za-z0-9\\s+]", "").length();

                    if (upCaseLen < 1 || lowCaseLen < 1 || specialCharsLen < 1) {
                        Utils.printJsonException(responseJson, "Your password must contain a combination of" +
                                "\ncapital letters, small letters and special characters.", out);
                        return;
                    }

                    int csaId = getCsaId(username, conn);

                    if (csaId < 1) {
                        Utils.printJsonException(responseJson, "Username did not exist", out);
                        return;
                    }

                    String oldUserPassword = getOldUserPassword(csaId, conn);

                    if (!BCrypt.checkpw(password, oldUserPassword)) {
                        prepStmt = conn.prepareStatement("UPDATE users SET password = ?, old_password = ?, " +
                                "status = ?, is_new_password = ? WHERE csa_id = ?");

                        prepStmt.setString(1, BCrypt.hashpw(password, BCrypt.gensalt()));
                        prepStmt.setString(2, "");
                        prepStmt.setInt(3, 0);
                        prepStmt.setString(4, "0");
                        prepStmt.setInt(5, csaId);
                        prepStmt.execute();

                        httpSession.setAttribute("interimUsername", null);

                        if (!lockedUsername.equals("")) {
                            httpSession.setAttribute("username", lockedUsername);
                        }

                        responseJson.put("success", true);
                        responseJson.put("reason", "Successfully activated the user.");
                        responseJson.put("csaId", csaId);
                        responseJson.put("username", username);
                        responseJson.put("csaFullName", Utils.getCsaNameById(csaId, conn));
                        responseJson.put("localAddressNorth",
                                Utils.getPropertyValue("localAddressNorth", getServletContext()));
                        responseJson.put("publicAddressNorth",
                                Utils.getPropertyValue("publicAddressNorth", getServletContext()));
                        out.println(responseJson);

                    } else {
                        Utils.printJsonException(responseJson, "Password is old.", out);
                    }
                } else {
                    Utils.printJsonException(responseJson, "Password should be 8-32 characters in length.", out);
                }
            } else {
                Utils.printJsonException(responseJson, "Password and retype password do not match.", out);
            }

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, null, ctx);
            out.close();
        }
    }

    private int getCsaId(String username, Connection connection ) throws SQLException {
        PreparedStatement prepStmt = connection.prepareStatement("SELECT csa_id FROM users WHERE username = ?");
        prepStmt.setString(1, username);

        ResultSet resultSet = prepStmt.executeQuery();
        int csaId = 0;
        if (resultSet.next()) {
            csaId = resultSet.getInt("csa_id");
        }

        prepStmt.close();
        resultSet.close();

        return csaId;
    }

    private String getOldUserPassword(int csaId, Connection connection) throws SQLException {
        PreparedStatement prepStmt = connection.prepareStatement("SELECT old_password FROM users WHERE csa_id = ?");
        prepStmt.setInt(1, csaId);

        ResultSet resultSet = prepStmt.executeQuery();
        String hashedPassword = "";
        if (resultSet.next()) {
            hashedPassword = resultSet.getString("old_password");
        }

        prepStmt.close();
        resultSet.close();

        return hashedPassword;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    public void checkParameters(String lockedUsername, String password, String retypePassword, String username,
                                JSONObject responseJson, PrintWriter out, ServletContext ctx) {

        if (lockedUsername == null) {
            Utils.logError("\"lockedUsername\" parameter is null", ctx);
            Utils.printJsonException(responseJson, "Missing data. See logs or try again.", out);
            return;
        } else if (lockedUsername.isEmpty()) {
            Utils.logError("\"lockedUsername\" parameter is empty", ctx);
            Utils.printJsonException(responseJson, "Missing data. See logs or try again.", out);
            return;
        }

        if (password == null) {
            Utils.logError("\"password\" parameter is null", ctx);
            Utils.printJsonException(responseJson, "Password required.", out);
            return;
        } else if (password.isEmpty()) {
            Utils.logError("\"password\" parameter is empty", ctx);
            Utils.printJsonException(responseJson, "Password required.", out);
            return;
        }

        if (retypePassword == null) {
            Utils.logError("\"retypePassword\" parameter is null", ctx);
            Utils.printJsonException(responseJson, "Retype password required.", out);
            return;
        } else if (retypePassword.isEmpty()) {
            Utils.logError("\"retypePassword\" parameter is empty", ctx);
            Utils.printJsonException(responseJson, "Retype password required.", out);
            return;
        }

        if (username == null) {
            Utils.logError("\"username\" parameter is null", ctx);
            Utils.printJsonException(responseJson, "Username required.", out);
        } else if (username.isEmpty()) {
            Utils.logError("\"username\" parameter is empty", ctx);
            Utils.printJsonException(responseJson, "Username required.", out);
        }
    }
}