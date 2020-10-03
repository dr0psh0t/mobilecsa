package wmdc.mobilecsa.servlet.getJsonData;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;

@WebServlet("/getindustries")
public class GetIndustry extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        if (!Utils.isOnline(request, getServletContext())) {
            Utils.printJsonException(responseJson, "Login first.", out);
            return;
        }

        ArrayList<JSONObject> arrayList = new ArrayList<>();
        JSONObject obj;

        Connection connCrm = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            connCrm = Utils.getConnectionFromCRM(getServletContext());

            prepStmt = connCrm.prepareStatement("SELECT industry_id, id1, id2, industry FROM industry");
            resultSet = prepStmt.executeQuery();

            while (resultSet.next()) {
                obj = new JSONObject();
                obj.put("industryname", resultSet.getString("industry"));
                obj.put("industryid", resultSet.getString("industry_id"));
                arrayList.add(obj);
            }

            responseJson.put("industryStore", arrayList);
            responseJson.put("success", true);

            out.println(responseJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), getServletContext(), connCrm);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString(),
                    getServletContext(), connCrm);

        } finally {
            Utils.closeDBResource(connCrm, prepStmt, resultSet, getServletContext());
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}