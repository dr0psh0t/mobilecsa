package wmdc.mobilecsa.servlet.getJsonData;

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

@WebServlet("/getcontacts")

public class GetContacts extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(responseJson, "Login first.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT contact_id, lastname, firstname, job_position, er, mf_gm, " +
                    "calib, is_deleted, is_transferred FROM contacts");
            resultSet = prepStmt.executeQuery();

            JSONObject obj;
            ArrayList<JSONObject> arrayList = new ArrayList<>();

            while (resultSet.next()) {
                obj = new JSONObject();
                obj.put("contactId", resultSet.getInt("contact_id"));
                obj.put("lastname", resultSet.getString("lastname"));
                obj.put("firstname", resultSet.getString("firstname"));
                obj.put("jobPosition", resultSet.getString("job_position"));
                obj.put("er", resultSet.getDouble("er"));
                obj.put("mf", resultSet.getDouble("mf_gm"));
                obj.put("calib", resultSet.getDouble("calib"));
                obj.put("isDeleted", resultSet.getInt("is_deleted"));
                obj.put("isTransferred", resultSet.getInt("is_transferred"));
                arrayList.add(obj);
            }

            obj = new JSONObject();
            obj.put("store", arrayList);
            obj.put("success", true);

            out.println(obj);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot get contacts at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot get contacts at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString(),
                    ctx, conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.redirectToLogin(response);
    }
}