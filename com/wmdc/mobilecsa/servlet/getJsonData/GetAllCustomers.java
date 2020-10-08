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
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by wmdcprog on 4/27/2018.
 */

@WebServlet("/getallcustomers")

public class GetAllCustomers extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

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
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT customer_id, lastname, firstname, company FROM customers");
            resultSet = prepStmt.executeQuery();

            String lastname;
            String firstname;
            String company;

            JSONObject obj;
            ArrayList<JSONObject> list = new ArrayList<>();

            while (resultSet.next()) {
                obj = new JSONObject();
                lastname = resultSet.getString("lastname");
                firstname = resultSet.getString("firstname");
                company = resultSet.getString("company");
                obj.put("customerId", resultSet.getInt("customer_id"));
                obj.put("lastname", resultSet.getString("lastname"));
                obj.put("firstname", resultSet.getString("firstname"));
                obj.put("company", resultSet.getString("company"));
                obj.put("customer", (company.isEmpty()) ? (lastname+", "+firstname) : company);
                list.add(obj);
            }

            Collections.sort(list, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject o1, JSONObject o2) {
                    return o1.get("customer").toString().compareTo(o2.get("customer").toString());
                }
            });

            responseJson.put("success", true);
            responseJson.put("reason", "Added new job order");
            responseJson.put("customerStore", list);

            out.println(responseJson);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot get customers at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot get customers at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString(),
                    ctx, conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }
}