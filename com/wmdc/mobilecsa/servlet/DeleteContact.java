package wmdc.mobilecsa.servlet;

import org.json.simple.JSONObject;
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
 * Created by wmdcprog on 5/30/2017.
 */
@WebServlet("/deletecontact")
public class DeleteContact extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject responseJson = new JSONObject();
        PrintWriter out = response.getWriter();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(responseJson, "Login first.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            if (request.getParameter("contactId") == null) {
                Utils.logError("\"contactId\" parameter is null");
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (request.getParameter("contactId").isEmpty()) {
                Utils.logError("\"contactId\" parameter is empty");
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            int contactId = Integer.parseInt(request.getParameter("contactId"));
            prepStmt = conn.prepareStatement("SELECT COUNT (*) as contactCount FROM contacts WHERE contact_id = ?");
            prepStmt.setInt(1, contactId);
            resultSet = prepStmt.executeQuery();

            int contactCount = 0;
            if (resultSet.next()) {
                contactCount = resultSet.getInt("contactCount");
            }

            if (contactCount < 1) {
                Utils.logError("No contact found to delete using id: "+contactId);
                Utils.printJsonException(responseJson, "No contact found to delete.", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT is_deleted FROM contacts WHERE contact_id = ?");
            prepStmt.setInt(1, contactId);
            resultSet = prepStmt.executeQuery();

            int isDeleted = -1;
            if (resultSet.next()) {
                isDeleted = resultSet.getInt("is_deleted");
            }

            if (isDeleted == -1) {
                Utils.printJsonException(responseJson, "No contact found to delete.", out);
                return;
            }

            if (isDeleted == 1) {
                Utils.printJsonException(responseJson, "Contact already deleted.", out);
                return;
            }

            String deleteStatement = "UPDATE contacts SET is_deleted = 1 WHERE contact_id = "+contactId;

            prepStmt = conn.prepareStatement("UPDATE contacts SET is_deleted = ? WHERE contact_id = ?");
            prepStmt.setInt(1, 1);
            prepStmt.setInt(2, contactId);
            prepStmt.executeUpdate();

            Utils.logDelete(conn, deleteStatement, (String) request.getSession(false).getAttribute("username"), false);

            responseJson.put("success", true);
            responseJson.put("reason", "Contact has been deleted");

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