package wmdc.mobilecsa.servlet;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet("/transfercustomer")

public class TransferCustomer extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject resJson = new JSONObject();
        PrintWriter out = response.getWriter();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        String customerIdStr = request.getParameter("customerId");
        if (customerIdStr == null) {
            Utils.logError("\"customerId\" parameter is null.", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (customerIdStr.isEmpty()) {
            Utils.logError("\"customerId\" parameter is empty.", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        Connection connCRM = null;
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());
            connCRM = Utils.getConnectionFromCRM(getServletContext());

            int customerId = Integer.parseInt(customerIdStr);
            if (getCustomerCount(customerId, conn) < 1) {
                Utils.logError("No customer found to transfer using customerId: "+customerId, ctx);
                Utils.printJsonException(resJson, "No customer found to transfer.", out);
                return;
            }
            if (!Utils.inspectAddress(customerId, conn)) {
                Utils.printJsonException(resJson, "Set customer location before transfer.", out);
                return;
            }

            int cityId, countryId, zip, areaCode, faxAreaCode, er, mfspgm, spareParts, calibration, zone, isDeleted,
                    provinceId, telNum, faxNum, assignedCsa;

            cityId = countryId = zip = areaCode = faxAreaCode = er = mfspgm = spareParts = calibration = zone =
                    isDeleted = provinceId = telNum = faxNum = assignedCsa = 0;

            String idAlpha, mi, company, address, emergency, contactPerson, email, website, dateStamp, classification,
                    plant, cell, dateOfBirth, lastname, firstname, dateAdded;

            idAlpha = mi = company = address = emergency = contactPerson = email = website = dateStamp =
                    classification = plant = cell = dateOfBirth = lastname = firstname = dateAdded = "";

            InputStream profilePhoto = null;
            InputStream signature = null;

            prepStmt = conn.prepareStatement("SELECT firstname, lastname, id_alpha, mi, company, address, city_id, " +
                    "country_id, zip, country_code, area_code, fax_country_code, fax_area_code, date_of_birth, " +
                    "emergency, contact_person, contact_number, email, website, er, mfspgm, spare_parts, calibration, "+
                    "date_stamp, classification, plant, zone, profile_photo, signature, is_deleted, province_id, " +
                    "cell, id_num, tel_num, fax_num, date_of_birth, date_added, assigned_csa FROM customers " +
                    "WHERE customer_id = ?");

            prepStmt.setInt(1, customerId);
            resultSet = prepStmt.executeQuery();

            if (resultSet.next()) {
                cityId = resultSet.getInt("city_id");
                countryId = resultSet.getInt("country_id");
                zip = resultSet.getInt("zip");
                areaCode = resultSet.getInt("area_code");
                faxAreaCode = resultSet.getInt("fax_area_code");
                er = resultSet.getInt("er");
                mfspgm = resultSet.getInt("mfspgm");
                spareParts = resultSet.getInt("spare_parts");
                calibration = resultSet.getInt("calibration");
                zone = resultSet.getInt("zone");
                isDeleted = resultSet.getInt("is_deleted");
                provinceId = resultSet.getInt("province_id");
                telNum = resultSet.getInt("tel_num");
                faxNum = resultSet.getInt("fax_num");
                assignedCsa = resultSet.getInt("assigned_csa");

                firstname = resultSet.getString("firstname");
                lastname =resultSet.getString("lastname");
                idAlpha = resultSet.getString("id_alpha");
                mi = resultSet.getString("mi");
                company = resultSet.getString("company");
                address = resultSet.getString("address");
                emergency = resultSet.getString("emergency");
                contactPerson = resultSet.getString("contact_person");
                email = resultSet.getString("email");
                website = resultSet.getString("website");
                dateStamp = resultSet.getString("date_stamp");
                classification = resultSet.getString("classification");
                plant = resultSet.getString("plant");
                cell = resultSet.getString("cell");
                dateOfBirth = resultSet.getString("date_of_birth");
                dateAdded = resultSet.getString("date_added");

                profilePhoto = resultSet.getBlob("profile_photo").getBinaryStream();
                signature = resultSet.getBlob("signature").getBinaryStream();
            }

            lastname = Utils.replaceMultipleWhitespace(lastname);
            firstname = Utils.replaceMultipleWhitespace(firstname);

            if (getCustomerCount(lastname, firstname, connCRM) > 0) {
                Utils.printJsonException(resJson, "Customer already in crm.", out);
                return;
            }
            if (getCustomerCount(Utils.filterSpecialChars(lastname),
                    Utils.filterSpecialChars(firstname), connCRM) > 0) {
                Utils.printJsonException(resJson, "Customer already in crm.", out);
                return;
            }

            int nextIdNum = 1+ Utils.getMaxIdInCustomers(idAlpha, connCRM);

            lastname = WordUtils.capitalizeFully(lastname);
            firstname = WordUtils.capitalizeFully(firstname);
            mi = mi.toUpperCase();

            String[] dateSplit = splitDate(dateOfBirth);

            prepStmt = connCRM.prepareStatement(
                "INSERT INTO customers (" +
                    "id_alpha, id_num, company_name, lname, " +
                    "fname, mi, street, province, city, " +
                    "country, postal, month, day, year, " +
                    "salutation, emergency, code1, tel1, code2, " +
                    "tel2, code3, tel3, code4, tel4, " +
                    "faxcode, fax, cell, email, website, " +
                    "payment, terms, credit_limit, checks, " +
                    "days, invoice, er, mfspgm, spare_parts, " +
                    "calibration, assigned_csa, date_associated, classification, plant, " +
                    "zone, is_deleted, contact_person, date_stamp, user_id, " +
                    "status, verified_by, verified_date)" +
                    "VALUES (" +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            prepStmt.setString(1, idAlpha);         //  id_alpha
            prepStmt.setInt(2, nextIdNum);          //  id_num
            prepStmt.setString(3, company);         //  company_name
            prepStmt.setString(4, lastname);        //  lname

            prepStmt.setString(5, firstname);       //  fname
            prepStmt.setString(6, mi);              //  mi
            prepStmt.setString(7, WordUtils.capitalizeFully(address));         //  street
            prepStmt.setString(8, Utils.getProvinceNameInCRM(provinceId, connCRM));    //  province
            prepStmt.setString(9, Utils.getCityNameInCRM(cityId, connCRM));            //  city

            prepStmt.setString(10, Utils.getCountryNameInCRM(countryId, connCRM));     //  country
            prepStmt.setInt(11, zip);                               //  postal
            prepStmt.setInt(12, Integer.parseInt(dateSplit[1]));    //  month
            prepStmt.setInt(13, Integer.parseInt(dateSplit[2]));    //  day
            prepStmt.setInt(14, Integer.parseInt(dateSplit[0]));    //  year

            prepStmt.setInt(15, 0);                  //  salutation
            prepStmt.setString(16, WordUtils.capitalizeFully(emergency));       //  emergency
            prepStmt.setString(17, ""+areaCode);     //  code1
            prepStmt.setString(18, ""+telNum);       //  tel1
            prepStmt.setInt(19, 0);                  //  code2

            prepStmt.setInt(20, 0);     //  tel2
            prepStmt.setInt(21, 0);     //  code3
            prepStmt.setInt(22, 0);     //  tel3
            prepStmt.setInt(23, 0);     //  code4
            prepStmt.setInt(24, 0);     //  tel4

            prepStmt.setInt(25, faxAreaCode);    //  faxcode
            prepStmt.setInt(26, faxNum);         //  fax
            prepStmt.setString(27, cell);        //  cell
            prepStmt.setString(28, email);       //  email
            prepStmt.setString(29, website);     //  website

            prepStmt.setString(30, "");             //  payment
            prepStmt.setInt(31, 0);                 //  terms
            prepStmt.setString(32, "");             //  credit_limit
            prepStmt.setString(33, "");             //  checks

            prepStmt.setInt(34, 0);             //  days
            prepStmt.setString(35, "");         //  invoice
            prepStmt.setInt(36, er);            //  er
            prepStmt.setInt(37, mfspgm);        //  mfpspgm
            prepStmt.setInt(38, spareParts);    //  spare_parts

            prepStmt.setInt(39, calibration);                           //  calibration
            prepStmt.setInt(40, assignedCsa);                           //  assigned_csa
            prepStmt.setDate(41, Utils.getDate(dateAdded, ctx));     //  date_associated
            prepStmt.setString(42, classification);                     //  classification
            prepStmt.setString(43, plant);                              //  plant

            prepStmt.setInt(44, zone);                                           //  zone
            prepStmt.setInt(45, isDeleted);                                      //  is_deleted
            prepStmt.setString(46, WordUtils.capitalizeFully(contactPerson));    //  contact_person
            prepStmt.setTimestamp(47, Timestamp.valueOf(dateStamp));             //  date_stamp
            prepStmt.setInt(48, 0);                                              //  user_id

            prepStmt.setInt(49, 0);      //  status
            prepStmt.setInt(50, 0);      //  verified_by
            prepStmt.setString(51, "0000-00-00 00:00:00");      //  verified_date

            //  transfer
            prepStmt.execute();

            //  get recent customer_id inserted
            int recentId = Utils.getRecentId(prepStmt);

            //  transfer signature
            prepStmt = connCRM.prepareStatement(
                    "INSERT INTO signatures (signature, source, source_id, date_stamp) VALUES (?, ?, ?, NOW())");
            prepStmt.setBlob(1, signature);
            prepStmt.setString(2, "Customer");
            prepStmt.setInt(3, recentId);
            prepStmt.executeUpdate();

            //  transfer profile_photo
            prepStmt = connCRM.prepareStatement(
                    "INSERT INTO photos (photo, user_id, date_stamp, source, source_id) VALUES (?, ?, NOW(), ?, ?)");
            prepStmt.setBlob(1, profilePhoto);
            prepStmt.setInt(2, assignedCsa);
            prepStmt.setString(3, "Customer");
            prepStmt.setInt(4, recentId);
            prepStmt.executeUpdate();

            prepStmt = conn.prepareStatement(
                    "UPDATE customers SET is_deleted = ?, is_transferred = ? WHERE customer_id = ?");
            prepStmt.setInt(1, 1);
            prepStmt.setInt(2, 1);
            prepStmt.setInt(3, customerId);
            prepStmt.executeUpdate();

            Utils.logTransfer(conn, request.getSession(false).getAttribute("username").toString(), true);

            profilePhoto.close();
            signature.close();
            Utils.printSuccessJson(resJson, "Successfully Transferred customer", out);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(), ctx);
            Utils.printJsonException(resJson, "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx);
            Utils.printJsonException(resJson, "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            Utils.closeDBResource(connCRM, null, null, ctx);
            out.close();
        }
    }

    private int getCustomerCount(String lastname, String firstname, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) as customerCount FROM customers WHERE lname = ? AND fname = ?");
        prepStmt.setString(1, lastname);
        prepStmt.setString(2, firstname);
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

    private String[] splitDate(String dateOfBirth) {
        String[] dateSplit;

        if (dateOfBirth == null) {
            dateSplit = new String[3];
            dateSplit[0] = "0";
            dateSplit[1] = "0";
            dateSplit[2] = "0";
        } else if (dateOfBirth.equals("")) {
            dateSplit = new String[3];
            dateSplit[0] = "0";
            dateSplit[1] = "0";
            dateSplit[2] = "0";
        } else {
            dateSplit = dateOfBirth.split("-");
        }

        return dateSplit;
    }

    private int getCustomerCount(int customerId, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS customerCount FROM customers WHERE customer_id = ?");
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