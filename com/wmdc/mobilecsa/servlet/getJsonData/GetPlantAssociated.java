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

@WebServlet("/getplantassociated")
public class GetPlantAssociated extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        if (!Utils.isOnline(request, getServletContext())) {
            Utils.printJsonException(responseJson, "Login first.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnectionFromCRM(getServletContext());

            prepStmt = conn.prepareStatement("SELECT plant_id, plant_associated FROM plant");
            resultSet = prepStmt.executeQuery();

            JSONObject obj;
            ArrayList<JSONObject> arrayList = new ArrayList<>();

            while (resultSet.next()) {
                obj = new JSONObject();
                obj.put("plant_id", resultSet.getInt("plant_id"));
                obj.put("plant_name", resultSet.getString("plant_associated"));
                arrayList.add(obj);
            }

            responseJson.put("plantStore", arrayList);
            responseJson.put("success", true);
            out.println(responseJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot get plants at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), getServletContext(), conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot get plants at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception",
                    e.toString(), getServletContext(), conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, getServletContext());
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        Utils.illegalRequest(response);
    }
}