package wmdc.mobilecsa.servlet;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by wmdcprog on 9/19/2017.
 */
@WebServlet("/addsalesman")
public class AddSalesman extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject responseJson = new JSONObject();
        PrintWriter out = response.getWriter();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(responseJson, "Login to continue.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            String username = request.getParameter("username");
            String adminUsername = request.getParameter("admin");
            String firstname = request.getParameter("firstname");
            String lastname = request.getParameter("lastname");

            if (username == null) {
                Utils.logError("\"username\" parameter is null");
                Utils.printJsonException(responseJson, "Username is required.", out);
                return;
            } else if (username.isEmpty()) {
                Utils.logError("\"username\" parameter is empty");
                Utils.printJsonException(responseJson, "Username is required.", out);
                return;
            }

            if (adminUsername == null) {
                Utils.logError("\"adminUsername\" parameter is null");
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (adminUsername.isEmpty()) {
                Utils.logError("\"adminUsername\" parameter is empty");
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (firstname == null) {
                Utils.logError("\"firstname\" parameter is null");
                Utils.printJsonException(responseJson, "Firstname required.", out);
                return;
            } else if (firstname.isEmpty()) {
                Utils.logError("\"firstname\" parameter is empty");
                Utils.printJsonException(responseJson, "Firstname required.", out);
                return;
            }

            if (lastname == null) {
                Utils.logError("\"lastname\" parameter is null");
                Utils.printJsonException(responseJson, "Lastname required.", out);
                return;
            } else if (lastname.isEmpty()) {
                Utils.logError("\"lastname\" parameter is empty");
                Utils.printJsonException(responseJson, "Lastname required.", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT COUNT (*) AS userCount FROM users WHERE username = ?");
            prepStmt.setString(1, username);
            resultSet = prepStmt.executeQuery();

            int userCount = 0;
            if (resultSet.next()) {
                userCount = resultSet.getInt("userCount");
            }

            prepStmt =  conn.prepareStatement("SELECT COUNT (*) AS adminCount FROM admins WHERE username = ?");
            prepStmt.setString(1, username);
            resultSet = prepStmt.executeQuery();

            int adminCount = 0;
            if (resultSet.next()) {
                adminCount = resultSet.getInt("adminCount");
            }

            if (userCount > 0 || adminCount > 0) {
                Utils.printJsonException(responseJson, "Username already taken.", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT admin_id FROM admins WHERE username = ?");
            prepStmt.setString(1, adminUsername);
            resultSet = prepStmt.executeQuery();

            int adminId = 0;
            if (resultSet.next()) {
                adminId = resultSet.getInt("admin_id");
            }

            if (adminId < 1) {
                Utils.printJsonException(responseJson, "Cannot find csa / admin id with that username", out);
                return;
            }

            prepStmt = conn.prepareStatement("INSERT INTO users (username, corp_id, date_stamp, creator_id, " +
                            "password, is_new_password, status, lastname, firstname) " +
                            "VALUES (?, ?, NOW(), ?, ?, ?, ?, ?, ?)");

            prepStmt.setString(1, username);
            prepStmt.setInt(2, 0);
            prepStmt.setInt(3, adminId);
            prepStmt.setString(4, "");
            prepStmt.setString(5, "1");
            prepStmt.setInt(6, 1);
            prepStmt.setString(7, lastname);
            prepStmt.setString(8, firstname);
            prepStmt.executeUpdate();

            responseJson.put("success", true);
            responseJson.put("reason", "Successfully saved new user");
            out.println(responseJson);
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}