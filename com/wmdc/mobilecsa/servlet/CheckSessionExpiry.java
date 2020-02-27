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
 * Created by wmdcprog on 2/5/2018.
 */
@WebServlet("/checksessionexpiry")

public class CheckSessionExpiry extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        Connection conn = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            responseJson.put("domain", getDomain(conn));

            if (request.getSession().getAttribute("username") == null) {
                Utils.printJsonException(responseJson, "username session is null.", out);
            } else {
                Utils.printSuccessJson(responseJson, "username session is not null", out);
            }
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, null, null);
            out.close();
        }
    }

    private String getDomain(Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT value FROM configs WHERE config_id = ?");
        prepStmt.setInt(1, 4);
        ResultSet resultSet = prepStmt.executeQuery();

        String url = "";
        if (resultSet.next()) {
            url = resultSet.getString("value");
        }

        prepStmt.close();
        resultSet.close();

        return url;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}