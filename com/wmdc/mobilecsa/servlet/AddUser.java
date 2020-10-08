package wmdc.mobilecsa.servlet;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
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

@WebServlet("/adduser")

public class AddUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject responseJson = new JSONObject();
        PrintWriter out = response.getWriter();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(responseJson, "Login first", out);
            return;
        }

        HttpSession httpSession = request.getSession(false);

        Connection conn = null;
        PreparedStatement prepStmt = null;

        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        final GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            String name = request.getParameter("name");
            String password = request.getParameter("password");
            String adminUsername = request.getParameter("adminUsername");
            String username = request.getParameter("username");

            if (name == null) {
                Utils.logError("\"name\" parameter is null", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (name.isEmpty()) {
                Utils.logError("\"name\" parameter is empty", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (password == null) {
                Utils.logError("\"password\" parameter is null", ctx);
                Utils.printJsonException(responseJson, "Password is required.", out);
                return;
            } else if (password.isEmpty()) {
                Utils.logError("\"password\" parameter is empty", ctx);
                Utils.printJsonException(responseJson, "Password is required.", out);
                return;
            }

            if (adminUsername == null) {
                Utils.logError("\"adminUsername\" parameter is null", null);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (adminUsername.isEmpty()) {
                Utils.logError("\"adminUsername\" parameter is empty", null);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (username == null) {
                Utils.logError("\"username\" parameter is null", null);
                Utils.printJsonException(responseJson, "Username is required.", out);
                return;
            } else if (username.isEmpty()) {
                Utils.logError("\"username\" parameter is empty", null);
                Utils.printJsonException(responseJson, "Username required.", out);
                return;
            }

            int passwordLen = password.length();
            if (!(passwordLen > 7 && passwordLen < 33)) {
                Utils.printJsonException(responseJson, "Password should be 8-32 characters in length.", out);
                return;
            }

            int upCaseLen = password.replaceAll("[^A-Z]", "").length();
            int lowCaseLen = password.replaceAll("[^a-z]", "").length();
            int specialCharsLen = password.replaceAll("[A-Za-z0-9\\s+]", "").length();

            if (upCaseLen < 1 || lowCaseLen < 1 || specialCharsLen < 1) {
                Utils.printJsonException(responseJson,
                        "Password must contain capital and small letters and symbols.", out);
                return;
            }

            int userCount = getUserCount(conn, username);
            int adminCount = getAdminCount(conn, username);

            if (userCount > 0 || adminCount > 0) {
                Utils.printJsonException(responseJson, "The username is already taken", out);
                return;
            }

            int creatorId = getCreatorId(conn, adminUsername);
            if (creatorId < 1) {
                Utils.printJsonException(responseJson, "Cannot find admin with that username", out);
                return;
            }

            String secretKey = googleAuthenticatorKey.getKey();
            prepStmt = conn.prepareStatement("INSERT INTO users (username, corp_id, date_stamp, creator_id, " +
                    "password, secret_key, status, is_new_password, old_password, lastname, firstname, csa_id) " +
                    "VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            prepStmt.setString(1, username);
            prepStmt.setInt(2, 0);
            prepStmt.setInt(3, creatorId);
            prepStmt.setString(4, "");
            prepStmt.setString(5, secretKey);
            prepStmt.setInt(6, 1);
            prepStmt.setString(7, "1");
            prepStmt.setString(8, BCrypt.hashpw(password, BCrypt.gensalt()));
            prepStmt.setString(9, "no-name");
            prepStmt.setString(10, "no-name");
            prepStmt.setInt(11, Integer.parseInt(name));
            prepStmt.execute();

            responseJson.put("success", true);
            responseJson.put("reason", "Successfully added user.");

            httpSession.setAttribute("tempUsername", username);
            out.println(responseJson);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot add user at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot add user at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, null, ctx);
            out.close();
        }
    }

    private int getCreatorId(Connection conn, String adminUsername) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT admin_id FROM admins WHERE username = ?");
        prepStmt.setString(1, adminUsername);
        ResultSet resultSet = prepStmt.executeQuery();

        int adminId = 0;
        if (resultSet.next()) {
            adminId = resultSet.getInt("admin_id");
        }

        prepStmt.close();
        resultSet.close();

        return adminId;
    }

    private int getAdminCount(Connection conn, String username) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS adminCount FROM admins WHERE username = ?");
        prepStmt.setString(1, username);
        ResultSet resultSet = prepStmt.executeQuery();

        int adminCount = 0;
        if (resultSet.next()) {
            adminCount = resultSet.getInt("adminCount");
        }

        prepStmt.close();
        resultSet.close();

        return adminCount;
    }

    private int getUserCount(Connection conn, String username) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS userCount FROM users WHERE username = ?");
        prepStmt.setString(1, username);
        ResultSet resultSet = prepStmt.executeQuery();

        int userCount = 0;
        if (resultSet.next()) {
            userCount = resultSet.getInt("userCount");
        }

        prepStmt.close();
        resultSet.close();

        return userCount;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}