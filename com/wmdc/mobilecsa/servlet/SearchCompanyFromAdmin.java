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
@WebServlet("/searchcompanyfromadmin")

public class SearchCompanyFromAdmin extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
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
                prepStmt = conn.prepareStatement("SELECT customer_id, company, contact_person, contact_number, " +
                        "tel_num, fax_num, fax_area_code, is_deleted, country_code, area_code FROM customers WHERE " +
                        "company LIKE ? OR contact_person LIKE ?");
                prepStmt.setString(1, "%"+query+"%");
                prepStmt.setString(2, "%"+query+"%");
                resultSet = prepStmt.executeQuery();

                ArrayList<JSONObject> arrayList = new ArrayList<>();
                JSONObject obj;

                while (resultSet.next()) {
                    obj = new JSONObject();
                    obj.put("customerId", resultSet.getInt("customer_id"));
                    obj.put("company", resultSet.getString("company"));
                    obj.put("contactPerson", resultSet.getString("contact_person"));
                    obj.put("contactNumber", Utils.eraseLeadingZeros(resultSet.getString("contact_number")));
                    obj.put("telNum", Utils.addPhoneDash(resultSet.getInt("tel_num"),
                            resultSet.getInt("country_code"), resultSet.getInt("area_code")));
                    obj.put("faxNum", Utils.addPhoneDash(resultSet.getInt("fax_num"),
                            resultSet.getInt("country_code"), resultSet.getInt("area_code")));
                    obj.put("isDeleted", resultSet.getInt("is_deleted"));
                    arrayList.add(obj);
                }

                responseJson.put("store", arrayList);
                responseJson.put("success", true);
                out.println(responseJson);
            } else {
                Utils.printJsonException(responseJson, "2 characters to search.", out);
            }

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot search company at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot search company at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

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