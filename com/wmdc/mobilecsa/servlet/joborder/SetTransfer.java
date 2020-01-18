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
 * Created by wmdcprog on 12/11/2018.
 */
@WebServlet("/settransfer")
public class SetTransfer extends HttpServlet {
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

        String csaId = request.getParameter("csaId");
        String initJoId = request.getParameter("initJoId");

        if (csaId == null) {
            Utils.logError("\"csaId\" parameter is null");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (csaId.isEmpty()) {
            Utils.logError("\"csaId\" parameter is empty");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        if (initJoId == null) {
            Utils.logError("\"initJoId\" parameter is null");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (initJoId.isEmpty()) {
            Utils.logError("\"initJoId\" parameter is empty");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int initJoIdInt = Integer.parseInt(initJoId);
            int csaIdInt = Integer.parseInt(csaId);

            if (!doesInitialJoExists(initJoIdInt, conn)) {
                Utils.printJsonException(resJson, "Unable find Initial Joborder.", out);
                return;
            }

            prepStmt = conn.prepareStatement("UPDATE initial_joborder SET is_added = ? WHERE csa_id = ? AND " +
                    "initial_joborder_id = ?");
            prepStmt.setInt(1, 1);
            prepStmt.setInt(2, csaIdInt);
            prepStmt.setInt(3, initJoIdInt);
            prepStmt.executeUpdate();

            resJson.put("success", true);
            resJson.put("reason", "Set Transfer succeeded.");

            out.println(resJson);
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

    public boolean doesInitialJoExists(int initJoId, Connection conn) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT COUNT (*) AS initJoCount FROM initial_joborder " +
                "WHERE initial_joborder_id = ?");
        prepStmt.setInt(1, initJoId);
        ResultSet resultSet = prepStmt.executeQuery();

        int initJoCount = 0;
        if (resultSet.next()) {
            initJoCount = resultSet.getInt("initJoCount");
        }

        prepStmt.close();
        resultSet.close();

        if (initJoCount > 0) {
            return true;
        } else {
            return false;
        }
    }
}