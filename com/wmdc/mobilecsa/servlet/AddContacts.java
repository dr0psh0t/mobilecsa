package wmdc.mobilecsa.servlet;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@MultipartConfig(fileSizeThreshold=1024*1024*6, // 5MB
        maxFileSize=1024*1024*3,      // 3MB
        maxRequestSize=1024*1024*50)   // 50MB
@WebServlet("/addcontacts")
public class AddContacts extends HttpServlet {
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

        int industry, city, province, country, zip, plant, er, mf, calib, spareParts, areaCode, telephone, fax,
                faxCode, csaId;
        double lat, lng;
        boolean signStatus;

        String lastname = request.getParameter("lastname");
        String firstname = request.getParameter("firstname");
        String mi = request.getParameter("mi");
        String company = request.getParameter("company");
        String birthDate = request.getParameter("birthDate");
        String address = request.getParameter("address");
        String jobPosition = request.getParameter("jobPosition");
        String mobile = request.getParameter("mobile");
        String emergencyContact = request.getParameter("emergencyContact");
        String signature = request.getParameter("signature");
        String email = request.getParameter("email");
        String website = request.getParameter("website");
        String emergency = request.getParameter("emergency");
        Part filePart = request.getPart("specimenPhoto");

        Utils.checkParameterValue(request.getParameter("csaId"), request.getParameter("faxCode"),
                request.getParameter("city"), request.getParameter("country"), request.getParameter("zip"),
                request.getParameter("areaCode"), request.getParameter("industry"), request.getParameter("plant"),
                request.getParameter("er"), request.getParameter("mf"), request.getParameter("spareParts"),
                request.getParameter("calib"), request.getParameter("province"), request.getParameter("signStatus"),
                address, signature, filePart, out);

        checkParameters(lastname, firstname, birthDate, mi, mobile, company, jobPosition, out);

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

            csaId = Integer.parseInt(request.getParameter("csaId"));
            industry = Integer.parseInt(request.getParameter("industry"));
            city = Integer.parseInt(request.getParameter("city"));
            province = Integer.parseInt(request.getParameter("province"));
            zip = Integer.parseInt(request.getParameter("zip"));
            plant = Integer.parseInt(request.getParameter("plant"));
            er = Integer.parseInt(request.getParameter("er"));
            mf = Integer.parseInt(request.getParameter("mf"));
            calib = Integer.parseInt(request.getParameter("calib"));
            spareParts = Integer.parseInt(request.getParameter("spareParts"));
            areaCode = Integer.parseInt(request.getParameter("areaCode"));

            country = request.getParameter("country").equals("") ? 0 :
                    Integer.parseInt(request.getParameter("country"));

            telephone = (request.getParameter("telephone").equals("")) ? 0 :
                    Integer.parseInt(Utils.getFiltered(request.getParameter("telephone")));

            fax = (request.getParameter("fax").equals("")) ? 0 :
                    Integer.parseInt(Utils.getFiltered(request.getParameter("fax")));

            faxCode = Integer.parseInt(Utils.getFiltered(request.getParameter("faxCode")));

            lat = Double.parseDouble(request.getParameter("lat"));
            lng = Double.parseDouble(request.getParameter("lng"));
            signStatus = Boolean.parseBoolean(request.getParameter("signStatus"));

            jobPosition = Utils.replaceMultipleWhitespace(jobPosition);

            if (!inspectEmailWebsite(email, website)) {
                Utils.printJsonException(resJson, "Invalid email/website format.", out);
                return;
            }

