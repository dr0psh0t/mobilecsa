package wmdc.mobilecsa.utils;

import org.apache.commons.codec.binary.Base64;

import org.json.JSONObject;
import wmdc.mobilecsa.exceptions.NonExistingException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String GET_JSON_DATA_PACKAGE = "wmdc.mobilecsa.servlet.getJsonData";
    public static final String JOBORDER_PACKAGE = "wmdc.mobilecsa.servlet.joborder";
    public static final String SERVLET_PACKAGE = "wmdc.mobilecsa.servlet";

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final int REQUIRED_IMAGE_BYTES = 512_000;

    public static boolean validEmail(String email) {
        return email.matches(EMAIL_PATTERN);
    }

    public static boolean validURL(String url) {
        Pattern pattern = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    public static String removeDash(String str) {
        return str.replaceAll("[\\-+]", "");
    }

    public static String replaceMultipleWhitespace(String str) {
        str = str.trim();
        str = str.replaceAll("[\\s+]{2,}", " ");
        return str;
    }

    public static String omitSpaces(String str) {
        return replaceMultipleWhitespace(str);
    }

    public static String filterSpecialChars(String str) {
        return getFiltered(str);
    }

    public static String getFiltered(String str) {
        str = str.replaceAll("[^A-Za-z0-9\\s+]", "");
        str = str.trim();
        str = str.replaceAll("[\\s+]{2,}", " ");
        return str;
    }

    public static BufferedImage resize(BufferedImage originalImage, int scaledWidth, int scaledHeight)
            throws IOException {
        BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, originalImage.getType());

        Graphics2D graphics2D = outputImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        graphics2D.dispose();

        return outputImage;
    }

    public static String eraseLeadingZeros(String number) {
        if (number.equals("0")) {
            return "";
        } else {
            return number;
        }
    }

    public static String addPhoneDash(int phone, int countryCode, int areaCode) {
        String strPhone = ""+phone;

        if (strPhone.equals("0")) {
            return "";
        } else {
            if (strPhone.length() < 4) {
                return "(" + countryCode + areaCode + ") " + strPhone;
            } else {
                StringBuilder stringBuilder = new StringBuilder(strPhone);
                stringBuilder.insert(3, '-');
                return "(" + countryCode + areaCode + ") " + stringBuilder.toString();
            }
        }
    }

    public static void databaseForName(ServletContext servletContext)
            throws ClassNotFoundException, IOException {
        Class.forName(getPropertyValue("forname", servletContext));
    }

    public static Connection getConnectionFromCRM(ServletContext servletContext)
            throws SQLException, IOException, NullPointerException {
        String url = getUrlFromConfig(servletContext);

        if (url == null) {
            throw new NullPointerException("URL database is null");
        } else {
                return DriverManager.getConnection(url, getPropertyValue("user", servletContext),
                        getPropertyValue("password", servletContext));
        }
    }

    public static Connection getConnection(ServletContext servletContext) throws SQLException, IOException {
        return DriverManager.getConnection(getPropertyValue("url", servletContext),
                getPropertyValue("user", servletContext), getPropertyValue("password", servletContext));
    }

    public static String getPropertyValue(String key, ServletContext servletContext) throws IOException {
        Properties prop = new Properties();
        prop.load(servletContext.getResourceAsStream("/WEB-INF/mobilecsa.properties"));
        return prop.getProperty(key);
    }

    public static String getAPIKey(ServletContext servletContext) throws IOException {
        return getPropertyValue("akey", servletContext);
    }

    public static String getServerAddress(ServletContext servletContext) throws IOException {
        return getPropertyValue("serverAddress", servletContext);
    }

    private static String getUrlFromConfig(ServletContext servletContext) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;
        ArrayList<String> result = new ArrayList<>(3);

        try {
            databaseForName(servletContext);
            conn = getConnection(servletContext);
            prepStmt = conn.prepareStatement("SELECT value FROM configs");
            resultSet = prepStmt.executeQuery();

            while (resultSet.next()) {
                result.add(resultSet.getString("value"));
            }
            return "jdbc:mysql://" + result.get(1) + ":" + result.get(2) + "/" + result.get(0);
        } catch (ClassNotFoundException | SQLException sqe) {
            displayStackTraceArray(sqe.getStackTrace(), "wmdc.mobilecsa.utils", "DBException", sqe.toString(), servletContext);
            return null;
        } finally {
            closeDBResource(conn, prepStmt, resultSet, servletContext);
        }
    }

    public static String getCountryNameInCRM(int countryId, Connection conn) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT country FROM country WHERE country_id = ?");
        prepStmt.setInt(1, countryId);
        ResultSet resultSet = prepStmt.executeQuery();

        String countryName = "";
        if (resultSet.next()) {
            countryName = resultSet.getString("country");
        }

        prepStmt.close();
        resultSet.close();

        if (countryName.isEmpty()) {
            return "Null";
        } else {
            return countryName;
        }
    }

    public static void updateIndustryTable(String idAlpha, Connection conn) throws Exception {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT next_id_num FROM industry WHERE id1 = ?");
        prepStmt.setString(1, idAlpha);
        ResultSet resultSet = prepStmt.executeQuery();

        int nextId = 0;

        if (resultSet.next()) {
            nextId = resultSet.getInt("next_id_num");
        }

        if (nextId < 1) {
            throw new Exception("Next id of an industry is zero.");
        }

        prepStmt = conn.prepareStatement("UPDATE industry SET next_id_num = ? WHERE id1 = ?");
        prepStmt.setInt(1, ++nextId);
        prepStmt.setString(2, idAlpha);
        prepStmt.executeUpdate();
        prepStmt.close();
        resultSet.close();
    }

    public static void illegalRequest(HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        JSONObject responseJson = new JSONObject();
        responseJson.put("success", false);
        responseJson.put("reason", "Illegal Request.");
        response.getWriter().println(responseJson);
    }

    public static int getNextIdNumber(String idAlpha, Connection conn) throws Exception {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT next_id_num FROM industry WHERE id1 = ?");
        prepStmt.setString(1, idAlpha);
        ResultSet resultSet = prepStmt.executeQuery();

        int idNum = 0;
        if (resultSet.next()) {
            idNum = resultSet.getInt("next_id_num");
        }

        prepStmt.close();
        resultSet.close();

        if (idNum < 1) {
            throw new Exception("Next id number should not be 0.");
        } else {
            return idNum;
        }
    }

    public static int getPlantIdByNameInCRM(String plantAssociated, Connection conn) throws Exception {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS plantCount FROM plant WHERE plant_associated = ?");
        prepStmt.setString(1, plantAssociated);
        ResultSet resultSet = prepStmt.executeQuery();

        int plantCount = 0;
        if (resultSet.next()) {
            plantCount = resultSet.getInt("plantCount");
        }

        if (plantCount < 1) {
            prepStmt.close();
            resultSet.close();
            throw new Exception("No plant associated found by name");
        } else {
            prepStmt = conn.prepareStatement("SELECT plant_id FROM plant WHERE plant_associated = ?");
            prepStmt.setString(1, plantAssociated);
            resultSet = prepStmt.executeQuery();

            int plantId = 0;
            if (resultSet.next()) {
                plantId = resultSet.getInt("plant_id");
            }

            prepStmt.close();
            resultSet.close();

            if (plantId < 1) {
                throw new Exception("No plant id found");
            }
            else {
                return plantId;
            }
        }
    }

    public static int getIndustryIdByName(String industry, Connection conn) throws Exception {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS industryCount FROM industry WHERE industry = ?");
        prepStmt.setString(1, industry);
        ResultSet resultSet = prepStmt.executeQuery();

        int industryCount = 0;
        if (resultSet.next()) {
            industryCount = resultSet.getInt("industryCount");
        }

        if (industryCount < 1) {
            prepStmt.close();
            resultSet.close();
            throw new Exception("No industry found with the supplied name");
        } else {
            prepStmt = conn.prepareStatement("SELECT industry_id FROM industry WHERE industry = ?");
            prepStmt.setString(1, industry);
            resultSet = prepStmt.executeQuery();

            int industryId = 0;
            if (resultSet.next()) {
                industryId = resultSet.getInt("industry_id");
            }

            prepStmt.close();
            resultSet.close();

            if (industryId < 1) {
                throw new Exception("No industry found with the supplied name");
            } else {
                return industryId;
            }
        }
    }

    public static int getIndustryIdByAlpha(String alpha, Connection conn) throws SQLException, NonExistingException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT industry_id FROM industry WHERE id1 = ?");
        prepStmt.setString(1, alpha);
        ResultSet resultSet = prepStmt.executeQuery();

        int industryId = 0;
        if (resultSet.next()) {
            industryId = resultSet.getInt("industry_id");
        }

        prepStmt.close();
        resultSet.close();

        if (industryId < 1) {
            throw new NonExistingException("No industry id found");
        } else {
            return industryId;
        }
    }

    public static String getIndustryName(int industryId, Connection conn) throws SQLException, NonExistingException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT industry FROM industry WHERE industry_id = ?");
        prepStmt.setInt(1, industryId);
        ResultSet resultSet = prepStmt.executeQuery();

        String industryName = "";

        if (resultSet.next()) {
            industryName = resultSet.getString("industry");
        }

        prepStmt.close();
        resultSet.close();

        if (industryName.isEmpty()) {
            throw new NonExistingException("Industry id did not exist. id=" + industryId);
        } else {
            return industryName;
        }
    }

    public static String getProvinceNameInCRM(int provinceId, Connection conn)
            throws SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT province FROM province WHERE province_id = ?");
        prepStmt.setInt(1, provinceId);
        ResultSet resultSet = prepStmt.executeQuery();

        String provinceName = "";

        if (resultSet.next()) {
            provinceName = resultSet.getString("province");
        }

        prepStmt.close();
        resultSet.close();

        if (provinceName.isEmpty()) {
            throw new NonExistingException("Province id did not exist.");
        } else {
            return provinceName;
        }
    }

    public static String getCityNameInCRM(int cityId, Connection conn) throws SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT city FROM city WHERE city_id = ?");
        prepStmt.setInt(1, cityId);
        ResultSet resultSet = prepStmt.executeQuery();

        String cityName = "";

        if (resultSet.next()) {
            cityName = resultSet.getString("city");
        }

        prepStmt.close();
        resultSet.close();

        if (cityName.isEmpty()) {
            throw new NonExistingException("City id did not exist.");
        } else {
            return cityName;
        }
    }

    public static String getCSAName(int csaId, Connection conn) throws SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT username FROM users WHERE csa_id = ?");
        prepStmt.setInt(1, csaId);
        ResultSet resultSet = prepStmt.executeQuery();

        String csaName = "";

        if (resultSet.next()) {
            csaName = resultSet.getString("username");
        }

        prepStmt.close();
        resultSet.close();

        if (csaName.isEmpty()) {
            throw new NonExistingException("CSA id did not exist.");
        } else {
            return csaName;
        }
    }

    public static String getPlantAssocInCRM(int plantId, Connection conn) throws SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT plant_associated FROM plant WHERE plant_id = ?");
        prepStmt.setInt(1, plantId);
        ResultSet resultSet = prepStmt.executeQuery();

        String plantAssociation = "";

        if (resultSet.next()) {
            plantAssociation = resultSet.getString("plant_associated");
        }

        prepStmt.close();
        resultSet.close();

        if (plantAssociation.isEmpty()) {
            throw new NonExistingException("Plant id did not exist.");
        } else {
            return plantAssociation;
        }
    }

    public static Date getDate(String dateStr, ServletContext ctx) throws ParseException {
        if (dateStr == null) {
            return null;
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            java.util.Date date = dateFormat.parse(dateStr);
            return new java.sql.Date(date.getTime());
        } catch (ParseException pe) {
            displayStackTraceArray(pe.getStackTrace(), "wmdc.mobilecsa.utils", "ParseException", pe.toString(), ctx);
            return null;
        }
    }

    public static void logLogin(Connection connection, String username, boolean isAdmin)
            throws ClassNotFoundException, SQLException, NonExistingException {

        String countQuery = "SELECT COUNT(*) AS userCount FROM users WHERE username = ?";
        String idQuery = "SELECT csa_id FROM users WHERE username = ?";
        String idType = "csa_id";

        if (isAdmin) {
            countQuery = "SELECT COUNT(*) as userCount FROM admins WHERE username = ?";
            idQuery = "SELECT admin_id FROM admins WHERE username = ?";
            idType = "admin_id";
        }

        int userCount = 0;

        PreparedStatement preparedStatement = connection.prepareStatement(countQuery);
        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            userCount = resultSet.getInt("userCount");
        }

        int userId = -1;

        preparedStatement = connection.prepareStatement(idQuery);
        preparedStatement.setString(1, username);
        resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            userId = resultSet.getInt(idType);
        }

        if (userCount < 1 || userId == -1) {
            preparedStatement.close();
            resultSet.close();
            throw new NonExistingException("Username does not exist..");
        } else {
            preparedStatement = connection.prepareStatement("INSERT INTO logs (log_date_time, user_id, table_name, " +
                    "event) VALUES (NOW(), ?, ?, ?)");
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, (isAdmin) ? "admins" : "user");
            preparedStatement.setString(3, "LOGIN");
            preparedStatement.executeUpdate();
            preparedStatement.close();
            resultSet.close();
        }
    }

    public static void logEdit(Connection conn, String editStatement, String oldData, String username,
                               boolean isCustomer) throws SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT admin_id FROM admins WHERE username = ?");
        prepStmt.setString(1, username);
        ResultSet resultSet = prepStmt.executeQuery();

        int userId = Integer.MAX_VALUE;
        if (resultSet.next()) {
            userId = resultSet.getInt("admin_id");
        }

        if (userId == Integer.MAX_VALUE) {
            prepStmt.close();
            resultSet.close();
            throw new NonExistingException("User id not found");
        } else {
            prepStmt = conn.prepareStatement("INSERT INTO logs (log_date_time, user_id, table_name, event, " +
                    "statement, old_data) VALUES (NOW(), ?, ?, ?, ?, ?)");
            prepStmt.setInt(1, userId);
            prepStmt.setString(2, (isCustomer) ? "customer" : "contacts");
            prepStmt.setString(3, "EDIT");
            prepStmt.setString(4, editStatement);
            prepStmt.setString(5, oldData);
            prepStmt.executeUpdate();
            prepStmt.close();
            resultSet.close();
        }
    }

    public static void logTransfer(Connection conn, String username, boolean isCustomer)
            throws SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS userCount FROM admins WHERE username = ?");
        prepStmt.setString(1, username);
        ResultSet resultSet = prepStmt.executeQuery();

        int userCount = 0;

        if (resultSet.next()) {
            userCount = resultSet.getInt("userCount");
        }

        int userId = Integer.MAX_VALUE;

        prepStmt = conn.prepareStatement("SELECT admin_id FROM admins WHERE username = ?");
        prepStmt.setString(1, username);
        resultSet = prepStmt.executeQuery();

        if (resultSet.next()) {
            userId = resultSet.getInt("admin_id");
        }

        if (userCount < 1 || userId == Integer.MAX_VALUE) {
            prepStmt.close();
            resultSet.close();
            throw new NonExistingException("Username does not exist.");
        } else {
            prepStmt = conn.prepareStatement("INSERT INTO logs (log_date_time, user_id, table_name, event) " +
                    "VALUES (NOW(), ?, ?, ?)");
            prepStmt.setInt(1, userId);
            prepStmt.setString(2, (isCustomer) ? "customer_trans" : "contacts_trans");
            prepStmt.setString(3, "TRANSFER");
            prepStmt.executeUpdate();
            prepStmt.close();
            resultSet.close();
        }
    }

    public static void logDelete(Connection conn, String deleteStatement, String username, boolean isCustomer)
            throws ClassNotFoundException, SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT COUNT(*) AS userCount FROM admins WHERE username = ?");
        prepStmt.setString(1, username);
        ResultSet resultSet = prepStmt.executeQuery();

        int userCount = 0;
        if (resultSet.next()) {
            userCount = resultSet.getInt("userCount");
        }

        int userId = Integer.MAX_VALUE;

        prepStmt = conn.prepareStatement("SELECT admin_id FROM admins WHERE username = ?");
        prepStmt.setString(1, username);
        resultSet = prepStmt.executeQuery();

        if (resultSet.next()) {
            userId = resultSet.getInt("admin_id");
        }

        if (userCount < 1 || userId == Integer.MAX_VALUE) {
            prepStmt.close();
            resultSet.close();
            throw new NonExistingException("Username does not exist.");
        } else {
            prepStmt = conn.prepareStatement("INSERT INTO logs (log_date_time, user_id, table_name, event, " +
                    "statement) VALUES (NOW(), ?, ?, ?, ?)");
            prepStmt.setInt(1, userId);
            prepStmt.setString(2, (isCustomer) ? "customer" : "contacts");
            prepStmt.setString(3, "DELETE");
            prepStmt.setString(4, deleteStatement);
            prepStmt.executeUpdate();
            prepStmt.close();
            resultSet.close();
        }
    }

    public static void logAdd(Connection conn, String username, boolean isCustomer)
            throws ClassNotFoundException, SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS userCount FROM users WHERE username = ?");
        prepStmt.setString(1, username);
        ResultSet resultSet = prepStmt.executeQuery();

        int userCount = 0;
        if (resultSet.next()) {
            userCount = resultSet.getInt("userCount");
        }

        int csaId = -1;

        prepStmt = conn.prepareStatement("SELECT csa_id FROM users WHERE username = ?");
        prepStmt.setString(1, username);
        resultSet = prepStmt.executeQuery();

        if (resultSet.next()) {
            csaId = resultSet.getInt("csa_id");
        }

        if (userCount < 1 || csaId == -1) {
            prepStmt.close();
            resultSet.close();
            throw new NonExistingException("Username does not exist.");
        } else {
            prepStmt = conn.prepareStatement("INSERT INTO logs (log_date_time, user_id, table_name, event) " +
                    "VALUES (NOW(), ?, ?, ?)");
            prepStmt.setInt(1, csaId);
            prepStmt.setString(2, (isCustomer) ? "customer" : "contacts");
            prepStmt.setString(3, "ADD");
            prepStmt.executeUpdate();
            prepStmt.close();
            resultSet.close();
        }
    }

    public static boolean isAdmin() {return true;}

    public static void redirectToLogin(HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }

    public static void redirectIfNull(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        boolean online = true;

        try {
            if(request.getSession(false).getAttribute("username") == null) {
                online = false;
            }
        } catch (NullPointerException npe) {
            online = false;
        } finally {
            if (!online) {
                response.sendRedirect("/index.jsp");
            }
        }
    }

    public static boolean isOnline(HttpServletRequest request, ServletContext ctx)
            throws ServletException, IOException {
        try {
            if (request.getSession().getAttribute("username") == null) {
                return false;
            }
        } catch (NullPointerException npe) {
            displayStackTraceArray(npe.getStackTrace(), "wmdc.mobilecsa.utils", "NullPointerException",
                    npe.toString(), ctx);
            return false;
        }
        return true;
    }

    public static String[] getIndustry(int industryId, Connection conn) throws SQLException, NonExistingException {

        String[] industries = new String[3];

        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS industryCount FROM industry WHERE industry_id = ?");
        prepStmt.setInt(1, industryId);
        ResultSet resultSet = prepStmt.executeQuery();

        int industryCount = 0;
        if (resultSet.next()) {
            industryCount = resultSet.getInt("industryCount");
        }

        if (industryCount < 1) {
            prepStmt.close();
            resultSet.close();
            throw new NonExistingException("Industry did not exist");
        } else {
            prepStmt = conn.prepareStatement("SELECT id1, id2, industry FROM industry WHERE industry_id = ?");
            prepStmt.setInt(1, industryId);
            resultSet = prepStmt.executeQuery();

            if (resultSet.next()) {
                industries[0] = resultSet.getString("id1");
                industries[1] = resultSet.getString("industry");
                industries[2] = resultSet.getString("id2");
            }

            prepStmt.close();
            resultSet.close();

            return industries;
        }
    }

    public static String getPlantByIdInCRM(int plantId, Connection conn) throws SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS plantCount FROM plant WHERE plant_id = ?");
        prepStmt.setInt(1, plantId);
        ResultSet resultSet = prepStmt.executeQuery();

        int plantCount = 0;
        if (resultSet.next()) {
            plantCount = resultSet.getInt("plantCount");
        }

        if (plantCount < 1) {
            throw new NonExistingException("Unable to find plant with associated id");
        } else {
            prepStmt = conn.prepareStatement("SELECT plant_associated FROM plant WHERE plant_id = ?");
            prepStmt.setInt(1, plantId);
            resultSet = prepStmt.executeQuery();

            String plantAssociated = "";

            if (resultSet.next()) {
                plantAssociated = resultSet.getString("plant_associated");
            }

            if (plantAssociated.isEmpty()) {
                throw new NonExistingException("Unable to find plant with associated id");
            } else {
                return plantAssociated;
            }
        }
    }

    public static String getCsaNameById(int csaId, Connection conn) throws SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT COUNT(*) AS csaCount FROM users WHERE csa_id = ?");
        prepStmt.setInt(1, csaId);
        ResultSet resultSet = prepStmt.executeQuery();

        int csaCount = 0;
        if (resultSet.next()) {
            csaCount = resultSet.getInt("csaCount");
        }

        if (csaCount < 1) {
            prepStmt.close();
            resultSet.close();
            throw new NonExistingException("No salesman found with that Id");
        } else {
            prepStmt = conn.prepareStatement("SELECT firstname, lastname FROM users WHERE csa_id = ?");
            prepStmt.setInt(1, csaId);
            resultSet = prepStmt.executeQuery();

            String fullname = "";
            if (resultSet.next()) {
                fullname = resultSet.getString("firstname") + " " + resultSet.getString("lastname");
            }

            prepStmt.close();
            resultSet.close();

            return fullname;
        }
    }

    public static int getYear(String date) {
        if (date == null) {
            return 0;
        } else {
            return Integer.parseInt(date.split("[\\-]")[0]);
        }
    }

    public static int getMonth(String date) {
        if (date == null) {
            return 0;
        } else {
            return Integer.parseInt(date.split("[\\-]")[1]);
        }
    }

    public static int getDay(String date) {
        if (date == null) {
            return 0;
        } else {
            return Integer.parseInt(date.split("[\\-]")[2]);
        }
    }

    public static boolean inspectNullRef(ArrayList<Object> requestParams) {
        for (Object obj : requestParams) {
            if (obj == null) {
                return true;
            }
        }
        return false;
    }

    public static String getAdministratorNameById(Connection conn, int adminId)
            throws SQLException, NonExistingException {

        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS adminCount FROM admins WHERE admin_id = ?");
        prepStmt.setInt(1, adminId);
        ResultSet resultSet = prepStmt.executeQuery();

        int adminCount = 0;
        if (resultSet.next()) {
            adminCount = resultSet.getInt("adminCount");
        }

        if (adminCount < 1) {
            prepStmt.close();
            resultSet.close();
            throw new NonExistingException("Unable to find administrator id");
        } else {
            prepStmt = conn.prepareStatement("SELECT username FROM admins WHERE admin_id = ?");
            prepStmt.setInt(1, adminId);
            resultSet = prepStmt.executeQuery();

            String user = "";
            if (resultSet.next()) {
                user = resultSet.getString("username");
            }

            prepStmt.close();
            resultSet.close();

            return user;
        }
    }

    public static int getMaxIdInCustomers(String idAlpha, Connection conn) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT id_alpha, MAX(id_num) FROM customers WHERE id_alpha = ? GROUP BY id_alpha");
        prepStmt.setString(1, idAlpha);
        ResultSet resultSet = prepStmt.executeQuery();

        int maxId = 0;
        if (resultSet.next()) {
            maxId = resultSet.getInt("MAX(id_num)");
        }

        prepStmt.close();
        resultSet.close();

        return maxId;
    }

    public static void printSuccessJson(JSONObject responseJson, String message, PrintWriter out) {
        responseJson.put("success", true);
        responseJson.put("reason", message);
        out.println(responseJson);
    }

    public static void printJsonException(JSONObject responseJson, String message, PrintWriter out) {
        responseJson.put("success", false);
        responseJson.put("reason", message);
        out.println(responseJson);
    }

    public static void closeDBResource(Connection conn, PreparedStatement prepStmt, ResultSet resultSet,
                                       ServletContext ctx) {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logError(e.toString(), ctx);
        }

        try {
            if (prepStmt != null) {
                prepStmt.close();
            }
        } catch (SQLException e) {
            logError(e.toString(), ctx);
        }

        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            logError(e.toString(), ctx);
        }
    }

    public static InputStream getSignatureInputStream(String signature, ServletContext ctx) throws Exception {
        try {
            String base64Signature = signature.split(",")[1];
            byte[] imageBytes = Base64.decodeBase64(base64Signature.getBytes());

            BufferedImage signatureImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            ImageIO.write(signatureImage, "png", os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            displayStackTraceArray(e.getStackTrace(), "wmdc.mobilecsa.utils", "IOException", e.toString(), ctx);
            throw e;
        }
    }

    public static String logError(String stackErrMsg, ServletContext servletContext, String packageRoot) {

        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            databaseForName(servletContext);
            conn = getConnection(servletContext);

            prepStmt = conn.prepareStatement("INSERT INTO error_logs (error_message) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);

            prepStmt.setString(1, stackErrMsg);
            prepStmt.execute();

            return "Error id "+getRecentId(prepStmt);
        } catch (ClassNotFoundException | SQLException sqe) {
            String errorMessage = sqe.toString() + " >> " + getRelevantTrace(sqe.getStackTrace(), packageRoot);
            logError("db_exception: "+errorMessage, servletContext);
            return errorMessage;
        } catch (IOException ioe) {
            String errorMessage = ioe.toString() + " >> " +getRelevantTrace(ioe.getStackTrace(), packageRoot);
            logError("io_exception: "+errorMessage, servletContext);
            return errorMessage;
        } finally {
            closeDBResource(conn, prepStmt, null, servletContext);
        }
    }

    public static int getRecentId(PreparedStatement prepStmt) throws SQLException {
        ResultSet recentIdRS = prepStmt.getGeneratedKeys();
        int recentId = 0;

        if (recentIdRS.next()) {
            recentId = recentIdRS.getInt(1);
        }

        return recentId;
    }

    public static int getCountryCodeById(int id, Connection conn) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT country_code FROM country_code WHERE country_code_id = ?");
        prepStmt.setInt(1, id);
        ResultSet resultSet = prepStmt.executeQuery();

        int countryCode = 0;

        if (resultSet.next()) {
            countryCode = resultSet.getInt("country_code");
        }

        prepStmt.close();
        resultSet.close();

        return countryCode;
    }

    public static InputStream reduceImage(InputStream bigInputStream) throws Exception {

        int sizeThreshold = REQUIRED_IMAGE_BYTES - 100_000;

        float quality = 1.0f;

        BufferedImage bufferedImage = ImageIO.read(bigInputStream);
        File file = new File("image.jpg");

        ImageIO.write(bufferedImage, "jpg", file);

        long fileSize = file.length();

        if (fileSize <= sizeThreshold) {
            return new FileInputStream(file);
        }

        Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");

        ImageWriter writer = (ImageWriter)iter.next();

        ImageWriteParam iwp = writer.getDefaultWriteParam();

        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

        FileInputStream inputStream = new FileInputStream(file);

        BufferedImage originalImage = ImageIO.read(inputStream);
        IIOImage image = new IIOImage(originalImage, null, null);

        float percent = 0.1f;   // 10% of 1

        File fileOut2 = null;

        while (fileSize > sizeThreshold) {
            if (percent >= quality) {
                percent = percent * 0.1f;
            }

            quality -= percent;

            File fileOut = new File("pic.jpg");
            if (fileOut.exists()) {
                System.out.println("fileOut.delete()= "+fileOut.delete());
            }
            FileImageOutputStream output = new FileImageOutputStream(fileOut);

            writer.setOutput(output);

            iwp.setCompressionQuality(quality);

            writer.write(null, image, iwp);

            fileOut2 = new File("pic.jpg");
            long newFileSize = fileOut2.length();

            if (newFileSize == fileSize) {
                // cannot reduce more, return
                break;
            } else {
                fileSize = newFileSize;
            }

            output.close();
        }

        writer.dispose();

        return new FileInputStream(fileOut2);
    }

    public static String getJoborderFolderName(ServletContext servletContext) {
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(servletContext);
            conn = Utils.getConnection(servletContext);

            prepStmt = conn.prepareStatement("SELECT `value` FROM `configs` WHERE `key` = ?");
            prepStmt.setString(1, "joborder_folder");
            resultSet = prepStmt.executeQuery();

            String key = null;
            if (resultSet.next()) {
                key = resultSet.getString("value");
            }

            return key;
        } catch (ClassNotFoundException | SQLException sqe) {
            displayStackTraceArray(sqe.getStackTrace(), "wmdc.mobilecsa.utils",
                    "db_exception", sqe.toString(), servletContext);
            return null;
        } catch (Exception e) {
            displayStackTraceArray(e.getStackTrace(), "wmdc.mobilecsa.utils",
                    "exception", e.toString(), servletContext);
            return null;
        } finally {
            closeDBResource(conn, prepStmt, resultSet, servletContext);
        }
    }

    public static void checkParameterValue(String csaId, String faxCode, String city, String country, String zip,
                                           String areaCode, String industry, String plant, String er, String mf,
                                           String spareParts, String calib, String province, String signStatus,
                                           String address, String signature, Part filePart, PrintWriter out,
                                           ServletContext ctx) {

        if (csaId == null) {
            Utils.logError("\"csaId\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Missing data required. See logs or try again.", out);
            return;
        } else if (csaId.isEmpty()) {
            Utils.logError("\"csaId\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Missing data required. See logs or try again.", out);
            return;
        }

        if (faxCode == null) {
            Utils.logError("\"faxCode\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Fax code required.", out);
            return;
        } else if (faxCode.isEmpty()) {
            Utils.logError("\"faxCode\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Fax code required.", out);
            return;
        }

        if (city == null) {
            Utils.logError("\"city\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "City required.", out);
            return;
        } else if (city.isEmpty()) {
            Utils.logError("\"city\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "City required.", out);
            return;
        }

        if (country == null) {
            Utils.logError("\"country\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Country required.", out);
            return;
        } else if (country.isEmpty()) {
            Utils.logError("\"country\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Country required.", out);
            return;
        }

        if (zip == null) {
            Utils.logError("\"zip\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Zip required.", out);
            return;
        } else if (zip.isEmpty()) {
            Utils.logError("\"zip\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Zip required.", out);
            return;
        }

        if (areaCode == null) {
            Utils.logError("\"areaCode\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Area code required.", out);
            return;
        } else if (areaCode.isEmpty()) {
            Utils.logError("\"areaCode\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Area code required.", out);
            return;
        }

        if (industry == null) {
            Utils.logError("\"industry\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Industry required.", out);
            return;
        } else if (industry.isEmpty()) {
            Utils.logError("\"industry\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Industry required.", out);
            return;
        }

        if (plant == null) {
            Utils.logError("\"plant\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Plant required.", out);
            return;
        } else if (plant.isEmpty()) {
            Utils.logError("\"plant\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Plant required.", out);
            return;
        }

        if (er == null) {
            Utils.logError("\"er\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "ER required.", out);
            return;
        } else if (er.isEmpty()) {
            Utils.logError("\"er\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "ER required.", out);
            return;
        }

        if (mf == null) {
            Utils.logError("\"mf\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "MF required.", out);
            return;
        } else if (mf.isEmpty()) {
            Utils.logError("\"mf\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "MF required.", out);
            return;
        }

        if (spareParts == null) {
            Utils.logError("\"spareParts\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Spare parts required.", out);
            return;
        } else if (spareParts.isEmpty()) {
            Utils.logError("\"spareParts\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Spare parts required.", out);
            return;
        }

        if (calib == null) {
            Utils.logError("\"calib\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Calib required.", out);
            return;
        } else if (calib.isEmpty()) {
            Utils.logError("\"calib\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Calib required.", out);
            return;
        }

        if (province == null) {
            Utils.logError("\"province\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Province required.", out);
            return;
        } else if (province.isEmpty()) {
            Utils.logError("\"province\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Province required.", out);
            return;
        }

        if (signStatus == null) {
            Utils.logError("\"signStatus\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Signature required.", out);
            return;
        } else if (signStatus.isEmpty()) {
            Utils.logError("\"signStatus\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Signature required.", out);
            return;
        }

        if (address == null) {
            Utils.logError("\"address\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Address required.", out);
            return;
        } else if (address.isEmpty()) {
            Utils.logError("\"address\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Address required.", out);
            return;
        }

        if (signature == null) {
            Utils.logError("\"signature\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Signature required.", out);
            return;
        } else if (signature.isEmpty()) {
            Utils.logError("\"signature\" parameter is empty.", ctx);
            printJsonException(new JSONObject(), "Signature required.", out);
            return;
        }

        if (filePart == null) {
            Utils.logError("\"filePart\" parameter is null.", ctx);
            printJsonException(new JSONObject(), "Null parameter found. See logs.", out);
        } else if (filePart.getSize() < 1) {
            Utils.logError("\"filePart\" size is zero.", ctx);
            printJsonException(new JSONObject(), "Empty parameter found. See logs.", out);
        }
    }

    public static void displayStackTraceArray(StackTraceElement[] stackTraceElements, String packageRoot,
                                              String exceptionName, String toString, ServletContext ctx) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(exceptionName).append(": ").append(toString);

        for (StackTraceElement elem : stackTraceElements) {
            if (elem.toString().contains(packageRoot)) {
                stringBuilder.append(" Source: ").append(elem.toString()).append(" ");
            }
        }

        logError(stringBuilder.toString(), ctx);
    }

    private static String getRelevantTrace(StackTraceElement[] traceElements, String packageRoot) {
        StringBuilder stringBuilder = new StringBuilder();
        for (StackTraceElement elem : traceElements) {
            if (elem.toString().contains(packageRoot)) {
                stringBuilder.append(packageRoot).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public static void println(Object object) {
        System.out.println(object);
    }

    public static String getCorrectJson(String incorrectJsonStr) {
        int start = incorrectJsonStr.indexOf("success");
        int end = incorrectJsonStr.lastIndexOf("s:") + 1;

        String correctJson;

        correctJson = incorrectJsonStr.substring(0, start) + "\""
                + incorrectJsonStr.substring(start, end) + "\""
                + incorrectJsonStr.substring(end, incorrectJsonStr.length());

        return correctJson;
    }

    public static String getCorrectJsonReason(String incorrectJsonStr) {
        if (incorrectJsonStr.contains("reason")) {
            int start = incorrectJsonStr.indexOf("reason");
            int end = incorrectJsonStr.lastIndexOf("n:") + 1;

            String correctJson;

            correctJson = incorrectJsonStr.substring(0, start) + "\""
                    + incorrectJsonStr.substring(start, end) + "\""
                    + incorrectJsonStr.substring(end, incorrectJsonStr.length());

            return correctJson;
        } else {
            return incorrectJsonStr;
        }
    }

    public static boolean inspectAddress(int customerId, Connection conn) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT address_lat, address_long FROM customers " +
                "WHERE customer_id = ?");
        prepStmt.setInt(1, customerId);
        ResultSet resultSet = prepStmt.executeQuery();

        double lat = 0;
        double lng = 0;

        if (resultSet.next()) {
            lat = resultSet.getDouble("address_lat");
            lng = resultSet.getDouble("address_long");
        }

        prepStmt.close();
        resultSet.close();

        return (lat != 0 && lng != 0);
    }

    public static boolean inspectAddressContact(int contactId, Connection conn) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement("SELECT address_lat, address_long FROM contacts " +
                "WHERE contact_id = ?");
        prepStmt.setInt(1, contactId);
        ResultSet resultSet = prepStmt.executeQuery();

        double lat = 0;
        double lng = 0;

        if (resultSet.next()) {
            lat = resultSet.getDouble("address_lat");
            lng = resultSet.getDouble("address_long");
        }

        prepStmt.close();
        resultSet.close();

        return (lat != 0 && lng != 0);
    }

    public static void invalidImage(HttpServletResponse response, ServletContext sc)
            throws ServletException, IOException{

        response.setContentType("image/jpeg");
        InputStream is = sc.getResourceAsStream("includes/images/invalid_image.jpg");

        BufferedImage bi = ImageIO.read(is);
        OutputStream os = response.getOutputStream();
        ImageIO.write(bi, "jpg", os);
    }

    public static void logError(String text, ServletContext context) {
        context.log(text);
    }

    public static void logMsg(String text, ServletContext context) {
        context.log(text);
    }
}