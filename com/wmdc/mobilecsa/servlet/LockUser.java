package wmdc.mobilecsa.servlet;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/*** Created by wmdcprog on 3/25/2017.*/
@WebServlet("/lock")

public class LockUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();
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

            String csaIdParam = request.getParameter("csaId");

            if (csaIdParam == null) {
                Utils.logError("\"csaId\" parameter is null.", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (csaIdParam.isEmpty()) {
                Utils.logError("\"csaId\" parameter is empty.", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            int csaId = Integer.parseInt(csaIdParam);

            prepStmt = conn.prepareStatement("SELECT COUNT(*) AS userCount FROM users WHERE csa_id = ?");
            prepStmt.setInt(1, csaId);
            resultSet = prepStmt.executeQuery();

            int count = 0;
            if (resultSet.next()) {
                count = resultSet.getInt("userCount");
            }
            if (count < 1) {
                Utils.logError("No user found using id: "+csaId, ctx);
                Utils.printJsonException(responseJson, "User not found", out);
                return;
            }

            String hashedPW = "";
            prepStmt = conn.prepareStatement("SELECT password FROM users WHERE csa_id = ?");
            prepStmt.setInt(1, csaId);
            resultSet = prepStmt.executeQuery();

            if (resultSet.next()) {
                hashedPW = resultSet.getString("password");
            }

            prepStmt = conn.prepareStatement("UPDATE users SET password = ?, status = ?, is_new_password = ?, " +
                    "old_password = ? WHERE csa_id = ?");
            prepStmt.setString(1, "");
            prepStmt.setInt(2, 1);
            prepStmt.setString(3, "1");
            prepStmt.setString(4, hashedPW);
            prepStmt.setInt(5, csaId);
            prepStmt.execute();

            responseJson.put("success", true);
            responseJson.put("reason", "Successfully locked user");
            out.println(responseJson);
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