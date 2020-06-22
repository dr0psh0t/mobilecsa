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
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wmdcprog on 8/4/2018.
 */
@WebServlet("/updateinitialjoborderwithoutphoto")

public class UpdateInitialJoborderWithoutPhoto extends HttpServlet {

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

        String initialJoborderId = request.getParameter("initialJoborderId");
        String customerId = request.getParameter("customerId");
        String customer = request.getParameter("customer");
        String source = request.getParameter("source");
        String mobile = request.getParameter("mobile");
        String purchaseOrder = request.getParameter("purchaseOrder");
        String poDate = request.getParameter("poDate");
        String make = request.getParameter("make");
        String cat = request.getParameter("cat");
        String modelId = request.getParameter("modelId");
        String serialNo = request.getParameter("serialNo");
        String dateReceive = request.getParameter("dateReceive");
        String dateCommit = request.getParameter("dateCommit");
        String refNo = request.getParameter("refNo");
        String remarks = request.getParameter("remarks");
        String preparedBy = request.getParameter("preparedBy");
        String imageType = request.getParameter("imageType");
        String joSignature = request.getParameter("joSignature");
        String joNumber = request.getParameter("joNumber");

        if (Utils.inspectNullRef(new ArrayList<>(Arrays.asList(
                initialJoborderId, customerId, customer, source, mobile, purchaseOrder,
                poDate, make, cat, modelId, serialNo, dateReceive, dateCommit, refNo,
                remarks, preparedBy, imageType, joSignature, joNumber)))) {
            Utils.printJsonException(resJson, "Null parameters found.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int customerIdInt = Integer.parseInt(customerId);
            int modelIdInt = Integer.parseInt(modelId);
            int preparedByInt = Integer.parseInt(preparedBy);
            int initialJoborderIdInt = Integer.parseInt(initialJoborderId);

            prepStmt = conn.prepareStatement("UPDATE initial_joborder SET jo_number = ?, customer_id = ?, " +
                    "customer_source = ?, mobile = ?, model_id = ?, serial_num = ?, po_date = ?, date_received = ?, " +
                    "date_committed = ?, category = ?, prepared_by = ?, remarks = ?, purchase_order = ?, " +
                    "reference_number = ?, make = ?, csa_id = ?, customer = ? WHERE initial_joborder_id = ?");
            prepStmt.setString(1, joNumber);
            prepStmt.setInt(2, customerIdInt);
            prepStmt.setString(3, source);
            prepStmt.setString(4, mobile);
            prepStmt.setInt(5, modelIdInt);
            prepStmt.setString(6, serialNo);
            prepStmt.setString(7, poDate);
            prepStmt.setString(8, dateReceive);
            prepStmt.setString(9, dateCommit);
            prepStmt.setString(10, cat);
            prepStmt.setInt(11, preparedByInt);
            prepStmt.setString(12, remarks);
            prepStmt.setString(13, purchaseOrder);
            prepStmt.setString(14, refNo);
            prepStmt.setString(15, make);
            prepStmt.setInt(16, preparedByInt);
            prepStmt.setString(17, customer);
            prepStmt.setInt(18, initialJoborderIdInt);
            prepStmt.executeUpdate();

            updateSignature(conn, joSignature, initialJoborderIdInt, ctx);

            resJson.put("success", true);
            resJson.put("reason", "Successfully updated joborder");

            out.println(resJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException",
                    sqe.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, null, ctx);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    private void updateSignature(Connection conn, String joSignature, int initialJoborderId, ServletContext ctx) throws Exception {
        if (!joSignature.isEmpty()) {
            InputStream signatureStream = Utils.getSignatureInputStream(joSignature, ctx, conn);

            PreparedStatement prepStmt = conn.prepareStatement("UPDATE initial_joborder SET signature = ? " +
                    "WHERE initial_joborder_id = ?");
            prepStmt.setBlob(1, signatureStream);
            prepStmt.setInt(2, initialJoborderId);
            prepStmt.executeUpdate();
            prepStmt.close();
        }
    }
}