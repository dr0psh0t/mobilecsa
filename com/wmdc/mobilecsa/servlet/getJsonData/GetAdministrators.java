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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by wmdcprog on 9/20/2017.
 */

@WebServlet("/getadministrators")

public class GetAdministrators extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject responseJson = new JSONObject();
        PrintWriter out = response.getWriter();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(responseJson, "Login first.", out);
            return;
        }

        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement prepStmt = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT admin_id, username, creator_id, date_stamp, status FROM admins");
            resultSet = prepStmt.executeQuery();

            JSONObject obj;
            ArrayList<JSONObject> arrayList = new ArrayList<>();

            while (resultSet.next()) {
                obj = new JSONObject();
                obj.put("adminId", resultSet.getInt("admin_id"));
                obj.put("username", resultSet.getString("username"));
                obj.put("dateStamp", resultSet.getString("date_stamp"));
                obj.put("status", resultSet.getInt("status"));
                obj.put("creator", resultSet.getInt("creator_id") != 0 ?
                        Utils.getAdministratorNameById(conn, resultSet.getInt("creator_id")) : "Super Admin");
                arrayList.add(obj);
            }

            obj = new JSONObject();
            obj.put("store", arrayList);
            obj.put("success", true);

            out.println(obj);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot get administrators at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot get administrators at the moment.", out);
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
        Utils.illegalRequest(response);
    }
}