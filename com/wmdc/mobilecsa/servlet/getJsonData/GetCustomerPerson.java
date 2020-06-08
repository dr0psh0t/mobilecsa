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
 * Created by wmdcprog on 3/17/2017.
 */
@WebServlet("/getcustomerperson")
public class GetCustomerPerson extends HttpServlet {
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

            prepStmt = conn.prepareStatement("SELECT customer_id, lastname, firstname, tel_num, fax_num, country_code, " +
                    "date_of_birth, date_added, assigned_csa, is_deleted, area_code, is_transferred FROM customers " +
                    "WHERE company = ? AND contact_person = ?");
            prepStmt.setString(1, "");
            prepStmt.setString(2, "");
            resultSet = prepStmt.executeQuery();

            ArrayList<JSONObject> arrayList = new ArrayList<>();
            JSONObject obj;

            while (resultSet.next()) {
                obj = new JSONObject();
                obj.put("customerId", resultSet.getInt("customer_id"));
                obj.put("lastname", resultSet.getString("lastname"));
                obj.put("firstname", resultSet.getString("firstname"));
                obj.put("telNum", Utils.addPhoneDash(resultSet.getInt("tel_num"),
                        resultSet.getInt("country_code"), resultSet.getInt("area_code")));
                obj.put("faxNum", Utils.addPhoneDash(resultSet.getInt("fax_num"),
                        resultSet.getInt("country_code"), resultSet.getInt("area_code")));
                obj.put("dateOfBirth", resultSet.getString("date_of_birth"));
                obj.put("dateAdded", resultSet.getString("date_added"));
                obj.put("assignedCsa", Utils.getCsaNameById(resultSet.getInt("assigned_csa"), conn));
                obj.put("isDeleted", resultSet.getInt("is_deleted"));
                obj.put("isTransferred", resultSet.getInt("is_transferred"));

                arrayList.add(obj);
            }

            obj = new JSONObject();
            obj.put("store", arrayList);
            obj.put("success", true);

            out.println(obj);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception",
                    e.toString(), ctx);
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