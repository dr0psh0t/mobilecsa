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

@WebServlet("/transfercompany")
public class TransferCompany extends HttpServlet {

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

        String customerIdString = request.getParameter("customerId");
        if (customerIdString == null) {
            Utils.logError("\"customerId\" is null", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (customerIdString.isEmpty()) {
            Utils.logError("\"customerId\" is empty", ctx);
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

            int customerId = Integer.parseInt(customerIdString);

            if (getCompanyCount(customerId, conn) < 1) {
                Utils.logError("No customer found to transfer using customerId: "+customerId, ctx);
                Utils.printJsonException(resJson, "No customer found to transfer", out);
                return;
            }

            if (!Utils.inspectAddress(customerId, conn)) {
                Utils.printJsonException(resJson, "Set the customer location before transfer", out);
                return;
            }

            int province, cityId, countryId, zip, areaCode, faxAreaCode, er, mfspgm, calibration, spareParts,
            assignedCsa, zone, isDeleted, telephone, fax;

            province = cityId = countryId = zip = areaCode = faxAreaCode = er = mfspgm = calibration = spareParts =
                    assignedCsa = zone = isDeleted = telephone = fax = 0;

            String idAlpha, lastname, firstname, mi, address, cell, emergency, email, website, dateAdded, dateStamp,
                    classification, plant, contactPerson, company;

            idAlpha = lastname = firstname = mi = address = cell = emergency = email = website = dateAdded =
                    dateStamp = classification = plant = contactPerson = company = "";

            InputStream profilePhoto = null;
            InputStream signature = null;

            prepStmt = conn.prepareStatement("SELECT province_id, city_id, country_id, zip, country_code, area_code, "+
                    "fax_country_code, fax_area_code, er, mfspgm, calibration, spare_parts, assigned_csa, zone, " +
                    "fax_num, tel_num, id_alpha, lastname, firstname, mi, address, cell, date_of_birth, emergency, " +
                    "email, website, date_added, date_stamp, classification, plant, profile_photo, signature, " +
                    "is_deleted, id_num, contact_person, company FROM customers WHERE customer_id = ?",
                    Statement.RETURN_GENERATED_KEYS);

            prepStmt.setInt(1, customerId);
            resultSet = prepStmt.executeQuery();

            if (resultSet.next()) {
                province = resultSet.getInt("province_id");
                cityId = resultSet.getInt("city_id");
                countryId = resultSet.getInt("country_id");
                zip = resultSet.getInt("zip");
                areaCode = resultSet.getInt("area_code");
                faxAreaCode = resultSet.getInt("fax_area_code");
                er = resultSet.getInt("er");
                mfspgm = resultSet.getInt("mfspgm");
                calibration = resultSet.getInt("calibration");
                spareParts = resultSet.getInt("spare_parts");
                assignedCsa = resultSet.getInt("assigned_csa");
                zone = resultSet.getInt("zone");
                isDeleted = resultSet.getInt("is_deleted");
                telephone = resultSet.getInt("tel_num");
                fax = resultSet.getInt("fax_num");

                idAlpha = resultSet.getString("id_alpha");
                lastname = resultSet.getString("lastname");
                firstname = resultSet.getString("firstname");
                mi = resultSet.getString("mi");
                address = resultSet.getString("address");
                cell = resultSet.getString("cell");
                emergency = resultSet.getString("emergency");
                email = resultSet.getString("email");
                website = resultSet.getString("website");
                dateAdded = resultSet.getString("date_added");
                dateStamp = resultSet.getString("date_stamp");
                classification = resultSet.getString("classification");
                plant = resultSet.getString("plant");

                profilePhoto = resultSet.getBlob("profile_photo").getBinaryStream();
                signature = resultSet.getBlob("signature").getBinaryStream();

                company = resultSet.getString("company");
                contactPerson = resultSet.getString("contact_person");
            }

            company = Utils.replaceMultipleWhitespace(company);
            if (getCompanyCount(company, connCRM) > 0) {
                Utils.printJsonException(resJson, "Customer already in crm.", out);
                return;
            }
            if (getCompanyCount(Utils.filterSpecialChars(company), connCRM) > 0) {
                Utils.printJsonException(resJson, "Customer already in crm.", out);
                return;
            }

            int nextIdNum = 1+ Utils.getMaxIdInCustomers(idAlpha, connCRM);
            company = WordUtils.capitalizeFully(company);

            prepStmt = connCRM.prepareStatement("INSERT INTO customers (id_alpha, id_num, company_name, lname, " +
                    "fname, mi, street, province, city, country, postal, month, day, year, salutation, emergency, " +
                    "code1, tel1, code2, tel2, code3, tel3, code4, tel4, faxcode, fax, cell, email, website, " +
                    "payment, terms, credit_limit, checks, days, invoice, er, mfspgm, spare_parts, calibration, " +
                    "assigned_csa, date_associated, classification, plant, zone, is_deleted, contact_person, " +
                    "date_stamp, user_id, status, verified_by, verified_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            prepStmt.setString(1, idAlpha);      //  id_alpha
            prepStmt.setInt(2, nextIdNum);        //  id_num
            prepStmt.setString(3, company);      //  company_name
            prepStmt.setString(4, lastname);     //  lname

            prepStmt.setString(5, firstname);    //  fname
            prepStmt.setString(6, mi);           //  mi
            prepStmt.setString(7, WordUtils.capitalizeFully(address));      //  street
            prepStmt.setString(8, Utils.getProvinceNameInCRM(province, connCRM));       //  province
            prepStmt.setString(9, Utils.getCityNameInCRM(cityId, connCRM));             //  city

            prepStmt.setString(10, Utils.getCountryNameInCRM(countryId, connCRM));
            prepStmt.setInt(11, zip);    //  postal
            prepStmt.setInt(12, 0);      //  month
            prepStmt.setInt(13, 0);      //  day
            prepStmt.setInt(14, 0);      //  year

            prepStmt.setInt(15, 0);              //  salutation
            prepStmt.setString(16, WordUtils.capitalizeFully(emergency));   //  emergency
            prepStmt.setInt(17, areaCode);       //  code1
            prepStmt.setInt(18, telephone);      //  tel1
            prepStmt.setInt(19, 0);              //  code2

            prepStmt.setInt(20, 0);      //  tel2
            prepStmt.setInt(21, 0);      //  code3
            prepStmt.setInt(22, 0);      //  tel3
            prepStmt.setInt(23, 0);      //  code4
            prepStmt.setInt(24, 0);      //  tel4

            prepStmt.setInt(25, faxAreaCode);    //  faxcode
            prepStmt.setInt(26, fax);            //  fax
            prepStmt.setString(27, cell);        //  cell
            prepStmt.setString(28, email);       //  email
            prepStmt.setString(29, website);     //  website

            prepStmt.setString(30, "");          //  payment
            prepStmt.setInt(31, 0);              //  terms
            prepStmt.setString(32, "");          //  credit_limit
            prepStmt.setString(33, "");          //  checks

            prepStmt.setInt(34, 0);              //  days
            prepStmt.setString(35, "");          //  invoice
            prepStmt.setInt(36, er);             //  er
            prepStmt.setInt(37, mfspgm);         //  mfspgm
            prepStmt.setInt(38, spareParts);     //  spare_parts

            prepStmt.setInt(39, calibration);                        //  calibration
            prepStmt.setInt(40, assignedCsa);                        //  assiged_csa
            prepStmt.setDate(41, Utils.getDate(dateAdded, ctx, conn));  //  date_associated
            prepStmt.setString(42, classification);                  //  classification
            prepStmt.setString(43, plant);                           //  plant

            prepStmt.setInt(44, zone);                                           //  zone
            prepStmt.setInt(45, isDeleted);                                      //  is_deleted
            prepStmt.setString(46, WordUtils.capitalizeFully(contactPerson));    //  contact_person
            prepStmt.setTimestamp(47, Timestamp.valueOf(dateStamp));             //  date_stamp
            prepStmt.setInt(48, 0);                                              //  user_id

            prepStmt.setInt(49, 0);      //  status
            prepStmt.setInt(50, 0);      //  verified_by
            prepStmt.setString(51, "0000-00-00 00:00:00");      //  verified_date
            prepStmt.execute();

            int recentId = Utils.getRecentId(prepStmt);

            prepStmt = connCRM.prepareStatement(
                    "INSERT INTO signatures (signature, source, source_id, date_stamp) VALUES (?, ?, ?, ?)");
            prepStmt.setBlob(1, signature);
            prepStmt.setString(2, "Customer");
            prepStmt.setInt(3, recentId);
            prepStmt.setTimestamp(4, Timestamp.valueOf(dateStamp));
            prepStmt.executeUpdate();

            prepStmt = connCRM.prepareStatement(
                    "INSERT INTO photos (photo, user_id, date_stamp, source, source_id) VALUES (?, ?, ?, ?, ?)");
            prepStmt.setBlob(1, profilePhoto);
            prepStmt.setInt(2, assignedCsa);
            prepStmt.setTimestamp(3, Timestamp.valueOf(dateStamp));
            prepStmt.setString(4, "Customer");
            prepStmt.setInt(5, recentId);
            prepStmt.executeUpdate();

            prepStmt = conn.prepareStatement(
                    "UPDATE customers SET is_deleted = ?, is_transferred = ? WHERE customer_id = ?");
            prepStmt.setInt(1, 1);
            prepStmt.setInt(2, 1);
            prepStmt.setInt(3, customerId);
            prepStmt.executeUpdate();

            Utils.logTransfer(conn, (String) request.getSession(false).getAttribute("username"), true);

            profilePhoto.close();
            signature.close();
            Utils.printSuccessJson(resJson, "Successfully transferred customer", out);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(resJson, "Cannot transfer company at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(resJson, "Cannot transfer company at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            Utils.closeDBResource(connCRM, null, null, ctx);
            out.close();
        }
    }

    private int getCompanyCount(String company, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) as customerCount FROM customers WHERE company_name = ?");
        prepStmt.setString(1, company);
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

    private int getCompanyCount(int customerId, Connection conn) throws SQLException {
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