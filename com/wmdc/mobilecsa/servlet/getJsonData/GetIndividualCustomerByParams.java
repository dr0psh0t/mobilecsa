package wmdc.mobilecsa.servlet.getJsonData;

import org.json.simple.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/getindividual")
public class GetIndividualCustomerByParams extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject responseJson = new JSONObject();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(responseJson, "Login.", out);
            return;
        }

        String customerIdParam = request.getParameter("customerId");
        if (customerIdParam == null) {
            Utils.logError("\"customerId\" parameter is null");
            Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
            return;
        }

        if (customerIdParam.isEmpty()) {
            Utils.logError("\"customerId\" parameter is empty");
            Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
            return;
        }

        Connection connCRM;
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());
            connCRM = Utils.getConnectionFromCRM(getServletContext());

            int customerId = Integer.parseInt(customerIdParam);
            if (getCustomerCount(conn, customerId) < 1) {
                Utils.logError("No customer found with id: "+customerId);
                Utils.printJsonException(responseJson, "No customer found", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT customer_id, lastname, firstname, address, address_lat, " +
                    "address_long, city_id, country_id, zip, tel_num, country_code, area_code, fax_num, " +
                    "fax_country_code, fax_area_code, date_of_birth, date_added, assigned_csa, mi, id_alpha, plant, " +
                    "province_id, cell, email, website, emergency, er, mfspgm, spare_parts, calibration " +
                    "FROM customers WHERE customer_id = ?");

            prepStmt.setInt(1, customerId);
            resultSet = prepStmt.executeQuery();

            int getCityId = 0, getCountryId = 0, getZip = 0, getCountryCode = 0, getAreaCode = 0, getFaxCountryCode = 0,
                    getFaxAreaCode = 0, getAssignedCsa = 0, getTelNum = 0, getFaxNum = 0, getCustomerId = 0,
                    getIndustry = 0, getPlant = 0, getProvince = 0, getEr = 0, getMf = 0, getSpareParts = 0,
                    getCalib = 0;

            String getLastname = "", getFirstname = "", getAddress = "", getLatitude = "", getLongitude = "",
                    getDateOfBirth = "", getDateAdded = "", getMi = "", getCellphone = "", getEmail = "",
                    getWebsite = "", getEmergency = "";

            if (resultSet.next())
            {
                getCustomerId = resultSet.getInt("customer_id");
                getFirstname = resultSet.getString("firstname");
                getLastname = resultSet.getString("lastname");
                getAddress = resultSet.getString("address");
                getLatitude = resultSet.getString("address_lat");
                getLongitude = resultSet.getString("address_long");
                getIndustry = Utils.getIndustryIdByAlpha(resultSet.getString("id_alpha"), conn);
                getPlant = Utils.getPlantIdByNameInCRM(resultSet.getString("plant"), connCRM);

                getProvince = resultSet.getInt("province_id");
                getEr = resultSet.getInt("er");
                getMf = resultSet.getInt("mfspgm");
                getSpareParts = resultSet.getInt("spare_parts");
                getCalib = resultSet.getInt("calibration");

                getMi = resultSet.getString("mi");
                getCellphone = resultSet.getString("cell");
                getEmail = resultSet.getString("email");
                getWebsite = resultSet.getString("website");
                getEmergency = resultSet.getString("emergency");

                getTelNum = resultSet.getInt("tel_num");
                getFaxNum = resultSet.getInt("fax_num");
                getCityId = resultSet.getInt("city_id");
                getCountryId = resultSet.getInt("country_id");
                getZip = resultSet.getInt("zip");
                getCountryCode = resultSet.getInt("country_code");
                getAreaCode = resultSet.getInt("area_code");
                getFaxCountryCode = resultSet.getInt("fax_country_code");
                getFaxAreaCode = resultSet.getInt("fax_area_code");

                getDateOfBirth = resultSet.getString("date_of_birth");
                getDateAdded = resultSet.getString("date_added");
                getAssignedCsa = resultSet.getInt("assigned_csa");
            }

            int countryCode = Utils.getCountryCodeById(getCountryCode, conn);

            JSONObject obj = new JSONObject();

            obj.put("customerId", getCustomerId);
            obj.put("year", Utils.getYear(getDateOfBirth));
            obj.put("month", Utils.getMonth(getDateOfBirth));
            obj.put("day", Utils.getDay(getDateOfBirth));

            obj.put("industry", getIndustry);
            obj.put("plant", getPlant);
            obj.put("province", getProvince);
            obj.put("er", getEr);
            obj.put("mf", getMf);
            obj.put("spareParts", getSpareParts);
            obj.put("calib", getCalib);

            obj.put("mi", getMi);
            obj.put("cellphone", getCellphone);
            obj.put("email", getEmail);
            obj.put("website", getWebsite);
            obj.put("emergency", getEmergency);

            obj.put("firstname", getFirstname);
            obj.put("lastname", getLastname);
            obj.put("address", getAddress);
            obj.put("latitude", getLatitude);
            obj.put("longitude", getLongitude);
            obj.put("city", Utils.getCityNameInCRM(getCityId, connCRM));
            obj.put("country", Utils.getCountryNameInCRM(getCountryId, connCRM));
            obj.put("cityId", getCityId);
            obj.put("countryId", getCountryId);
            obj.put("zip", getZip);
            obj.put("telnum", Utils.addPhoneDash(getTelNum, countryCode, getAreaCode));
            obj.put("telephone", getTelNum);
            obj.put("faxnum", Utils.addPhoneDash(getFaxNum, countryCode, getAreaCode));
            obj.put("fax", getFaxNum);

            obj.put("faxcountrycode", getFaxCountryCode);
            obj.put("countrycode", getCountryCode);
            obj.put("areacode", getAreaCode);
            obj.put("faxareacode", getFaxAreaCode);

            obj.put("dateofbirth", getDateOfBirth);
            obj.put("dateadded", getDateAdded);
            obj.put("assignedCsa", Utils.getCsaNameById(getAssignedCsa, conn));
            obj.put("photo", "Click to display photo");
            obj.put("signature", "Click to display signature");
            obj.put("location", "Click to display location");
            obj.put("isCompany", false);
            obj.put("success", true);

            out.println(obj);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    public int getCustomerCount(Connection conn, int customerId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT COUNT (*) AS customerCount FROM customers " +
                "WHERE customer_id = ?");
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
}