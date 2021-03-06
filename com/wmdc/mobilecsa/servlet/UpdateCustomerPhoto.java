package wmdc.mobilecsa.servlet;

import wmdc.mobilecsa.utils.Utils;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wmdcprog on 7/5/2019.
 */
@MultipartConfig(fileSizeThreshold=1024*1024*6, // 5MB
        maxFileSize=1024*1024*3,      // 3MB
        maxRequestSize=1024*1024*50)   // 50MB
@WebServlet("/updatecustomerphoto")

public class UpdateCustomerPhoto extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject resJson = new JSONObject();
        PrintWriter out = response.getWriter();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(resJson, "Login to continue.", out);
            return;
        }

        String customerIdParam = request.getParameter("customerId");
        Part customerPhoto = request.getPart("customerPhoto");

        if (customerIdParam == null) {
            Utils.logError("\"customerId\" parameter is null.", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (customerIdParam.isEmpty()) {
            Utils.logError("\"customerId\" parameter is empty.", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        if (customerPhoto == null) {
            Utils.printJsonException(resJson, "Customer photo parameter is null", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int customerId = Integer.parseInt(customerIdParam);
            if (getCustomerCount(conn, customerId) < 1)  {
                Utils.logError("No customer found to update using customerId: "+customerId, ctx);
                Utils.printJsonException(resJson, "No customer found to update.", out);
                return;
            }

            prepStmt = conn.prepareStatement("UPDATE customers SET profile_photo = ? WHERE customer_id = ?");
            prepStmt.setBlob(1, customerPhoto.getInputStream());
            prepStmt.setInt(2, customerId);
            int updateCount = prepStmt.executeUpdate();

            ctx.log("No# of customers updated photo: "+updateCount);

            if (updateCount < 1) {
                Utils.printJsonException(resJson, "No customer photo updated.", out);
            } else {
                Utils.printSuccessJson(resJson, "Successfully updated customer photo.", out);
            }
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(resJson, "Cannot update photo at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(resJson, "Cannot update photo at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, null, ctx);
            out.close();
        }
    }

    private int getCustomerCount(Connection conn, int customerId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS customerCount FROM customers WHERE customer_id = ?" );
        prepStmt.setInt(1, customerId);
        ResultSet resultSet = prepStmt.executeQuery();

        int customerCount = 0;
        if (resultSet.next()) {
            customerCount = resultSet.getInt("customerCount");
        }

        prepStmt.close();
        resultSet.close();

        return customerCount;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}