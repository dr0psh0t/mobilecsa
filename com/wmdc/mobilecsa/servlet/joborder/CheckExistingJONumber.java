package wmdc.mobilecsa.servlet.joborder;

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
 * Created by wmdcprog on 7/28/2018.
 */
@WebServlet("/checkexistingjonumber")
public class CheckExistingJONumber extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();

        String joNumberStr = request.getParameter("joNumber");

        if (joNumberStr == null) {
            Utils.logError("\"joNumber\" parameter is null");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (joNumberStr.isEmpty()) {
            Utils.logError("\"joNumber\" parameter is empty");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement(
                    "SELECT COUNT (*) AS joNumberCount FROM initial_joborder WHERE jo_number = ?");
            prepStmt.setString(1, joNumberStr);
            resultSet = prepStmt.executeQuery();

            int joNumberCount = 0;
            if (resultSet.next()) {
                joNumberCount = resultSet.getInt("joNumberCount");
            }

            if (joNumberCount > 0) {
                resJson.put("joExists", true);
            } else {
                resJson.put("joExists", false);
            }

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
}