package wmdc.mobilecsa.servlet.joborder;

import org.json.simple.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by wmdcprog on 7/11/2018.
 */
@WebServlet("/getallinitialjoborderlist")
public class GetAllInitialJoborderList extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT initial_joborder_id, customer_source, customer, date_stamp, " +
                    "serial_num, model_id, make, jo_number, is_added FROM initial_joborder " +
                    "ORDER BY YEAR(date_stamp) DESC, MONTH(date_stamp) DESC, DAY(date_stamp) DESC ");
            resultSet = prepStmt.executeQuery();

            ArrayList<JSONObject> initialJoborderList = new ArrayList<>();

            while (resultSet.next()) {
                resJson = new JSONObject();
                String dateAdded = resultSet.getString("date_stamp");

                resJson.put("dateAdded", getExcludedTime(dateAdded));
                resJson.put("initialJoborderId", resultSet.getInt("initial_joborder_id"));
                resJson.put("source", resultSet.getString("customer_source"));
                resJson.put("customer", resultSet.getString("customer"));
                resJson.put("serialNo", resultSet.getString("serial_num"));
                resJson.put("model", resultSet.getInt("model_id"));
                resJson.put("make", resultSet.getString("make"));
                resJson.put("joNumber", resultSet.getString("jo_number"));
                resJson.put("added", resultSet.getInt("is_added"));

                initialJoborderList.add(resJson);
            }

            resJson = new JSONObject();

            resJson.put("success", true);
            resJson.put("initialJoborderList", initialJoborderList);

            out.println(resJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
            out.close();
        }
    }

    public String getExcludedTime(String dateAdded) throws java.text.ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(dateAdded, formatter);

        int year = date.getYear();
        int day = date.getDayOfMonth();
        Month month = date.getMonth();
        int monthAsInt = month.getValue();

        return year+"-"+monthAsInt+"-"+day;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}