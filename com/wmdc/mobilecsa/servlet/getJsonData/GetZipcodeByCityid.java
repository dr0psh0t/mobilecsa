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

/**
 * Created by wmdcprog on 7/19/2017.
 */
@WebServlet("/getzipcode")
public class GetZipcodeByCityid extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(responseJson, "Login", out);
            return;
        }

        Connection conn = null;

        int cityId = 0;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnectionFromCRM(getServletContext());

            if (request.getParameter("city") == null) {
                Utils.logError("\"city\" parameter is null", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (request.getParameter("city").isEmpty()) {
                Utils.logError("\"city\" parameter is empty", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            cityId = Integer.parseInt(request.getParameter("city"));

            if (getCityCount(cityId, conn) < 1) {
                Utils.logError("No city found with cityId: "+cityId, ctx);
                return;
            }

            responseJson.put("success", true);
            responseJson.put("reason", "okay");
            responseJson.put("zipCode", getZipCode(cityId, conn));
            responseJson.put("cityId", cityId);

            out.println(responseJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(),
                    Utils.GET_JSON_DATA_PACKAGE, "db_exception", sqe.toString(), ctx);
            responseJson.put("success", false);
            responseJson.put("reason", "An error occured in zip code");
            responseJson.put("zipCode", 0);
            responseJson.put("cityId", cityId);
            out.println(responseJson);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(),
                    Utils.GET_JSON_DATA_PACKAGE, "exception", e.toString(), ctx);
            responseJson.put("success", false);
            responseJson.put("reason", e.getMessage());
            responseJson.put("zipCode", 0);
            responseJson.put("cityId", cityId);
            out.println(responseJson);
        } finally {
            Utils.closeDBResource(conn, null, null, getServletContext());
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    private int getZipCode(int cityId,Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT zip_code FROM city WHERE city_id = ?");
        prepStmt.setInt(1, cityId);

        ResultSet resultSet = prepStmt.executeQuery();
        int zipCode = 0;
        if (resultSet.next()) {
            zipCode = resultSet.getInt("zip_code");
        }

        prepStmt.close();
        resultSet.close();

        return zipCode;
    }

    private int getCityCount(int cityId, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT COUNT(*) AS cityCount FROM city WHERE city_id = ?");
        prepStmt.setInt(1, cityId);

        ResultSet resultSet = prepStmt.executeQuery();
        int cityCount = 0;
        if (resultSet.next()) {
            cityCount = resultSet.getInt("cityCount");
        }

        prepStmt.close();
        resultSet.close();

        return cityCount;
    }
}
