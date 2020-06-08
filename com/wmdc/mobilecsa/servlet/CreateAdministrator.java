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
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wmdcprog on 9/19/2017.
 */
@WebServlet("/createadministrator")

public class CreateAdministrator extends HttpServlet {
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

        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        final GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String creator = request.getParameter("creator");

            if (username == null) {
                Utils.logError("\"username\" parameter is null.", ctx);
                Utils.printJsonException(responseJson, "Username is required.", out);
                return;
            } else if (username.isEmpty()) {
                Utils.logError("\"username\" parameter is empty.", ctx);
                Utils.printJsonException(responseJson, "Username is required.", out);
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

            if (creator == null) {
                Utils.logError("\"creator\" parameter is null.", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (creator.isEmpty()) {
                Utils.logError("\"creator\" parameter is empty.", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
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

                prepStmt = conn.prepareStatement("SELECT COUNT(*) AS adminCount FROM admins WHERE username = ?");
                prepStmt.setString(1, username);
                resultSet = prepStmt.executeQuery();

                int adminCount = 0;
                if (resultSet.next()) {
                    adminCount = resultSet.getInt("adminCount");
                }

                prepStmt = conn.prepareStatement("SELECT COUNT(*) AS userCount FROM users WHERE username = ?");
                prepStmt.setString(1, username);
                resultSet = prepStmt.executeQuery();

                int userCount = 0;
                if (resultSet.next()) {
                    userCount = resultSet.getInt("userCount");
                }

                prepStmt = conn.prepareStatement("SELECT admin_id FROM admins WHERE username = ?");
                prepStmt.setString(1, creator);
                resultSet = prepStmt.executeQuery();

                int creatorId = 0;
                if (resultSet.next()) {
                    creatorId = resultSet.getInt("admin_id");
                }

                if (adminCount > 0 || userCount > 0) {
                    Utils.printJsonException(responseJson, "Username is taken.", out);
                    return;
                }

                if (creatorId < 1) {
                    Utils.logError("No creator found using username: "+creator, ctx);
                    Utils.printJsonException(responseJson, "Creator not found.", out);
                    return;
                }

                String secretKey = googleAuthenticatorKey.getKey();

                prepStmt = conn.prepareStatement("INSERT INTO admins (username, admin_password, corp_id, creator_id, " +
                        "date_stamp, secret_key, status) VALUES (?, ?, ?, ?, NOW(), ?, ?)");
                prepStmt.setString(1, username);
                prepStmt.setString(2, BCrypt.hashpw(password, BCrypt.gensalt()));
                prepStmt.setInt(3, 0);
                prepStmt.setInt(4, creatorId);
                prepStmt.setString(5, secretKey);
                prepStmt.setInt(6, 0);
                prepStmt.executeUpdate();

                responseJson.put("success", true);
                responseJson.put("reason", "Successfully added administrator");
                out.println(responseJson);
            } else {
                Utils.printJsonException(responseJson, "Password is too short", out);
            }

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
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