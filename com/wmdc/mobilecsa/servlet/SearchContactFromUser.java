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

/*** Created by wmdcprog on 8/3/2017.*/
@WebServlet("/searchcontactfromuser")

public class SearchContactFromUser extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

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

            if (query.length() > 1) {
                prepStmt = conn.prepareStatement("SELECT lastname, firstname, contact_id, csa_id, is_transferred " +
                        "FROM contacts WHERE lastname LIKE ? OR firstname LIKE ? OR company LIKE ?");
                prepStmt.setString(1, query+"%");
                prepStmt.setString(2, query+"%");
                prepStmt.setString(3, query+"%");
                resultSet = prepStmt.executeQuery();

                int contactId;
                String photoLink;
                String signatureLink;
                String lastname;
                String firstname;

                ArrayList<JSONObject> resultList = new ArrayList<>();
                ArrayList<JSONObject> resultList2 = new ArrayList<>();

                int searchCount = 0;
                JSONObject obj;

                while (resultSet.next()) {
                    obj = new JSONObject();

                    contactId = resultSet.getInt("contact_id");
                    photoLink = "getcontactsphoto?contactId="+contactId;
                    signatureLink = "getcontactsignature?contactId="+contactId;

                    lastname = resultSet.getString("lastname");
                    firstname = resultSet.getString("firstname");

                    obj.put("contactId", contactId);
                    obj.put("firstname", firstname);
                    obj.put("lastname", lastname);
                    obj.put("signaturesource", signatureLink);
                    obj.put("src", photoLink);
                    obj.put("isAPerson", true);
                    obj.put("label", lastname + ", " + firstname);
                    obj.put("addedby", Utils.getCSAName(resultSet.getInt("csa_id"), conn));
                    obj.put("salesman", Utils.getCsaNameById(resultSet.getInt("csa_id"), conn));
                    obj.put("id", contactId);
                    obj.put("isTransferred", resultSet.getInt("is_transferred"));

                    resultList.add(obj);
                    ++searchCount;
                }

                responseJson.put("result", resultList);

                obj = new JSONObject();
                obj.put("withAuthority", true);
                obj.put("searchCount", searchCount);
                obj.put("wordQuery", query);
                obj.put("isCustomer", false);

                resultList2.add(obj);
                responseJson.put("info", resultList2);
                responseJson.put("success", true);

                out.println(responseJson);
            } else {
                Utils.printJsonException(responseJson, "At least 2 characters to search", out);
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
}