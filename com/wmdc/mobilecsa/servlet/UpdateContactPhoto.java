package wmdc.mobilecsa.servlet;

import org.json.simple.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wmdcprog on 7/5/2019.
 */
@MultipartConfig(fileSizeThreshold=1024*1024*6, // 5MB
        maxFileSize=1024*1024*3,      // 3MB
        maxRequestSize=1024*1024*50)   // 50MB
@WebServlet("/updatecontactphoto")
public class UpdateContactPhoto extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject resJson = new JSONObject();
        PrintWriter out = response.getWriter();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(resJson, "Login to continue.", out);
            return;
        }

        String contactIdParam = request.getParameter("contactId");
        Part contactPhoto = request.getPart("contactPhoto");

        if (contactIdParam == null) {
            Utils.logError("\"contactId\" parameter is null");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (contactIdParam.isEmpty()) {
            Utils.logError("\"contactId\" parameter is empty");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        if (contactPhoto == null) {
            Utils.printJsonException(resJson, "Contact photo parameter is null", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int contactId = Integer.parseInt(contactIdParam);
            if (getContactCount(conn, contactId) < 1)  {
                Utils.logError("No contact found to update using contactId: "+contactId);
                Utils.printJsonException(resJson, "No contact found to update.", out);
                return;
            }

            prepStmt = conn.prepareStatement("UPDATE contacts SET specimen = ? WHERE contact_id = ?");
            prepStmt.setBlob(1, contactPhoto.getInputStream());
            prepStmt.setInt(2, contactId);
            int updateCount = prepStmt.executeUpdate();

            System.out.println("No# of contacts updated photo: "+updateCount);

            if (updateCount < 1) {
                Utils.printJsonException(resJson, "No contact photo updated.", out);
            } else {
                Utils.printSuccessJson(resJson, "Successfully updated contact photo.", out);
            }
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(resJson, sqe.toString(), out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(resJson, e.toString(), out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
            out.close();
        }
    }

    private int getContactCount(Connection conn, int contactId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT (*) AS contactCount FROM contacts WHERE contact_id = ?" );
        prepStmt.setInt(1, contactId);
        ResultSet resultSet = prepStmt.executeQuery();

        int contactCount = 0;
        if (resultSet.next()) {
            contactCount = resultSet.getInt("contactCount");
        }

        prepStmt.close();
        resultSet.close();

        return contactCount;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}