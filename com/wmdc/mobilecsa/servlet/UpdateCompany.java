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

@WebServlet("/updatecompany")

public class UpdateCompany extends HttpServlet {

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

        JSONObject oldData = new JSONObject();

        Connection conn = null;
        Connection connCRM = null;
        PreparedStatement prepStmt = null;

        String company, address, email, website, contactPerson, contactNumber, emergency;

        int plant, city, province, country, fax, telephone, faxAreaCode, faxCountryCode, countryCode, areaCode, zip, er,
                mf, calib, spareParts, customerId;

        double latitude, longitude;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());
            connCRM = Utils.getConnectionFromCRM(getServletContext());

            oldData.put("company", request.getParameter("oldCompany"));
            oldData.put("address", request.getParameter("oldAddress"));
            oldData.put("industry_id", request.getParameter("oldIndustry"));
            oldData.put("plant_associated_id", request.getParameter("oldPlant"));
            oldData.put("city_id", request.getParameter("oldCity"));
            oldData.put("province_id", request.getParameter("oldProvince"));
            oldData.put("country_id", request.getParameter("oldCountry"));
            oldData.put("fax_number", request.getParameter("oldFaxNumber"));
            oldData.put("tel_num", request.getParameter("oldTelephone"));
            oldData.put("email", request.getParameter("oldEmail"));
            oldData.put("website", request.getParameter("oldWebsite"));
            oldData.put("contact_person", request.getParameter("oldContactPerson"));
            oldData.put("contact_number", request.getParameter("oldContactNumber"));
            oldData.put("emergency", request.getParameter("oldEmergency"));
            oldData.put("fax_code", request.getParameter("oldFaxAreaCode"));
            oldData.put("fax_country_code", request.getParameter("oldFaxCountryCode"));
            oldData.put("country_code", request.getParameter("oldCountryCode"));
            oldData.put("area_code", request.getParameter("oldAreaCode"));
            oldData.put("zip_code", request.getParameter("oldZipCode"));
            oldData.put("er", request.getParameter("oldEr"));
            oldData.put("mf", request.getParameter("oldMf"));
            oldData.put("calib", request.getParameter("oldCalib"));
            oldData.put("spare_parts", request.getParameter("oldSpareParts"));
            oldData.put("address_lat", request.getParameter("oldLatitude"));
            oldData.put("address_long", request.getParameter("oldLongitude"));

            company = request.getParameter("company");
            address = request.getParameter("address");
            email = request.getParameter("email");
            website = request.getParameter("website");
            contactPerson = request.getParameter("contactPerson");
            contactNumber = request.getParameter("contactNumber");
            emergency = request.getParameter("emergency");

            plant = Integer.parseInt(request.getParameter("plant"));
            city = Integer.parseInt(request.getParameter("cityId"));
            province = Integer.parseInt(request.getParameter("province"));
            country = Integer.parseInt(request.getParameter("countryId"));
            fax = request.getParameter("fax").isEmpty() ? 0 : Integer.parseInt(request.getParameter("fax"));
            telephone = request.getParameter("telNum").isEmpty() ? 0 : Integer.parseInt(request.getParameter("telNum"));
            faxAreaCode = Integer.parseInt(request.getParameter("faxAreaCode"));
            faxCountryCode = Integer.parseInt(request.getParameter("faxCountryCode"));
            countryCode = Integer.parseInt(request.getParameter("countryCode"));
            areaCode = Integer.parseInt(request.getParameter("areaCode"));
            zip = Integer.parseInt(request.getParameter("zip"));
            er = Integer.parseInt(request.getParameter("er"));
            mf = Integer.parseInt(request.getParameter("mfspgm"));
            spareParts = Integer.parseInt(request.getParameter("spareParts"));
            calib = Integer.parseInt(request.getParameter("calib"));
            customerId = Integer.parseInt(request.getParameter("customerId"));

            latitude = Double.parseDouble(request.getParameter("latitude"));
            longitude = Double.parseDouble(request.getParameter("longitude"));

            if (getCustomerCount(conn, customerId) < 1) {
                Utils.logError("No customer found to update using customerId: "+customerId, ctx);
                Utils.printJsonException(responseJson, "No customer found to update", out);
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

            String updateStatement = "UPDATE customers SET company = ?, address = ?, email = ?, website = ?, " +
                    "contact_person = ?, contact_number = ?, emergency = ?, plant = ?, city_id = ?, province_id = ?, " +
                    "country_id = ?, fax_num = ?, tel_num = ?, fax_area_code = ?, fax_country_code = ?, " +
                    "country_code = ?, area_code = ?, zip = ?, er = ?, mfspgm = ?, calibration = ?, spare_parts = ?, " +
                    "address_lat = ?, address_long = ? WHERE customer_id = ?";

            prepStmt = conn.prepareStatement(updateStatement);
            prepStmt.setString(1, company);
            prepStmt.setString(2, address);
            prepStmt.setString(3, email);
            prepStmt.setString(4, website);
            prepStmt.setString(5, contactPerson);
            prepStmt.setString(6, contactNumber);

            prepStmt.setString(7, emergency);
            prepStmt.setString(8, Utils.getPlantAssocInCRM(plant, connCRM));
            prepStmt.setInt(9, city);
            prepStmt.setInt(10, province);

            prepStmt.setInt(11, country);
            prepStmt.setInt(12, fax);
            prepStmt.setInt(13, telephone);
            prepStmt.setInt(14, faxAreaCode);
            prepStmt.setInt(15, faxCountryCode);

            prepStmt.setInt(16, countryCode);
            prepStmt.setInt(17, areaCode);
            prepStmt.setInt(18, zip);
            prepStmt.setInt(19, er);
            prepStmt.setInt(20, mf);

            prepStmt.setInt(21, calib);
            prepStmt.setInt(22, spareParts);
            prepStmt.setDouble(23, latitude);
            prepStmt.setDouble(24, longitude);;

            prepStmt.setInt(25, customerId);
            prepStmt.executeUpdate();

            Utils.logEdit(conn, updateStatement, oldData.toString(),
                    request.getSession(false).getAttribute("username").toString(), true);

            responseJson.put("success", true);
            responseJson.put("reason", "Successfully updated customer.");

            out.println(responseJson);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(responseJson, "Cannot update company at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(responseJson, "Cannot update company at the moment.", out);
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
        Utils.illegalRequest(response);
    }
}