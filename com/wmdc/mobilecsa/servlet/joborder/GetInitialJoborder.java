package wmdc.mobilecsa.servlet.joborder;

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
 * Created by wmdcprog on 6/7/2018.
 */
@WebServlet("/getinitialjoborder")
public class GetInitialJoborder extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject resJson = new JSONObject();
        PrintWriter out = response.getWriter();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        if (request.getParameter("initialJoborderId") == null) {
            Utils.logError("\"initialJoborderId\" parameter is null");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (request.getParameter("initialJoborderId").isEmpty()) {
            Utils.logError("\"initialJoborderId\" parameter is empty");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int initialJoborderId = Integer.parseInt(request.getParameter("initialJoborderId"));

            prepStmt = conn.prepareStatement("SELECT customer_source, mobile, purchase_order, po_date, model_id, " +
                    "make, category, serial_num, date_received, date_committed, reference_number, remarks, " +
                    "initial_joborder_id, customer, is_added, jo_number, customer_id FROM initial_joborder " +
                    "WHERE initial_joborder_id = ?");
            prepStmt.setInt(1, initialJoborderId);
            resultSet = prepStmt.executeQuery();

            JSONObject object = null;

            if (resultSet.next()) {
                object = new JSONObject();
                object.put("initialJoborderId", resultSet.getInt("initial_joborder_id"));
                object.put("customer", resultSet.getString("customer"));
                object.put("source", resultSet.getString("customer_source"));
                object.put("mobile", resultSet.getString("mobile"));
                object.put("purchaseOrder", resultSet.getString("purchase_order"));
                object.put("poDate", resultSet.getString("po_date"));
                object.put("model", resultSet.getInt("model_id"));
                object.put("make", resultSet.getString("make"));
                object.put("category", resultSet.getString("category"));
                object.put("serialNo", resultSet.getString("serial_num"));
                object.put("dateReceived", resultSet.getString("date_received"));
                object.put("dateCommitted", resultSet.getString("date_committed"));
                object.put("referenceNo", resultSet.getString("reference_number"));
                object.put("remarks", resultSet.getString("remarks"));
                object.put("isAdded", resultSet.getInt("is_added"));
                object.put("joNumber", resultSet.getString("jo_number"));
                object.put("customerId", resultSet.getInt("customer_id"));
            }

            resJson.put("success", true);
            resJson.put("quotation", object);

            out.print(resJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString());
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