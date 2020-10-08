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
import java.sql.*;

/**
 * Created by wmdcprog on 3/31/2017.
 */
@WebServlet("/getcustomercompanybyparams")
public class GetCustomerCompanyByParams extends HttpServlet {
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

        String customerIdParam = request.getParameter("customerId");

        if (customerIdParam == null) {
            Utils.logError("\"customerId\" parameter is null", ctx);
            Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (customerIdParam.isEmpty()) {
            Utils.logError("\"customerId\" parameter is empty", ctx);
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
                Utils.logError("No customer found using id: "+customerId, ctx);
                Utils.printJsonException(responseJson, "No Customer found.", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT customer_id, company, address, address_lat, address_long, " +
                    "city_id, country_id, zip, tel_num, country_code, area_code, fax_num, fax_country_code, " +
                    "fax_area_code, contact_person, contact_number, date_added, assigned_csa, " +
                    "classification, plant, province_id, email, website, emergency, er, mfspgm, spare_parts, " +
                    "calibration FROM customers WHERE customer_id = ?");
            prepStmt.setInt(1, customerId);
            resultSet = prepStmt.executeQuery();

            int getCityId = 0, getCountryId = 0, getZip = 0, getCountryCode = 0, getAreaCode = 0, getFaxCountryCode = 0,
                    getFaxAreaCode = 0, getAssignedCsa = 0, getTelNum = 0, getFaxNum = 0, getCustomerId = 0,
                    getIndustry = 0, getProvince = 0, getEr = 0, getMf = 0, getSpareParts = 0, getCalib = 0;

            String getCompany = "", getAddress = "", getLatitude = "", getLongitude = "", getContactPerson = "",
                    getContactNumber = "", getDateAdded = "", getPlant = "", getClassification = "", getEmail = "",
                    getWebsite = "", getEmergency = "";

            if (resultSet.next())
            {
                getCustomerId = resultSet.getInt("customer_id");
                getCompany = resultSet.getString("company");
                getAddress = resultSet.getString("address");
                getLatitude = resultSet.getString("address_lat");
                getLongitude = resultSet.getString("address_long");

                Utils.logMsg("classification= "+resultSet.getString("classification"), ctx);

                getIndustry = Utils.getIndustryIdByName(resultSet.getString("classification"), connCRM);

                getProvince = resultSet.getInt("province_id");
                getEr = resultSet.getInt("er");
                getMf = resultSet.getInt("mfspgm");
                getSpareParts = resultSet.getInt("spare_parts");
                getCalib = resultSet.getInt("calibration");

                getPlant = resultSet.getString("plant");
                getClassification = resultSet.getString("classification");
                getEmail = resultSet.getString("email");
                getWebsite = resultSet.getString("website");
                getEmergency = resultSet.getString("emergency");

                getFaxNum = resultSet.getInt("fax_num");
                getTelNum = resultSet.getInt("tel_num");
                getCityId = resultSet.getInt("city_id");
                getCountryId = resultSet.getInt("country_id");
                getZip = resultSet.getInt("zip");
                getCountryCode = resultSet.getInt("country_code");
                getAreaCode = resultSet.getInt("area_code");
                getFaxCountryCode = resultSet.getInt("fax_country_code");
                getFaxAreaCode = resultSet.getInt("fax_area_code");

                getContactPerson = resultSet.getString("contact_person");
                getContactNumber = resultSet.getString("contact_number");
                getDateAdded = resultSet.getString("date_added");
                getAssignedCsa = resultSet.getInt("assigned_csa");
            }

            JSONObject obj = new JSONObject();

            obj.put("customerId", getCustomerId);
            obj.put("industry", getIndustry);
            obj.put("province", getProvince);
            obj.put("er", getEr);
            obj.put("mf", getMf);
            obj.put("spareParts", getSpareParts);
            obj.put("calib", getCalib);
            obj.put("plant", getPlant);
            obj.put("rawPlant", Utils.getPlantIdByNameInCRM(getPlant, connCRM));
            obj.put("classification", getClassification);
            obj.put("email", getEmail);
            obj.put("website", getWebsite);
            obj.put("emergency", getEmergency);
            obj.put("rawFax", getFaxNum);
            obj.put("rawTelephone", getTelNum);
            obj.put("company", getCompany);
            obj.put("address", getAddress);
            obj.put("latitude", getLatitude);
            obj.put("longitude", getLongitude);
            obj.put("city", Utils.getCityNameInCRM(getCityId, connCRM));
            obj.put("rawCity", getCityId);
            obj.put("country", Utils.getCountryNameInCRM(getCountryId, connCRM));
            obj.put("rawCountry", getCountryId);
            obj.put("zip", getZip);
            obj.put("telnum", Utils.addPhoneDash(getTelNum, getCountryCode, getAreaCode));

            obj.put("countrycode", getCountryCode);
            obj.put("areacode", getAreaCode);
            obj.put("faxcountrycode", getFaxCountryCode);
            obj.put("faxareacode", getFaxAreaCode);

            obj.put("faxnum", Utils.addPhoneDash(getFaxNum, getCountryCode, getAreaCode));
            obj.put("contactperson", getContactPerson);
            obj.put("contactnumber", Utils.eraseLeadingZeros(""+getContactNumber));
            obj.put("dateadded", getDateAdded);
            obj.put("assignedCsa", Utils.getCsaNameById(getAssignedCsa, conn));
            obj.put("photo", "Click to display photo");
            obj.put("signature", "Click to display signature");
            obj.put("location", "Click to display location");
            obj.put("isCompany", true);
            obj.put("success", true);

            out.println(obj);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot get customer at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot get customer at the moment.", out);
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

    public int getCustomerCount(Connection conn, int customerId) throws SQLException {
        PreparedStatement prepStmt =
                conn.prepareStatement("SELECT COUNT(*) AS customerCount FROM customers WHERE customer_id = ?");
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