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

@WebServlet("/updatecontact")

public class UpdateContact extends HttpServlet {

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
        PreparedStatement prepStmt = null;

        String lastname, firstname, mi, company, jobPosition, address, mobile, email, website, emergencyContact,
                emergency, year, month, day;

        int plant, city, province, country, fax, telephone, faxAreaCode, areaCode, zip, er, mf, calib, spareParts,
                contactId;

        double latitude, longitude;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            lastname = request.getParameter("lastname");
            firstname = request.getParameter("firstname");
            mi = request.getParameter("mi");
            company = request.getParameter("company");
            jobPosition = request.getParameter("jobPosition");
            address = request.getParameter("address");
            mobile = request.getParameter("mobile");
            email = request.getParameter("email");
            website = request.getParameter("website");
            emergencyContact = request.getParameter("emergencyContact");
            emergency = request.getParameter("emergency");
            year = request.getParameter("year");
            month = request.getParameter("month");
            day = request.getParameter("day");

            plant = Integer.parseInt(request.getParameter("plant"));
            city = Integer.parseInt(request.getParameter("city"));
            province = Integer.parseInt(request.getParameter("province"));
            country = Integer.parseInt(request.getParameter("country"));
            fax = Integer.parseInt(request.getParameter("fax"));
            telephone = Integer.parseInt(request.getParameter("telephone"));
            faxAreaCode = Integer.parseInt(request.getParameter("faxAreaCode"));
            areaCode = Integer.parseInt(request.getParameter("areaCode"));
            zip = Integer.parseInt(request.getParameter("zip"));
            er = Integer.parseInt(request.getParameter("er"));
            mf = Integer.parseInt(request.getParameter("mf"));
            calib = Integer.parseInt(request.getParameter("calib"));
            spareParts = Integer.parseInt(request.getParameter("spareParts"));
            contactId = Integer.parseInt(request.getParameter("contactId"));

            latitude = Double.parseDouble(request.getParameter("latitude"));
            longitude = Double.parseDouble(request.getParameter("longitude"));

            oldData.put("lastname", request.getParameter("oldFirstname"));
            oldData.put("firstname", request.getParameter("oldLastname"));
            oldData.put("mi", request.getParameter("oldMi"));
            oldData.put("company", request.getParameter("oldCompany"));
            oldData.put("job_position", request.getParameter("oldJobPosition"));
            oldData.put("address", request.getParameter("oldAddress"));
            oldData.put("date_of_birth", request.getParameter("oldYear")+"-"+request.getParameter("oldMonth")+"" +
                    "-"+request.getParameter("oldDay"));
            oldData.put("industry_id", request.getParameter("oldIndustry"));
            oldData.put("plant_associated_id", request.getParameter("oldPlantAssociated"));
            oldData.put("city_id", request.getParameter("oldCity"));
            oldData.put("province_id", request.getParameter("oldProvince"));
            oldData.put("country_id", request.getParameter("oldCountry"));
            oldData.put("fax_number", request.getParameter("oldFaxNumber"));
            oldData.put("tel_num", request.getParameter("oldTelephone"));
            oldData.put("mobile", request.getParameter("oldMobileNumber"));
            oldData.put("email", request.getParameter("oldEmail"));
            oldData.put("website", request.getParameter("oldWebsite"));
            oldData.put("emergency_contact", request.getParameter("oldEmergencyContact"));
            oldData.put("emergency", request.getParameter("oldEmergency"));
            oldData.put("fax_code", request.getParameter("oldFaxAreaCode"));
            oldData.put("area_code", request.getParameter("oldAreaCode"));
            oldData.put("zip_code", request.getParameter("oldZipCode"));
            oldData.put("er", request.getParameter("oldEr"));
            oldData.put("mf", request.getParameter("oldMf"));
            oldData.put("calib", request.getParameter("oldCalib"));
            oldData.put("spare_parts", request.getParameter("oldSpareParts"));
            oldData.put("address_lat", request.getParameter("oldLatitude"));
            oldData.put("address_long", request.getParameter("oldLongitude"));

            if (getContactCount(conn, contactId) < 1) {
                Utils.logError("No contact found to update using contactId: "+contactId, ctx);
                Utils.printJsonException(responseJson, "No Contact found to update", out);
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
                    Utils.printJsonException(responseJson, "website format is not valid", out);
                    return;
                }
            }

            String updateStatement = "UPDATE contacts SET lastname = ?, firstname = ?, mi = ?, company = ?, " +
                    "job_position = ?, address = ?, date_of_birth = ?, plant_associated_id = ?, city_id = ?, " +
                    "province_id = ?, country_id = ?, fax_number = ?, tel_num = ?, mobile = ?, email = ?, website = ?, " +
                    "emergency_contact = ?, emergency = ?, fax_code = ?, area_code = ?, zip_code = ?, er = ?, " +
                    "mf_gm = ?, calib = ?, spare_parts = ?, address_lat = ?, address_long = ? WHERE contact_id = ?";

            prepStmt = conn.prepareStatement(updateStatement);
            prepStmt.setString(1, lastname);
            prepStmt.setString(2, firstname);
            prepStmt.setString(3, mi);
            prepStmt.setString(4, company);
            prepStmt.setString(5, jobPosition);

            prepStmt.setString(6, address);
            prepStmt.setDate(7, Utils.getDate(year+"-"+month+"-"+day, ctx));
            prepStmt.setInt(8, plant);

            prepStmt.setInt(9, city);
            prepStmt.setInt(10, province);
            prepStmt.setInt(11, country);
            prepStmt.setInt(12, fax);
            prepStmt.setInt(13, telephone);

            prepStmt.setString(14, mobile);
            prepStmt.setString(15, email);
            prepStmt.setString(16, website);
            prepStmt.setString(17, emergencyContact);
            prepStmt.setString(18, emergency);

            prepStmt.setInt(19, faxAreaCode);
            prepStmt.setInt(20, areaCode);
            prepStmt.setInt(21, zip);
            prepStmt.setInt(22, er);
            prepStmt.setInt(23, mf);
            prepStmt.setInt(24, calib);

            prepStmt.setInt(25, spareParts);
            prepStmt.setDouble(26, latitude);
            prepStmt.setDouble(27, longitude);
            prepStmt.setInt(28, contactId);

            prepStmt.executeUpdate();

            Utils.logEdit(conn, updateStatement, oldData.toString(),
                    (String)request.getSession(false).getAttribute("username"), true);

            responseJson.put("success", true);
            responseJson.put("reason", "Successfully updated contact");
            out.println(responseJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx);
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, null, ctx);
            out.close();
        }
    }

    private int getContactCount(Connection conn, int contactId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS contactCount FROM contacts WHERE contact_id = ?" );
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.redirectToLogin(response);
    }
}