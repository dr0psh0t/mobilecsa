package wmdc.mobilecsa.servlet;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/updatecustomer")
public class UpdateCustomer extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(responseJson, "Session not allowed", out);
            return;
        }

        JSONObject oldData = new JSONObject();

        Connection conn = null;
        Connection connCRM = null;
        PreparedStatement prepStmt = null;

        String lastname, firstname, mi, address, year, month, day, cell, email, website, emergency;

        int plant, city, province, country, fax, telephone, faxAreaCode, faxCountryCode, areaCode, countryCode, zip,
                er, mf, spareParts, calib, customerId;

        double latitude, longitude;

        oldData.put("lastname", request.getParameter("oldLastname"));
        oldData.put("firstname", request.getParameter("oldFirstname"));
        oldData.put("mi", request.getParameter("oldMi"));
        oldData.put("address", request.getParameter("oldAddress"));
        oldData.put("date_of_birth", request.getParameter("oldYear")+"-"+request.getParameter("oldMonth")+"" +
                "-"+request.getParameter("oldDay"));
        oldData.put("plant_associated_id", request.getParameter("oldPlant"));
        oldData.put("city_id", request.getParameter("oldCity"));
        oldData.put("province_id", request.getParameter("oldProvince"));
        oldData.put("country_id", request.getParameter("oldCountry"));
        oldData.put("fax_number", request.getParameter("oldFaxNumber"));
        oldData.put("tel_num", request.getParameter("oldTelephone"));
        oldData.put("cell", request.getParameter("oldCellphone"));
        oldData.put("email", request.getParameter("oldEmail"));
        oldData.put("website", request.getParameter("oldWebsite"));
        oldData.put("emergency", request.getParameter("oldEmergency"));
        oldData.put("fax_code", request.getParameter("oldFaxAreaCode"));
        oldData.put("fax_country_code", request.getParameter("oldFaxCountryCode"));
        oldData.put("area_code", request.getParameter("oldAreaCode"));
        oldData.put("country_code", request.getParameter("oldCountryCode"));
        oldData.put("zip_code", request.getParameter("oldZipCode"));
        oldData.put("er", request.getParameter("oldEr"));
        oldData.put("mf", request.getParameter("oldMf"));
        oldData.put("calib", request.getParameter("oldCalib"));
        oldData.put("spare_parts", request.getParameter("oldSpareParts"));
        oldData.put("address_lat", request.getParameter("oldLatitude"));
        oldData.put("address_long", request.getParameter("oldLongitude"));

        try {
            lastname = request.getParameter("lastname");
            firstname = request.getParameter("firstname");
            mi = request.getParameter("mi");
            address = request.getParameter("address");
            year = request.getParameter("year");
            month = request.getParameter("month");
            day = request.getParameter("day");
            cell = request.getParameter("cell");
            email = request.getParameter("email");
            website = request.getParameter("website");
            emergency = request.getParameter("emergency");

            plant = Integer.parseInt(request.getParameter("plant"));
            city = Integer.parseInt(request.getParameter("cityId"));
            province = Integer.parseInt(request.getParameter("province"));
            country = Integer.parseInt(request.getParameter("countryId"));

            fax = request.getParameter("fax").isEmpty() ? 0 : Integer.parseInt(request.getParameter("fax"));
            telephone = request.getParameter("telNum").isEmpty() ? 0 : Integer.parseInt(request.getParameter("telNum"));
            faxAreaCode = Integer.parseInt(request.getParameter("faxAreaCode"));
            faxCountryCode = Integer.parseInt(request.getParameter("faxCountryCode"));
            areaCode = Integer.parseInt(request.getParameter("areaCode"));
            countryCode = Integer.parseInt(request.getParameter("countryCode"));
            zip = Integer.parseInt(request.getParameter("zip"));
            er = Integer.parseInt(request.getParameter("er"));
            mf = Integer.parseInt(request.getParameter("mfspgm"));
            spareParts = Integer.parseInt(request.getParameter("spareParts"));
            calib = Integer.parseInt(request.getParameter("calib"));
            customerId = Integer.parseInt(request.getParameter("customerId"));

            latitude = Double.parseDouble(request.getParameter("latitude"));
            longitude = Double.parseDouble(request.getParameter("longitude"));

            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());
            connCRM = Utils.getConnectionFromCRM(getServletContext());

            if (getCustomerCount(conn, customerId) < 1) {
                Utils.logError("No customer found with customer id: "+customerId, ctx);
                Utils.printJsonException(responseJson, "No customer found with the id", out);
                return;
            }

            if (!email.isEmpty()) {
                if (!Utils.validEmail(email)) {
                    Utils.printJsonException(responseJson, "Email format is not valid", out);
                    return;
                }
            }

            if (!website.isEmpty()) {
                if (!Utils.validURL(website)) {
                    Utils.printJsonException(responseJson, "Website format is not valid", out);
                    return;
                }
            }

            String updateStatement = "UPDATE customers SET lastname = ?, firstname = ?, mi = ?, address = ?, " +
                    "date_of_birth = ?, cell = ?, email = ?, website = ?, emergency = ?, plant = ?, city_id = ?, " +
                    "province_id = ?, country_id = ?, fax_num = ?, tel_num = ?, fax_area_code = ?, " +
                    "fax_country_code = ?, area_code = ?, country_code = ?, zip = ?, er = ?, mfspgm = ?, " +
                    "spare_parts = ?, calibration = ?, address_lat = ?, address_long = ? WHERE customer_id = ?";

            prepStmt = conn.prepareStatement(updateStatement);
            prepStmt.setString(1, lastname);
            prepStmt.setString(2, firstname);
            prepStmt.setString(3, mi);
            prepStmt.setString(4, address);
            prepStmt.setDate(5, Utils.getDate(year+"-"+month+"-"+day, ctx, conn));

            prepStmt.setString(6, cell);
            prepStmt.setString(7, email);
            prepStmt.setString(8, website);
            prepStmt.setString(9, emergency);

            prepStmt.setString(10, Utils.getPlantByIdInCRM(plant, connCRM));
            prepStmt.setInt(11, city);
            prepStmt.setInt(12, province);
            prepStmt.setInt(13, country);

            prepStmt.setInt(14, fax);
            prepStmt.setInt(15, telephone);
            prepStmt.setInt(16, faxAreaCode);
            prepStmt.setInt(17, faxCountryCode);
            prepStmt.setInt(18, areaCode);

            prepStmt.setInt(19, countryCode);
            prepStmt.setInt(20, zip);
            prepStmt.setInt(21, er);
            prepStmt.setInt(22, mf);
            prepStmt.setInt(23, spareParts);

            prepStmt.setInt(24, calib);
            prepStmt.setDouble(25, latitude);
            prepStmt.setDouble(26, longitude);
            prepStmt.setInt(27, customerId);
            prepStmt.executeUpdate();

            Utils.logEdit(conn, updateStatement, oldData.toString(),
                    (String)request.getSession(false).getAttribute("username"), true);

            responseJson.put("success", true);
            responseJson.put("reason", "Successfully updated customer");
            out.println(responseJson);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(responseJson, "DB exception raised", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(responseJson, "Exception raised", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, null, ctx);
            Utils.closeDBResource(connCRM, null, null, ctx);
            out.close();
        }
    }

    private int getCustomerCount(Connection conn, int customerId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS customerCount FROM customers WHERE customer_id = ?" );
        prepStmt.setInt(1, customerId);
        ResultSet resultSet = prepStmt.executeQuery();

        int customerCount = 0;
        if (resultSet.next()) {
            customerCount = resultSet.getInt("customerCount");
        }

        prepStmt.close();
        resultSet.close();

        return customerCount;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.redirectToLogin(response);
    }
}