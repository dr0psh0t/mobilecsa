package wmdc.mobilecsa.servlet;

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
 * Created by wmdcprog on 7/15/2017.
 */
@WebServlet("/searchcustomerfromadmin")

public class SearchCustomerFromAdmin extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();
        ServletContext ctx = getServletContext();

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(responseJson, "Login first.", out);
            return;
        }

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            String query = request.getParameter("query");
            if (query == null) {
                Utils.logError("\"query\" parameter is null", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (query.isEmpty()) {
                Utils.logError("\"query\" parameter is empty", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (query.length() > 2) {
                ArrayList<JSONObject> arrayList = new ArrayList<>();
                JSONObject obj;

                prepStmt = conn.prepareStatement("SELECT customer_id, lastname, firstname, tel_num, fax_num, " +
                        "date_of_birth, date_added, assigned_csa, is_deleted, country_code, area_code, is_transferred " +
                        "FROM customers WHERE lastname LIKE ? OR firstname LIKE ?");
                prepStmt.setString(1, "%"+query+"%");
                prepStmt.setString(2, "%"+query+"%");
                resultSet = prepStmt.executeQuery();

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

                responseJson.put("store", arrayList);
                responseJson.put("success", true);
                out.println(responseJson);
            } else {
                Utils.printJsonException(responseJson, "At least 2 characters to search.", out);
            }
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}