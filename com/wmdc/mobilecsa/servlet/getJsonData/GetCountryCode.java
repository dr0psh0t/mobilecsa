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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by wmdcprog on 8/31/2017.
 */
@WebServlet("/getcountrycode")

public class GetCountryCode extends HttpServlet {
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

            prepStmt = conn.prepareStatement("SELECT country_code_id, country, country_code FROM country_code");
            resultSet = prepStmt.executeQuery();

            ArrayList<JSONObject> arrayList = new ArrayList<>();
            JSONObject obj;

            while (resultSet.next()) {
                obj = new JSONObject();
                obj.put("countryCodeId", resultSet.getInt("country_code_id"));
                obj.put("countryCode", resultSet.getInt("country_code"));
                obj.put("country", resultSet.getInt("country_code")+" - "+resultSet.getString("country"));
                arrayList.add(obj);
            }

            obj = new JSONObject();
            obj.put("countryCodeStore", arrayList);
            obj.put("success", true);

            out.println(obj);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot get country code at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot get country code at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString(),
                    ctx, conn);

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