            if (!signStatus) {
                Utils.printJsonException(resJson, "Include signature.", out);
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

            lastname = Utils.getFiltered(lastname);
            firstname = Utils.getFiltered(firstname);

            if (getContactCount(lastname, firstname, conn) > 0) {
                Utils.printJsonException(resJson, "Contact already exist in MCSA.", out);
                return;
            }

            if (getContactCountCRM(lastname, firstname, connCRM) > 0) {
                Utils.printJsonException(resJson, "Contact already exist in CRM.", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT contact_id FROM contacts ORDER BY contact_id DESC LIMIT 1");
            resultSet = prepStmt.executeQuery();

            int lastContactId = 0;
            if (resultSet.next()) {lastContactId = resultSet.getInt("contact_id"); }
            ++lastContactId;

            String[] alpha_industry = Utils.getIndustry(industry, connCRM);

            prepStmt = conn.prepareStatement("INSERT INTO contacts (contact_id, id_alpha, industry_id, lastname, " +
                    "firstname, mi, date_of_birth, address, city_id, province_id, country_id, zip_code, csa_id, " +
                    "date_associated, job_position, area_code, tel_num, fax_number, fax_code, plant_associated_id, " +
                    "specimen, signature, phone, mobile, email, website, emergency, emergency_contact, er, mf_gm, " +
                    "calib, spare_parts, user_id, date_stamp, is_deleted, company, id_num, address_lat, " +
                    "address_long, is_transferred) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?)");

            prepStmt.setInt(1, lastContactId);
            prepStmt.setString(2, alpha_industry[2]);  //  <-  id_alpha is id2 for contacts
            prepStmt.setInt(3, industry);
            prepStmt.setString(4, lastname);
            prepStmt.setString(5, firstname);

            prepStmt.setString(6, mi);
            prepStmt.setDate(7, Utils.getDate(birthDate));
            prepStmt.setString(8, address);
            prepStmt.setInt(9, city);
            prepStmt.setInt(10, province);

            prepStmt.setInt(11, country);
            prepStmt.setInt(12, zip);
            prepStmt.setInt(13, csaId);
            prepStmt.setString(14, jobPosition);

            prepStmt.setInt(15, areaCode);
            prepStmt.setInt(16, telephone);
            prepStmt.setInt(17, fax);  //  get fax
            prepStmt.setInt(18, faxCode);
            prepStmt.setInt(19, plant);

            prepStmt.setBlob(20, photoStream);
            prepStmt.setBlob(21, signatureInputStream);
            prepStmt.setInt(22, telephone);
            prepStmt.setString(23, mobile);
            prepStmt.setString(24, email);

            prepStmt.setString(25, website);
            prepStmt.setString(26, emergency);
            prepStmt.setString(27, emergencyContact);
            prepStmt.setInt(28, er);
            prepStmt.setInt(29, mf);

            prepStmt.setInt(30, calib);
            prepStmt.setInt(31, spareParts);
            prepStmt.setInt(32, 0);
            prepStmt.setInt(33, 0);

            prepStmt.setString(34, company);
            prepStmt.setInt(35, Utils.getNextIdNumber(alpha_industry[0], conn)); //  id_num

            prepStmt.setDouble(36, lat);
            prepStmt.setDouble(37, lng);
            prepStmt.setInt(38, 0);    //  <- is_transferred

            prepStmt.execute();

            Utils.logAdd(conn, onlineUser, false);
            Utils.updateIndustryTable(alpha_industry[0], conn);
            Utils.printSuccessJson(resJson, "Successfully added contacts", out);

            filePart.getInputStream();
            signatureInputStream.close();
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
            Utils.closeDBResource(connCRM, prepStmt, resultSet);
            out.close();
        }
    }

    private int getContactCount(String lastname, String firstname, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT (*) AS contactCount FROM contacts WHERE lastname = ? AND firstname = ?");

        prepStmt.setString(1, lastname);
        prepStmt.setString(2, firstname);
        ResultSet resultSet = prepStmt.executeQuery();

        int contactCount = 0;
        if (resultSet.next()) {
            contactCount = resultSet.getInt("contactCount");
        }

        prepStmt.close();
        resultSet.close();

        return contactCount;
    }

    private int getContactCountCRM(String lastname, String firstname, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT (*) AS contactCount FROM contacts WHERE lname = ? AND fname = ?");

        String lastname2 = Utils.omitSpaces(lastname);
        String firstname2 = Utils.omitSpaces(firstname);

        prepStmt.setString(1, lastname2);
        prepStmt.setString(2, firstname2);
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

    public void checkParameters(String lastname, String firstname, String birthDate, String mi, String mobile,
                                String company, String jobPosition, PrintWriter out) {
        try {
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
                return;
            } else if (mobile.isEmpty()) {
                Utils.logError("\"mobile\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Mobile is required.", out);
                return;
            }

            if (company == null) {
                Utils.logError("\"company\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Company is required.", out);
                return;
            } else if (company.isEmpty()) {
                Utils.logError("\"company\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Company is required.", out);
                return;
            }

            if (jobPosition == null) {
                Utils.logError("\"jobPosition\" parameter is null.");
                Utils.printJsonException(new JSONObject(), "Job position is required.", out);
            } else if (jobPosition.isEmpty()) {
                Utils.logError("\"jobPosition\" parameter is empty.");
                Utils.printJsonException(new JSONObject(), "Job position is required.", out);
            }
        } catch (IOException ie) {
            System.err.println(ie.toString());
        }
    }
}