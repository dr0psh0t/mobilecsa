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

/**
 * Created by wmdcprog on 2/21/2017.
 */
@WebServlet("/getcsa")

public class GetCSAs extends HttpServlet {
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

            prepStmt = conn.prepareStatement("SELECT csa_id, username, lastname, firstname, is_new_password, status "+
                            "FROM users WHERE lastname <> ? AND firstname <> ?");
            prepStmt.setString(1, "");
            prepStmt.setString(2, "");
            resultSet = prepStmt.executeQuery();

            JSONObject obj;
            ArrayList<JSONObject> arrayList = new ArrayList<>();

            while (resultSet.next()) {
                obj = new JSONObject();
                obj.put("csaId", resultSet.getInt("csa_id"));
                obj.put("username", resultSet.getString("username"));
                obj.put("lastname", resultSet.getString("lastname"));
                obj.put("firstname", resultSet.getString("firstname"));
                obj.put("passwordStatus", resultSet.getString("is_new_password"));
                obj.put("status", resultSet.getInt("status"));
                arrayList.add(obj);
            }

            obj = new JSONObject();
            obj.put("store", arrayList);
            obj.put("success", true);

            out.println(obj);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot get csas at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot get csa at the moment.", out);
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