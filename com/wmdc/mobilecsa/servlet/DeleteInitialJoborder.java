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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wmdcprog on 11/10/2018.
 */
@WebServlet("/deleteinitialjoborder")

public class DeleteInitialJoborder extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        String initialJoborderIdStr = request.getParameter("initialJoborderId");

        if (initialJoborderIdStr == null) {
            Utils.logError("\"initialJoborderIdStr\" parameter is null");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (initialJoborderIdStr.isEmpty()) {
            Utils.logError("\"initialJoborderIdStr\" parameter is empty");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int initialJoborderId = Integer.parseInt(initialJoborderIdStr);

            if (getJoborderCountById(initialJoborderId, conn) < 1) {
                Utils.logError("No initial joborder found to delete using joborder id: "+initialJoborderId);
                Utils.printJsonException(resJson, "No initial joborder found to delete.", out);
                return;
            }

            prepStmt = conn.prepareStatement("DELETE FROM initial_joborder WHERE initial_joborder_id = ?");
            prepStmt.setInt(1, initialJoborderId);

            int rowsAffected = prepStmt.executeUpdate();
            if (rowsAffected > 0) {
                prepStmt = conn.prepareStatement("DELETE FROM initial_joborder_image WHERE initial_joborder_id = ?");
                prepStmt.setInt(1, initialJoborderId);
                prepStmt.execute();
                Utils.printSuccessJson(resJson, "Initial joborder has been deleted.", out);
            } else {
                Utils.logError("A problem has occured in deleting initial joborder. Rows affected: "+rowsAffected);
                Utils.printJsonException(resJson, "A problem has occured in deleting initial joborder. " +
                        "See logs or try again", out);
            }
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, null);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    private int getJoborderCountById(int initialJoborderId, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT (*) AS joCount FROM initial_joborder WHERE initial_joborder_id = ?");
        prepStmt.setInt(1, initialJoborderId);
        ResultSet resultSet = prepStmt.executeQuery();

        int joCount = 0;
        if (resultSet.next()) {
            joCount = resultSet.getInt("joCount");
        }

        prepStmt.close();
        resultSet.close();

        return joCount;
    }
}