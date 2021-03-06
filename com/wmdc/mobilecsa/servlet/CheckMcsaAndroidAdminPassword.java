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

/**
 * Created by wmdcprog on 11/26/2018.
 */
@WebServlet("/checkmcsaandroidadminpassword")

public class CheckMcsaAndroidAdminPassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();
        ServletContext ctx = getServletContext();

        String mcsaAndroidAdminPassword = request.getParameter("mcsaAndroidAdminPassword");
        if (mcsaAndroidAdminPassword == null) {
            Utils.logError("\"mcsaAndroidAdminPassword\" parameter is null.", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (mcsaAndroidAdminPassword.isEmpty()) {
            Utils.logError("\"mcsaAndroidAdminPassword\" parameter is empty.", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT `value` FROM `configs` WHERE `key` = ?");
            prepStmt.setString(1, "mcsa_android_admin_password");

            resultSet = prepStmt.executeQuery();
            String password = "";
            if (resultSet.next()) {
                password = resultSet.getString("value");
            }

            if (BCrypt.checkpw(mcsaAndroidAdminPassword, password)) {
                resJson.put("success", true);
                resJson.put("reason", "Password is correct.");
            } else {
                resJson.put("success", false);
                resJson.put("reason", "Password is incorrect.");
            }

            out.println(resJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot check password at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot check password at the moment.", out);
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