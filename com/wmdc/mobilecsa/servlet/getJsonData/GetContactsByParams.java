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

@WebServlet("/getcontactsbyparams")
public class GetContactsByParams extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

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

        String contactIdParam = request.getParameter("contactId");
        if (contactIdParam == null) {
            Utils.logError("\"contactId\" parameter is null", ctx);
            Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (contactIdParam.isEmpty()) {
            Utils.logError("\"contactId\" parameter is empty", ctx);
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

            int contactId = Integer.parseInt(contactIdParam);
            if (getContactCount(conn, contactId) < 1) {
                Utils.logError("No contacts found with id: "+contactIdParam, ctx);
                Utils.printJsonException(responseJson, "No contacts found.", out);
                return;
            }

            prepStmt = conn.prepareStatement(
                "SELECT contact_id, industry_id, lastname, firstname, mi, date_of_birth, address, city_id, " +
                        "province_id, country_id, zip_code, csa_id, job_position, area_code, plant_associated_id, " +
                        "phone, mobile, emergency_contact, er, mf_gm, calib, company, fax_number, tel_num, email, " +
                        "website, emergency, fax_code, spare_parts, address_lat, address_long " +
                        "FROM contacts WHERE contact_id = ?");
            prepStmt.setInt(1, contactId);
            resultSet = prepStmt.executeQuery();

            int getIndustryId = 0, getCityId = 0, getProvinceId = 0, getCountryId = 0, getZipCode = 0, getCsaId = 0,
                    getPlantAssociationId = 0, getAreaCode = 0, getPhone = 0, getFaxNumber = 0, getTelNum = 0,
                    getFaxCode = 0, getContactId = 0;

            double getER = 0, getMF = 0, getCalib = 0, getSpareParts = 0, getlatitude = 0, getlongitude = 0;

            String getLastname = "", getFirstname = "", getMi = "", getDateOfBirth = "", getAddress = "",
                    getJobPosition = "", getMobile = "", getEmergencyContact = "", getCompany = "", getEmail = "",
                    getWebsite = "", getEmergency = "";

            if (resultSet.next())
            {
                getContactId = resultSet.getInt("contact_id");
                getIndustryId = resultSet.getInt("industry_id");
                getCityId = resultSet.getInt("city_id");
                getProvinceId = resultSet.getInt("province_id");
                getCountryId = resultSet.getInt("country_id");
                getZipCode = resultSet.getInt("zip_code");
                getCsaId = resultSet.getInt("csa_id");
                getPlantAssociationId = resultSet.getInt("plant_associated_id");
                getAreaCode = resultSet.getInt("area_code");
                getPhone = resultSet.getInt("phone");
                getFaxNumber = resultSet.getInt("fax_number");
                getTelNum = resultSet.getInt("tel_num");
                getFaxCode = resultSet.getInt("fax_code");

                getER = resultSet.getDouble("er");
                getMF = resultSet.getDouble("mf_gm");
                getCalib = resultSet.getDouble("calib");
                getSpareParts = resultSet.getDouble("spare_parts");
                getlatitude = resultSet.getDouble("address_lat");
                getlongitude = resultSet.getDouble("address_long");

                getLastname = resultSet.getString("lastname");
                getFirstname = resultSet.getString("firstname");
                getMi = resultSet.getString("mi");
                getDateOfBirth = resultSet.getString("date_of_birth");
                getAddress = resultSet.getString("address");
                getJobPosition = resultSet.getString("job_position");
                getMobile = resultSet.getString("mobile");
                getEmergencyContact = resultSet.getString("emergency_contact");
                getCompany = resultSet.getString("company");
                getEmail = resultSet.getString("email");
                getWebsite = resultSet.getString("website");
                getEmergency = resultSet.getString("emergency");
            }

            JSONObject obj = new JSONObject();

            obj.put("contactId", getContactId);
            obj.put("industryId", getIndustryId);
            obj.put("cityId", getCityId);
            obj.put("provinceId", getProvinceId);
            obj.put("countryId", getCountryId);
            obj.put("plantId", getPlantAssociationId);

            obj.put("industry", Utils.getIndustryName(getIndustryId, conn));
            obj.put("city", Utils.getCityNameInCRM(getCityId, connCRM));
            obj.put("province", Utils.getProvinceNameInCRM(getProvinceId, connCRM));
            obj.put("country", Utils.getCountryNameInCRM(getCountryId, connCRM));
            obj.put("zipCode", getZipCode);
            obj.put("csa", Utils.getCsaNameById(getCsaId, conn));
            obj.put("plantAssociated", Utils.getPlantAssocInCRM(
                    getPlantAssociationId, connCRM));

            obj.put("areaCode", getAreaCode);
            obj.put("faxNumber", getFaxNumber);
            obj.put("telNum", getTelNum);
            obj.put("faxCode", getFaxCode);

            obj.put("er", getER);
            obj.put("mf", getMF);
            obj.put("calib", getCalib);
            obj.put("spareParts", getSpareParts);
            obj.put("latitude", getlatitude);
            obj.put("longitude", getlongitude);

            obj.put("lastname", getLastname);
            obj.put("firstname", getFirstname);
            obj.put("mi", getMi);
            obj.put("dateOfBirth", getDateOfBirth);
            obj.put("address", getAddress);
            obj.put("jobPosition", getJobPosition);
            obj.put("phone", Utils.addPhoneDash(getPhone, 0, getAreaCode));
            obj.put("mobile", Utils.eraseLeadingZeros(""+getMobile));

            obj.put("emergencyContact", Utils.eraseLeadingZeros(""+getEmergencyContact));
            obj.put("company", getCompany);
            obj.put("email", getEmail);
            obj.put("website", getWebsite);
            obj.put("emergency", getEmergency);

            obj.put("year", Utils.getYear(getDateOfBirth));
            obj.put("month", Utils.getMonth(getDateOfBirth));
            obj.put("day", Utils.getDay(getDateOfBirth));

            obj.put("photo", "Click to display photo");
            obj.put("location", "Click to display location");
            obj.put("signature", "Click to display location");
            obj.put("success", true);

            out.println(obj);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }

    private int getContactCount(Connection conn, int contactId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT COUNT(*) AS contactCount FROM contacts " +
                        "WHERE contact_id = ?");
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
}