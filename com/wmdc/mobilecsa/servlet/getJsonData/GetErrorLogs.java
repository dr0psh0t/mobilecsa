package wmdc.mobilecsa.servlet.getJsonData;

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
import java.util.ArrayList;

/**
 * Created by wmdcprog on 9/19/2018.
 */
@WebServlet("/geterrorlogs")
public class GetErrorLogs extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet result = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT * FROM error_logs");
            result = prepStmt.executeQuery();

            JSONObject obj;
            ArrayList<JSONObject> list = new ArrayList<>();

            while (result.next()) {
                obj = new JSONObject();
                obj.put("errorId", result.getInt("error_id"));
                obj.put("errorMessage", result.getString("error_message"));
                list.add(obj);
            }

            resJson.put("success", true);
            resJson.put("reason", "Success fetch.");
            resJson.put("errorLogs", list);

            out.println(resJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, result);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet result = null;

        if (request.getParameter("errorId") == null) {
            Utils.printJsonException(resJson, "Null parameters fond.", out);
            return;
        }

        try {
            int errorId = Integer.parseInt(request.getParameter("errorId"));

            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT * FROM error_logs WHERE error_id = ?");
            prepStmt.setInt(1, errorId);
            result = prepStmt.executeQuery();

            if (result.next()) {
                resJson.put("success", true);
                resJson.put("reason", "Fetch success.");
                resJson.put("errorId", result.getInt("error_id"));
                resJson.put("errorMessage", result.getString("errorMessage"));
            } else {
                resJson.put("success", false);
                resJson.put("reason", "Empty result set.");
            }

            out.println(resJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, result);
            out.close();
        }
    }
}