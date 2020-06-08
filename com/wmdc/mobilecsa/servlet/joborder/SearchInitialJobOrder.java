package wmdc.mobilecsa.servlet.joborder;

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
import java.util.ArrayList;

/**
 * Created by wmdcprog on 4/24/2018.
 */
@WebServlet("/searchinitialjoborder")
public class SearchInitialJobOrder extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            String customer = request.getParameter("customer");

            if (customer == null) {
                Utils.logError("\"customer\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Missing data. See logs or try again.", out);
                return;
            }

            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            if (getCustomerCount(customer, conn) < 1) {
                Utils.printJsonException(resJson, "No customer found.", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT initial_joborder_id, jo_number, customer_id, customer_source, " +
                    "mobile, model_id, serial_num, po_date, date_received, date_committed, category, prepared_by, " +
                    "remarks, purchase_order, reference_number, make, date_stamp, customer FROM initial_joborder " +
                    "WHERE customer like ?");
            prepStmt.setString(1, customer+"%");
            resultSet = prepStmt.executeQuery();

            JSONObject obj;
            ArrayList<JSONObject> arrayList = new ArrayList<>();

            while (resultSet.next()) {
                obj = new JSONObject();
                obj.put("initialJoborderId", resultSet.getInt("initial_joborder_id"));
                obj.put("joNumber", resultSet.getInt("jo_number"));
                obj.put("customerId", resultSet.getInt("customer_id"));
                obj.put("source", resultSet.getString("customer_source"));
                obj.put("customer", resultSet.getString("customer"));
                obj.put("mobile", resultSet.getString("mobile"));
                obj.put("modelId", resultSet.getInt("model_id"));
                obj.put("make", resultSet.getString("make"));
                obj.put("serialNo", resultSet.getString("serial_num"));
                obj.put("poDate", resultSet.getString("po_date"));
                obj.put("dateReceive", resultSet.getString("date_received"));
                obj.put("dateCommit", resultSet.getString("date_committed"));
                obj.put("category", resultSet.getString("category"));
                obj.put("preparedBy", resultSet.getInt("prepared_by"));
                obj.put("remarks", resultSet.getString("remarks"));
                obj.put("purchaseOrder", resultSet.getString("purchase_order"));
                obj.put("referenceNo", resultSet.getString("reference_number"));
                obj.put("csa", Utils.getCsaNameById(resultSet.getInt("prepared_by"), conn));
                obj.put("dateStamp", resultSet.getString("date_stamp"));
                arrayList.add(obj);
            }

            resJson.put("success", true);
            resJson.put("reason", "OK");
            resJson.put("result", arrayList);

            out.println(resJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException",
                    sqe.toString(), ctx);
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

    private int getCustomerCount(String customer, Connection conn) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT COUNT(*) AS customerCount FROM initial_joborder " +
                "WHERE customer LIKE ?");
        prepStmt.setString(1, customer+"%");
        ResultSet resultSet = prepStmt.executeQuery();

        int count = 0;
        while (resultSet.next()) {
            count = resultSet.getInt("customerCount");
        }

        prepStmt.close();
        resultSet.close();

        return count;
    }
}