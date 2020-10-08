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
 * Created by wmdcprog on 1/6/2018.
 */
@WebServlet("/getlocationandroid")
public class GetLocationAndroid extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        ServletContext ctx = getServletContext();

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            String idStr = request.getParameter("id");
            String isCustomerStr = request.getParameter("isCustomer");

            if (idStr == null) {
                Utils.logError("\"id\" parameter is null", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (idStr.isEmpty()) {
                Utils.logError("\"id\" parameter is empty", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (isCustomerStr == null) {
                Utils.logError("\"isCustomer\" parameter is null", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (isCustomerStr.isEmpty()) {
                Utils.logError("\"isCustomer\" parameter is empty", ctx);
                Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (Boolean.parseBoolean(isCustomerStr)) {
                prepStmt = conn.prepareStatement("SELECT address_lat, address_long, firstname, lastname, company " +
                                "FROM customers WHERE customer_id = ?");
                prepStmt.setInt(1, Integer.parseInt(idStr));
                resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    responseJson.put("lat", resultSet.getDouble("address_lat"));
                    responseJson.put("lng", resultSet.getDouble("address_long"));
                    responseJson.put("object", resultSet.getString("company").isEmpty() ?
                            resultSet.getString("firstname") +" "+resultSet.getString("lastname") :
                            resultSet.getString("company"));
                }
            } else {
                prepStmt = conn.prepareStatement("SELECT address_lat, address_long, firstname, lastname FROM " +
                                "contacts WHERE contact_id = ?");
                prepStmt.setInt(1, Integer.parseInt(idStr));
                resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    responseJson.put("lat", resultSet.getDouble("address_lat"));
                    responseJson.put("lng", resultSet.getDouble("address_long"));
                    responseJson.put("object", resultSet.getString("firstname")+" "+resultSet.getString("lastname"));
                }
            }

            responseJson.put("success", true);
            out.println(responseJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot get location at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot get location at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString(),
                    ctx, conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, getServletContext());
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}