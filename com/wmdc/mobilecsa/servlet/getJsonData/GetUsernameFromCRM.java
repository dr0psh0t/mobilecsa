package wmdc.mobilecsa.servlet.getJsonData;

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
import java.util.ArrayList;

/**
 * Created by wmdcprog on 11/14/2017.
 */
@WebServlet("/getusernamefromcrm")

public class GetUsernameFromCRM extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request,HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        if (!Utils.isOnline(request, getServletContext())) {
            Utils.printJsonException(responseJson, "Login first", out);
            return;
        }

        Connection conn = null;
        Connection connCRM = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());
            connCRM = Utils.getConnectionFromCRM(getServletContext());

            prepStmt = connCRM.prepareStatement("SELECT csa_id, name FROM csa WHERE status IS NOT NULL ");
            resultSet = prepStmt.executeQuery();

            ArrayList<JSONObject> arrayJson = new ArrayList<>();

            while (resultSet.next()) {
                JSONObject tempJson = new JSONObject();
                tempJson.put("text", resultSet.getString("name"));
                tempJson.put("id", resultSet.getInt("csa_id"));
                arrayJson.add(tempJson);
            }

            responseJson.put("usernames", arrayJson);
            responseJson.put("success", true);

            out.println(responseJson);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), getServletContext(), conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception",
                    e.toString(), getServletContext(), conn);

        } finally {
            Utils.closeDBResource(connCRM, prepStmt, resultSet, getServletContext());
            Utils.closeDBResource(conn, null, null, getServletContext());
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}