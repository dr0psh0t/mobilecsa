package wmdc.mobilecsa.servlet;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MultipartConfig(fileSizeThreshold=1024*1024*6, // 5MB
        maxFileSize=1024*1024*3,      // 3MB
        maxRequestSize=1024*1024*50)   // 50MB

@WebServlet("/addcustomerperson")

public class AddCustomerPerson extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject resJson = new JSONObject();
        PrintWriter out = response.getWriter();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(resJson, "Login to continue.", out);
            return;
        }

        int csaId, cityId, countryId, zip, telNum, countryCode, areaCode, faxNum, faxCountryCode, faxAreaCode,
                industryId, plant, er, mfspgm, spareParts, calib, province;
        double latitude, longitude;
        boolean signStatus;

        String lastname = request.getParameter("lastname");
        String firstname = request.getParameter("firstname");
        String address = request.getParameter("address");
        String birthDate = request.getParameter("birthDate");
        String signature = request.getParameter("signature");
        String email = request.getParameter("email");
        String website = request.getParameter("website");
        String emergency = request.getParameter("emergency");
        String mi = request.getParameter("mi");
        String mobile = request.getParameter("mobile");
        Part filePart = request.getPart("photo");

        Utils.checkParameterValue(request.getParameter("csaId"), request.getParameter("faxCode"),
                request.getParameter("city"), request.getParameter("country"), request.getParameter("zip"),
                request.getParameter("areaCode"), request.getParameter("industry"), request.getParameter("plant"),
                request.getParameter("er"), request.getParameter("mf"), request.getParameter("spareParts"),
                request.getParameter("calib"), request.getParameter("province"), request.getParameter("signStatus"),
                address, signature, filePart, out);

        checkParameters(request.getParameter("faxCountryCode"), request.getParameter("countryCode"), lastname,
                firstname, birthDate, mi, mobile, out);

        Connection connCRM = null;
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());
            connCRM = Utils.getConnectionFromCRM(getServletContext());

            HttpSession httpSession = request.getSession(false);
            String onlineUser = (String) httpSession.getAttribute("username");

            countryId = request.getParameter("country").equals("") ? 0 :
                    Integer.parseInt(request.getParameter("country"));

            telNum = (request.getParameter("telephone").equals("")) ? 0 :
                    Integer.parseInt(Utils.removeDash(request.getParameter("telephone")));

            faxNum = (request.getParameter("fax").equals("")) ? 0 :
                    Integer.parseInt(Utils.removeDash(request.getParameter("fax")));

            csaId = Integer.parseInt(request.getParameter("csaId"));
            cityId = Integer.parseInt(request.getParameter("city"));
            zip = Integer.parseInt(request.getParameter("zip"));
            countryCode = Integer.parseInt(request.getParameter("countryCode"));
            areaCode = Integer.parseInt(request.getParameter("areaCode"));
            faxCountryCode = Integer.parseInt(request.getParameter("faxCountryCode"));
            faxAreaCode = Integer.parseInt(request.getParameter("faxCode"));
            industryId = Integer.parseInt(request.getParameter("industry"));
            plant = Integer.parseInt(request.getParameter("plant"));
            er = Integer.parseInt(request.getParameter("er"));
            mfspgm = Integer.parseInt(request.getParameter("mf"));
            spareParts = Integer.parseInt(request.getParameter("spareParts"));
            calib = Integer.parseInt(request.getParameter("calib"));
            province = Integer.parseInt(request.getParameter("province"));
            latitude = Double.parseDouble(request.getParameter("lat"));
            longitude = Double.parseDouble(request.getParameter("lng"));
            signStatus = Boolean.parseBoolean(request.getParameter("signStatus"));

            if (!inspectEmailWebsite(email, website)) {
                Utils.printJsonException(resJson, "Invalid email/website format.", out);
                return;
            }

            if (!signStatus) {
                Utils.printJsonException(resJson, "Include signature", out);
                return;
            }

            InputStream signatureInputStream = Utils.getSignatureInputStream(signature);
            InputStream photoStream = filePart.getInputStream();

            if (filePart.getSize() > (Utils.REQUIRED_IMAGE_BYTES-100_000)) {
                photoStream = Utils.reduceImage(photoStream);
            }

            if (photoStream == null) {
                Utils.logError("Getting photo input stream from filePart returns null.");
                Utils.printJsonException(resJson, "Photo problem occured. Try again or see logs.", out);
                return;
            }

            if (signatureInputStream == null) {
                Utils.logError("Getting signature input stream from signature string returns null.");
                Utils.printJsonException(resJson, "Signature problem occured. Try again or see logs.", out);
                return;
            }

            if (getCustomerCount(conn, lastname, firstname) > 0) {
                Utils.printJsonException(resJson, "Customer already saved in MCSA.", out);
                return;
            }

            if (getCustomerCountCRM(connCRM, lastname, firstname) > 0) {
                Utils.printJsonException(resJson, "Customer already exist in CRM", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT customer_id FROM customers ORDER BY customer_id DESC LIMIT 1");
            resultSet = prepStmt.executeQuery();

            int lastCustomerId = 0;
            if (resultSet.next()) { lastCustomerId = resultSet.getInt("customer_id"); }
            ++lastCustomerId;

            String[] alpha_industry = Utils.getIndustry(industryId, connCRM);

            prepStmt = conn.prepareStatement("INSERT INTO customers(customer_id, id_alpha, lastname, firstname, mi, " +
                    "company, address, address_lat, address_long, city_id, country_id, zip, tel_num, country_code, " +
                    "area_code, fax_num, fax_country_code, fax_area_code, date_of_birth, emergency, contact_person, " +
                    "contact_number, email, website, er, mfspgm, spare_parts, calibration, assigned_csa, date_added, " +
                    "date_stamp, classification, plant, zone, profile_photo, signature, is_deleted, province_id, " +
                    "cell, id_num, is_transferred, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE(), NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            prepStmt.setInt(1, lastCustomerId);
            prepStmt.setString(2, alpha_industry[0]);
            prepStmt.setString(3, lastname);
            prepStmt.setString(4, firstname);
            prepStmt.setString(5, mi);

            prepStmt.setString(6, ""); //  <-  company
            prepStmt.setString(7, address);
            prepStmt.setDouble(8, latitude);
            prepStmt.setDouble(9, longitude);
            prepStmt.setInt(10, cityId);

            prepStmt.setInt(11, countryId);
            prepStmt.setInt(12, zip);
            prepStmt.setInt(13, telNum);
            prepStmt.setInt(14, countryCode);
            prepStmt.setInt(15, areaCode);

            prepStmt.setInt(16, faxNum);
            prepStmt.setInt(17, faxCountryCode);
            prepStmt.setInt(18, faxAreaCode);
            prepStmt.setDate(19, Utils.getDate(birthDate));
            prepStmt.setString(20, emergency);

            prepStmt.setString(21, "");    //  <-  contact_person
            prepStmt.setString(22, "");    //  <-  contact_number
            prepStmt.setString(23, email);
            prepStmt.setString(24, website);
            prepStmt.setInt(25, er);

            prepStmt.setInt(26, mfspgm);
            prepStmt.setInt(27, spareParts);
            prepStmt.setInt(28, calib);
            prepStmt.setInt(29, csaId);

            prepStmt.setString(30, alpha_industry[1]);
            prepStmt.setString(31, Utils.getPlantByIdInCRM(plant, connCRM));
            prepStmt.setInt(32, 0);
            prepStmt.setBlob(33, photoStream);

            prepStmt.setBlob(34, signatureInputStream);
            prepStmt.setInt(35, 0);
            prepStmt.setInt(36, province);
            prepStmt.setString(37, mobile);
            prepStmt.setInt(38, Utils.getNextIdNumber(alpha_industry[0], conn));

            prepStmt.setInt(39, 0);
            prepStmt.setInt(40, 0);
            prepStmt.executeUpdate();

            Utils.logAdd(conn, onlineUser, true);
            Utils.updateIndustryTable(alpha_industry[0], conn);

            Utils.printSuccessJson(resJson, "Successfully added customer.", out);

            filePart.getInputStream().close();
            signatureInputStream.close();
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
            Utils.closeDBResource(connCRM, null, null);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    private static boolean inspectEmailWebsite(String email, String website) throws IOException {
        if (!email.isEmpty()) {
            if (!Utils.validEmail(email)) {
                return false;
            }
        }

        if (!website.isEmpty()) {
            if (!Utils.validURL(website)) {
                return  false;
            }
        }

        return true;
    }

    private int getCustomerCountCRM(Connection connection, String lastname, String firstname) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT (*) AS customerCount FROM customers WHERE lname = ? AND fname = ?");

        String lastname2 = Utils.omitSpaces(lastname);
        String firstname2 = Utils.omitSpaces(firstname);

        preparedStatement.setString(1, lastname2);
        preparedStatement.setString(2, firstname2);

        ResultSet resultSet = preparedStatement.executeQuery();
        int customerCount = 0;

        if (resultSet.next()) {
            customerCount = resultSet.getInt("customerCount");
        }

        preparedStatement.close();
        resultSet.close();

        return customerCount;
    }

    private int getCustomerCount(Connection connection, String lastname, String firstname) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
            "SELECT COUNT (*) AS customerCount FROM customers WHERE lastname = ? AND firstname = ?");

        preparedStatement.setString(1, lastname);
        preparedStatement.setString(2, firstname);

        ResultSet resultSet = preparedStatement.executeQuery();
        int customerCount = 0;

        if (resultSet.next()) {
            customerCount = resultSet.getInt("customerCount");
        }

        preparedStatement.close();
        resultSet.close();

        return customerCount;
    }

    public void checkParameters(String faxCountryCode, String countryCode, String lastname, String firstname,
                                String birthDate, String mi, String mobile, PrintWriter out) {
        try {
            if (faxCountryCode == null) {
                Utils.logError("\"faxCountryCode\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Fax country code is required.", out);
                return;
            } else if (faxCountryCode.isEmpty()) {
                Utils.logError("\"faxCountryCode\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Fax country code is required.", out);
                return;
            }

            if (countryCode == null) {
                Utils.logError("\"countryCode\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Country code is required.", out);
                return;
            } else if (countryCode.isEmpty()) {
                Utils.logError("\"countryCode\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Country code is required.", out);
                return;
            }

            if (lastname == null) {
                Utils.logError("\"lastname\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Lastname is required.", out);
                return;
            } else if (lastname.isEmpty()) {
                Utils.logError("\"lastname\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Lastname is required.", out);
                return;
            }

            if (firstname == null) {
                Utils.logError("\"firstname\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Firstname is required.", out);
                return;
            } else if (firstname.isEmpty()) {
                Utils.logError("\"firstname\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Firstname is required.", out);
                return;
            }

            if (birthDate == null) {
                Utils.logError("\"birthDate\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Birthdate is required.", out);
                return;
            } else if (birthDate.isEmpty()) {
                Utils.logError("\"birthDate\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Birthdate is required.", out);
                return;
            }

            if (mi == null) {
                Utils.logError("\"mi\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "MI is required.", out);
                return;
            } else if (mi.isEmpty()) {
                Utils.logError("\"mi\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "MI is required.", out);
                return;
            }

            if (mobile == null) {
                Utils.logError("\"mobile\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Mobile is required.", out);
            } else if (mobile.isEmpty()) {
                Utils.logError("\"mobile\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Mobile is required.", out);
            }
        } catch (IOException ie) {
            System.err.println(ie.toString());
        }
    }
}