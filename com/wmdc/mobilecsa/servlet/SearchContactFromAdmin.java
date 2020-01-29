package wmdc.mobilecsa.servlet;

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
 * Created by wmdcprog on 7/15/2017.
 */
@WebServlet("/searchcontactfromadmin")

public class SearchContactFromAdmin extends HttpServlet {

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        if (!Utils.isOnline(request)) {
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
                Utils.logError("\"query\" parameter is null");
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (query.isEmpty()) {
                Utils.logError("\"query\" parameter is empty");
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (query.length() > 2) {
                prepStmt = conn.prepareStatement("SELECT contact_id, lastname, firstname, job_position, er, mf_gm, " +
                        "calib, is_deleted FROM contacts WHERE lastname LIKE ? OR firstname LIKE ? OR job_position " +
                        "LIKE ? OR company LIKE ?");

                prepStmt.setString(1, "%"+query+"%");
                prepStmt.setString(2, "%"+query+"%");
                prepStmt.setString(3, "%"+query+"%");
                prepStmt.setString(4, "%"+query+"%");
                resultSet = prepStmt.executeQuery();

                ArrayList<JSONObject> arrayList = new ArrayList<>();
                JSONObject obj;

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
                    arrayList.add(obj);
                }

                responseJson.put("store", arrayList);
                responseJson.put("success", true);
                out.println(responseJson);
            } else {
                Utils.printJsonException(responseJson, "At least 2 characters to search.", out);
            }
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
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