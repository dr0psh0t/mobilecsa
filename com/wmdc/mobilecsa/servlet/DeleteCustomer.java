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
@WebServlet("/deletecustomer")
public class DeleteCustomer extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(responseJson, "Login", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            if (request.getParameter("customerId") == null) {
                Utils.logError("\"customerId\" parameter is null.");
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (request.getParameter("customerId").isEmpty()) {
                Utils.logError("\"customerId\" parameter is empty.");
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            int customerId = Integer.parseInt(request.getParameter("customerId"));

            prepStmt = conn.prepareStatement("SELECT COUNT (*) AS customerCount FROM customers WHERE customer_id = ?");
            prepStmt.setInt(1, customerId);
            resultSet = prepStmt.executeQuery();

            int customerCount = 0;
            if (resultSet.next()) {
                customerCount = resultSet.getInt("customerCount");
            }

            if (customerCount < 1) {
                Utils.logError("No customer found to delete using customer_id: "+customerId);
                Utils.printJsonException(responseJson, "No customers found to delete.", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT is_deleted FROM customers WHERE customer_id = ?");
            prepStmt.setInt(1, customerId);
            resultSet = prepStmt.executeQuery();

            int isDeleted = -1;
            if (resultSet.next()) {
                isDeleted = resultSet.getInt("is_deleted");
            }

            if (isDeleted == -1) {
                Utils.logError("No customer found to delete using customer_id: "+customerId);
                Utils.printJsonException(responseJson, "No customers found to delete.", out);
                return;
            }

            if (isDeleted == 1) {
                Utils.printJsonException(responseJson, "Customer already deleted", out);
                return;
            }

            String deleteStatement = "UPDATE customer SET is_deleted = 1 WHERE customer_id = "+customerId;

            prepStmt = conn.prepareStatement("UPDATE customers SET is_deleted = ? WHERE customer_id = ?");
            prepStmt.setInt(1, 1);
            prepStmt.setInt(2, customerId);
            prepStmt.executeUpdate();

            Utils.logDelete(conn, deleteStatement, (String) request.getSession(false).getAttribute("username"),
                    true);

            responseJson.put("success", true);
            responseJson.put("reason", "Customer has been deleted (flagged).");

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