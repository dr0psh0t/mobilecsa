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
 * Created by wmdcprog on 8/3/2017.
 */
@WebServlet("/searchcustomerfromuser")

public class SearchCustomerFromUser extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procRequest(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procRequest(request, response);
    }

    private void procRequest(HttpServletRequest request, HttpServletResponse response)
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

            String query = request.getParameter("filter");
            if (query == null) {
                Utils.logError("\"filter\" parameter is null", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (query.isEmpty()) {
                Utils.logError("\"filter\" parameter is empty", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (query.length() > 1) {
                prepStmt = conn.prepareStatement("SELECT customer_id, company, firstname, lastname, assigned_csa, " +
                        "contact_person, contact_number, is_transferred FROM customers WHERE firstname LIKE ? OR " +
                        "lastname LIKE ? OR company LIKE ? OR contact_person LIKE ?");
                prepStmt.setString(1, query+"%");
                prepStmt.setString(2, query+"%");
                prepStmt.setString(3, query+"%");
                prepStmt.setString(4, query+"%");

                resultSet = prepStmt.executeQuery();

                int customerId;

                String photoLink;
                String signatureLink;
                String firstname;
                String lastname;
                String company;

                ArrayList<JSONObject> resultList = new ArrayList<>();
                ArrayList<JSONObject> resultList2 = new ArrayList<>();

                int searchCount = 0;
                JSONObject obj;

                while (resultSet.next()) {
                    obj = new JSONObject();
                    customerId = resultSet.getInt("customer_id");
                    firstname = resultSet.getString("firstname");
                    lastname = resultSet.getString("lastname");
                    company = resultSet.getString("company");

                    photoLink = "getcustomerphoto?customerId="+customerId;
                    signatureLink = "getcustomersignature?customerId="+customerId;

                    obj.put("customerId", customerId);
                    obj.put("addedby", Utils.getCSAName(resultSet.getInt("assigned_csa"), conn));
                    obj.put("salesman", Utils.getCsaNameById(resultSet.getInt("assigned_csa"), conn));
                    obj.put("src", photoLink);
                    obj.put("signaturesource", signatureLink);
                    obj.put("companyName", company);
                    obj.put("contactPerson", resultSet.getString("contact_person"));
                    obj.put("contactNumber", resultSet.getString("contact_number"));
                    obj.put("lastname", lastname);
                    obj.put("firstname", firstname);
                    obj.put("label", (company.isEmpty() ? lastname + ", " + firstname : company));
                    obj.put("id", customerId);
                    obj.put("customer", company.isEmpty() ? lastname+", "+firstname : company);
                    obj.put("isTransferred", resultSet.getInt("is_transferred"));
                    obj.put("isAPerson", company.equals(""));

                    resultList.add(obj);
                    ++searchCount;
                }

                responseJson.put("result", resultList);

                obj = new JSONObject();
                obj.put("searchCount", searchCount);
                obj.put("wordQuery", query);
                obj.put("withAuthority", true);
                obj.put("isCustomer", true);

                resultList2.add(obj);
                responseJson.put("info", resultList2);
                responseJson.put("success", true);
            } else {
                Utils.printJsonException(responseJson, "At least 2 characters", out);
            }

            out.println(responseJson);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }
}