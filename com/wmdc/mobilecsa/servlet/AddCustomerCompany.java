package wmdc.mobilecsa.servlet;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
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

@WebServlet("/addcustomercompany")

public class AddCustomerCompany extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(resJson, "Login to continue.", out);
            return;
        }

        int csaId, countryId, cityId, zip, telNum, countryCode, areaCode, faxNum, faxCountryCode, faxAreaCode,
                industryId, plant, er, mfspgm, spareParts, calib, province;
        double latitude, longitude;
        boolean signStatus;

        String company = request.getParameter("company");
        String address = request.getParameter("address");
        String contactPerson = request.getParameter("contactPerson");
        String contactPersonNumber = request.getParameter("contactNumber");
        String signature = request.getParameter("signature");
        String email = request.getParameter("email");
        String website = request.getParameter("website");
        String emergency = request.getParameter("emergency");
        Part filePart = request.getPart("photo");

        Utils.checkParameterValue(request.getParameter("csaId"), request.getParameter("faxCode"),
                request.getParameter("city"), request.getParameter("country"), request.getParameter("zip"),
                request.getParameter("areaCode"), request.getParameter("industry"), request.getParameter("plant"),
                request.getParameter("er"), request.getParameter("mf"), request.getParameter("spareParts"),
                request.getParameter("calib"), request.getParameter("province"), request.getParameter("signStatus"),
                address, signature, filePart, out, ctx);

        checkParameters(request.getParameter("faxCountryCode"), request.getParameter("countryCode"), company,
                contactPerson, contactPersonNumber, out, ctx);

        Connection connCRM = null;
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            HttpSession httpSession = request.getSession(false);
            String onlineUser = (String) httpSession.getAttribute("username");

            countryId = request.getParameter("country").equals("") ? 0 :
                    Integer.parseInt(request.getParameter("country"));

            telNum = (request.getParameter("telephone").equals("")) ? 0 :
                    Integer.parseInt(Utils.removeDash(request.getParameter("telephone")));

            faxNum = (request.getParameter("fax").equals("")) ? 0 :
                    Integer.parseInt(Utils.removeDash(request.getParameter("fax")));

            cityId = Integer.parseInt(request.getParameter("city"));
            zip = Integer.parseInt(request.getParameter("zip"));
            csaId = Integer.parseInt(request.getParameter("csaId"));
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
                Utils.printJsonException(resJson, "Invalid email/website format.", out); return;
            }

            if (!signStatus) {
                Utils.printJsonException(resJson, "Include signature.", out); return;
            }

            InputStream signatureInputStream = Utils.getSignatureInputStream(signature, ctx, conn);
            InputStream photoStream = filePart.getInputStream();

            if (photoStream == null) {
                Utils.logError("Getting photo input stream from filePart returns null.", ctx);
                Utils.printJsonException(resJson, "Photo problem occured. Try again or see logs.", out);
                return;
            }

            if (signatureInputStream == null) {
                Utils.logError("Getting signature input stream from signature string returns null.", ctx);
                Utils.printJsonException(resJson, "Signature problem occured. Try again or see logs.", out);
                return;
            }

            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());
            connCRM = Utils.getConnectionFromCRM(getServletContext());

            if (getCompanyCount(conn, company) > 0) {
                Utils.printJsonException(resJson, "Customer already encoded in MCSA.", out);
                return;
            }

            if (getCompanyCountCRM(connCRM, company) > 0) {
                Utils.printJsonException(resJson, "Customer already exists in CRM.", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT customer_id FROM customers ORDER BY customer_id DESC LIMIT 1");
            resultSet = prepStmt.executeQuery();

            int lastCustomerId = 0;
            if (resultSet.next()) {
                lastCustomerId = resultSet.getInt("customer_id");
            }   ++lastCustomerId;

            String[] alpha_industry = Utils.getIndustry(industryId, connCRM);

            prepStmt = conn.prepareStatement("INSERT INTO customers(customer_id, id_alpha, lastname, firstname, mi, " +
                    "company, address, address_lat, address_long, city_id, country_id, zip, tel_num, country_code, " +
                    "area_code, fax_num, fax_country_code, fax_area_code, date_of_birth, emergency, contact_person, " +
                    "contact_number, email, website, er, mfspgm, spare_parts, calibration, assigned_csa, date_added, " +
                    "date_stamp, classification, plant, zone, profile_photo, signature, is_deleted, cell, " +
                    "province_id, id_num, is_transferred, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE(), NOW(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            prepStmt.setInt(1, lastCustomerId);
            prepStmt.setString(2, alpha_industry[0]);
            prepStmt.setString(3, "");  //  lastname
            prepStmt.setString(4, "");  //  firstname
            prepStmt.setString(5, "");  //  mi

            prepStmt.setString(6, company);
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
            prepStmt.setDate(19, null);                     //  dateofbirth
            prepStmt.setString(20, emergency);

            prepStmt.setString(21, contactPerson);          //  contact_person
            prepStmt.setString(22, contactPersonNumber);    //  contact_number
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
            prepStmt.setString(36, contactPersonNumber+"");
            prepStmt.setInt(37, province);
            prepStmt.setInt(38, Utils.getNextIdNumber(alpha_industry[0], conn));

            prepStmt.setInt(39, 0); //  is_transferred
            prepStmt.setInt(40, 0); //  user_id
            prepStmt.execute();

            Utils.logAdd(conn, onlineUser, true);
            Utils.updateIndustryTable(alpha_industry[0], conn);
            Utils.printSuccessJson(resJson, "Successfully added customer.", out);

            filePart.getInputStream().close();
            signatureInputStream.close();

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "DB exception raised.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Exception raised", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            Utils.closeDBResource(connCRM, null, null, ctx);
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
                return false;
            }
        }

        return true;
    }

    private int getCompanyCountCRM(Connection connection, String company) throws SQLException {
        String company2 = Utils.omitSpaces(company);

        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT COUNT(*) AS customerCount FROM customers WHERE company_name = ?");

        preparedStatement.setString(1, company2);

        ResultSet resultSet = preparedStatement.executeQuery();
        int customerCount = 0;

        if (resultSet.next()) {
            customerCount = resultSet.getInt("customerCount");
        }

        preparedStatement.close();
        resultSet.close();

        return customerCount;
    }

    private int getCompanyCount(Connection connection, String company) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS customerCount FROM " +
                "customers WHERE company = ?");

        preparedStatement.setString(1, company);

        ResultSet resultSet = preparedStatement.executeQuery();
        int customerCount = 0;

        if (resultSet.next()) {
            customerCount = resultSet.getInt("customerCount");
        }

        preparedStatement.close();
        resultSet.close();

        return customerCount;
    }

    public void checkParameters(String faxCountryCode, String countryCode, String company, String contactPerson,
                                String contactPersonNumber, PrintWriter out, ServletContext ctx) {

        if (faxCountryCode == null) {
            Utils.logError("\"faxCountryCode\" parameter is null.", ctx);
            Utils.printJsonException(new JSONObject(), "Fax country code is required.", out);
            return;
        } else if (faxCountryCode.isEmpty()) {
            Utils.logError("\"faxCountryCode\" parameter is empty.", ctx);
            Utils.printJsonException(new JSONObject(), "Fax country code is required.", out);
            return;
        }

        if (countryCode == null) {
            Utils.logError("\"countryCode\" parameter is null.", ctx);
            Utils.printJsonException(new JSONObject(), "Country code is required.", out);
            return;
        } else if (countryCode.isEmpty()) {
            Utils.logError("\"countryCode\" parameter is empty.", ctx);
            Utils.printJsonException(new JSONObject(), "Country code is required.", out);
            return;
        }

        if (company == null) {
            Utils.logError("\"company\" parameter is null.", ctx);
            Utils.printJsonException(new JSONObject(), "Company is required.", out);
            return;
        } else if (company.isEmpty()) {
            Utils.logError("\"company\" parameter is empty.", ctx);
            Utils.printJsonException(new JSONObject(), "Company is required.", out);
            return;
        }

        if (contactPerson == null) {
            Utils.logError("\"contactPerson\" parameter is null.", ctx);
            Utils.printJsonException(new JSONObject(), "Contact person is required.", out);
            return;
        } else if (contactPerson.isEmpty()) {
            Utils.logError("\"contactPerson\" parameter is empty.", ctx);
            Utils.printJsonException(new JSONObject(), "Contact person is required.", out);
            return;
        }

        if (contactPersonNumber == null) {
            Utils.logError("\"contactPersonNumber\" parameter is null.", ctx);
            Utils.printJsonException(new JSONObject(), "Contact person number is required.", out);
        } else if (contactPersonNumber.isEmpty()) {
            Utils.logError("\"contactPersonNumber\" parameter is empty.", ctx);
            Utils.printJsonException(new JSONObject(), "Contact person number is required.", out);
        }
    }
}