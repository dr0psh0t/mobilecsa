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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/checkinitial")

public class CheckInitial extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, getServletContext())) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            String jono = request.getParameter("jono");

            if (jono == null) {
                Utils.logError("\"jono\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (jono.isEmpty()) {
                Utils.logError("\"jono\" parameter is empty", ctx);
                Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
                return;
            }

            prepStmt = conn.prepareStatement(
                    "SELECT COUNT(*) AS initJoCount FROM initial_joborder WHERE jo_number = ?");
            prepStmt.setString(1, jono);
            resultSet = prepStmt.executeQuery();

            int initJoCount = 0;
            if (resultSet.next()) {
                initJoCount = resultSet.getInt("initJoCount");
            }

            resJson.put("success", true);

            if (initJoCount < 1) {
                resJson.put("reason", "There is no initial joborder encoded.");
            } else {
                resJson.put("reason", "There is initial joborder encoded.");
            }

            out.println(resJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException",
                    sqe.toString(), getServletContext());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString(),
                    getServletContext());
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
