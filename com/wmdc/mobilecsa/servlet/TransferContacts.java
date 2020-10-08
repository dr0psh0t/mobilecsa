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

@WebServlet("/transfercontact")

public class TransferContacts extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject resJson = new JSONObject();
        PrintWriter out = response.getWriter();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(resJson, "Login first", out);
            return;
        }

        String contactIdStr = request.getParameter("contactId");
        if (contactIdStr == null) {
            Utils.logError("\"contactId\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (contactIdStr.isEmpty()) {
            Utils.logError("\"contactId\" parameter is empty", ctx);
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

            int contactId = Integer.parseInt(contactIdStr);
            if (getContactCount(contactId, conn) < 1) {
                Utils.logError("No contacts found to transfer using contactId: "+contactId, ctx);
                Utils.printJsonException(resJson, "No contacts found to transfer", out);
                return;
            }
            if (!Utils.inspectAddressContact(contactId, conn)) {
                Utils.printJsonException(resJson, "Set contact location before transfer.", out);
                return;
            }

            int industryId, cityId, provinceId, countryId, zipCode, csaId, areaCode, telNum, faxNumber, faxCode,
                    plantAssociatedId, spareParts, isDeleted, er, mf, calib;

            industryId = cityId = provinceId = countryId = zipCode = csaId = areaCode = telNum = faxNumber = faxCode =
                    plantAssociatedId = spareParts = isDeleted = er = mf = calib = 0;

            String idAlpha, mi, dateOfBirth, address, dateAssociated, email, website, emergency, dateStamp, mobile,
                    lastname, firstname, jobPosition;

            idAlpha = mi = dateOfBirth = address = dateAssociated = email = website = emergency = dateStamp = mobile =
                    lastname = firstname = jobPosition = "";

            InputStream profilePhoto = null;
            InputStream signature = null;

            prepStmt = conn.prepareStatement("SELECT industry_id, city_id, province_id, country_id, zip_code, " +
                    "csa_id, area_code, tel_num, fax_number, fax_code, plant_associated_id, phone, spare_parts, " +
                    "id_alpha, mi, date_of_birth, address, date_associated, email, website, emergency, " +
                    "emergency_contact, date_stamp, specimen, signature, mobile, id_num, lastname, firstname, " +
                    "job_position, is_deleted, er, mf_gm, calib FROM contacts WHERE contact_id = ?");
            prepStmt.setInt(1, contactId);
            resultSet = prepStmt.executeQuery();

            while (resultSet.next()) {
                industryId = resultSet.getInt("industry_id");
                cityId = resultSet.getInt("city_id");
                provinceId = resultSet.getInt("province_id");
                countryId = resultSet.getInt("country_id");
                zipCode = resultSet.getInt("zip_code");
                csaId = resultSet.getInt("csa_id");
                areaCode = resultSet.getInt("area_code");
                telNum = resultSet.getInt("tel_num");
                faxNumber = resultSet.getInt("fax_number");
                faxCode = resultSet.getInt("fax_code");
                plantAssociatedId = resultSet.getInt("plant_associated_id");
                spareParts = resultSet.getInt("spare_parts");
                isDeleted = resultSet.getInt("is_deleted");
                er = resultSet.getInt("er");
                mf = resultSet.getInt("mf_gm");
                calib = resultSet.getInt("calib");

                idAlpha = resultSet.getString("id_alpha");
                mi = resultSet.getString("mi");
                dateOfBirth = resultSet.getString("date_of_birth");
                address = resultSet.getString("address");
                dateAssociated = resultSet.getString("date_associated");
                email = resultSet.getString("email");
                website = resultSet.getString("website");
                emergency = resultSet.getString("emergency");
                dateStamp = resultSet.getString("date_stamp");
                mobile = resultSet.getString("mobile");
                firstname = resultSet.getString("firstname");
                lastname = resultSet.getString("lastname");
                jobPosition = resultSet.getString("job_position");

                profilePhoto = resultSet.getBlob("specimen").getBinaryStream();
                signature = resultSet.getBlob("signature").getBinaryStream();
            }

            lastname = Utils.replaceMultipleWhitespace(lastname);
            firstname = Utils.replaceMultipleWhitespace(firstname);

            if (getContactCount(lastname, firstname, connCRM) > 0) {
                Utils.printJsonException(resJson, "Customer already in crm", out);
                return;
            }
            if (getContactCount(Utils.filterSpecialChars(lastname),
                    Utils.filterSpecialChars(firstname), connCRM) > 0) {
                Utils.printJsonException(resJson, "Customer already in crm.", out);
                return;
            }

            int nextIdNum = 1+ Utils.getMaxIdInCustomers(idAlpha, connCRM);

            lastname = WordUtils.capitalizeFully(lastname);
            firstname = WordUtils.capitalizeFully(firstname);
            mi = mi.toUpperCase();

            String[] dateSplit = dateOfBirth.split("-");

            prepStmt = connCRM.prepareStatement("INSERT INTO contacts (id_alpha, id_num, plant, lname, fname, mi, " +
                    "month, day, year, address, city, province, country, postal, csa, date_associated, industry, " +
                    "job_pos, code1, tele1, code2, tele2, faxcode, fax, cellphone, email, website, payment, terms, " +
                    "credit, checks, days, incentives, percent, salu, emergency, is_deleted, er, mfspgm, " +
                    "calibration, spare_parts, user_id, date_stamp, status, verified_by, verified_date) VALUES (?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            prepStmt.setString(1, idAlpha);      //  id_alpha
            prepStmt.setInt(2, nextIdNum);           //  id_num
            prepStmt.setString(3, Utils.getPlantByIdInCRM(plantAssociatedId, connCRM));    //  plant
            prepStmt.setString(4, lastname);     //  lname

            prepStmt.setString(5, firstname);                       //   fname
            prepStmt.setString(6, mi);                              //   mi
            prepStmt.setInt(7, Integer.parseInt(dateSplit[1]));     //  month
            prepStmt.setInt(8, Integer.parseInt(dateSplit[2]));     //  day
            prepStmt.setInt(9, Integer.parseInt(dateSplit[0]));     //  year

            prepStmt.setString(10, WordUtils.capitalizeFully(address));        //  address
            prepStmt.setString(11, Utils.getCityNameInCRM(cityId, connCRM));           //  city
            prepStmt.setString(12, Utils.getProvinceNameInCRM(provinceId, connCRM));   //  province
            prepStmt.setString(13, Utils.getCountryNameInCRM(countryId, connCRM));     //  country

            prepStmt.setInt(14, zipCode);               //  postal
            prepStmt.setInt(15, csaId);                 //  csa
            prepStmt.setString(16, dateAssociated);     //  date_associated
            prepStmt.setString(17, Utils.getIndustryName(industryId, connCRM));   //  industry
            prepStmt.setString(18, WordUtils.capitalizeFully(jobPosition));        //  job_pos

            prepStmt.setInt(19, areaCode);      //  code1
            prepStmt.setInt(20, telNum);        //  tel1
            prepStmt.setInt(21, 0);             //  code2
            prepStmt.setInt(22, 0);             //  tel2
            prepStmt.setInt(23, faxCode);       //  faxcode

            prepStmt.setInt(24, faxNumber);        //   fax
            prepStmt.setString(25, mobile);        //   cellphone
            prepStmt.setString(26, email);         //   email
            prepStmt.setString(27, website);       //   website
            prepStmt.setInt(28, 0);                //   payment

            prepStmt.setInt(29, 0);                 //   terms
            prepStmt.setInt(30, 0);                 //   credit
            prepStmt.setString(31, "");             //   checks
            prepStmt.setInt(32, 0);                 //   days
            prepStmt.setString(33, "");             //   incentives

            prepStmt.setInt(34, 0);                                            //  percent
            prepStmt.setInt(35, 0);                                            //   salu
            prepStmt.setString(36, WordUtils.capitalizeFully(emergency));      //   emergency
            prepStmt.setInt(37, isDeleted);                                    //   is_deleted
            prepStmt.setInt(38, er);                                           //   er

            prepStmt.setInt(39, mf);                                    //  mfspgm
            prepStmt.setInt(40, calib);                                 //  calibration
            prepStmt.setInt(41, spareParts);                            //  spare_parts
            prepStmt.setInt(42, 0);                                     //  user_id
            prepStmt.setTimestamp(43, Timestamp.valueOf(dateStamp));    //  date_stamp

            prepStmt.setInt(44, 0);      //  status
            prepStmt.setInt(45, 0);      //  verified_by
            prepStmt.setString(46, "0000-00-00 00:00:00");      //  verified_date
            prepStmt.execute();

            int recentId = Utils.getRecentId(prepStmt);

            prepStmt = connCRM.prepareStatement(
                    "INSERT INTO signatures (signature, source, source_id, date_stamp) VALUES (?, ?, ?, NOW())");
            prepStmt.setBlob(1, signature);
            prepStmt.setString(2, "Contact");
            prepStmt.setInt(3, recentId);
            prepStmt.executeUpdate();

            prepStmt = connCRM.prepareStatement("INSERT INTO profile_photos (photo, user_id, date_stamp, source, " +
                    "source_id) VALUES (?, ?, NOW(), ?, ?)");
            prepStmt.setBlob(1, profilePhoto);
            prepStmt.setInt(2, csaId);
            prepStmt.setString(3, "Contact");
            prepStmt.setInt(4, recentId);
            prepStmt.executeUpdate();

            prepStmt = conn.prepareStatement(
                    "UPDATE contacts SET is_deleted = ?, is_transferred = ? WHERE contact_id = ?");
            prepStmt.setInt(1, 1);
            prepStmt.setInt(2, 1);
            prepStmt.setInt(3, contactId);
            prepStmt.executeUpdate();

            Utils.logTransfer(conn, (String) request.getSession(false).getAttribute("username"), false);

            profilePhoto.close();
            signature.close();
            Utils.printSuccessJson(resJson, "Successfully transferred customer.", out);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot transfer contact at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot transfer contact at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            Utils.closeDBResource(connCRM, null, null, ctx);
            out.close();
        }
    }

    private int getContactCount(String lastname, String firstname, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) as contactCount FROM contacts WHERE lname = ? AND fname = ?");
        prepStmt.setString(1, lastname);
        prepStmt.setString(2, firstname);
        ResultSet resultSet = prepStmt.executeQuery();

        int customerCount = 0;
        if (resultSet.next()) {
            customerCount = resultSet.getInt("contactCount");
        }

        prepStmt.close();
        resultSet.close();

        return customerCount;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.redirectIfNull(request, response);
    }

    private int getContactCount(int contactId, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) as contactCount FROM contacts WHERE contact_id = ?");
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