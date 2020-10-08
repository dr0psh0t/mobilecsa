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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wmdcprog on 9/20/2017.
 */
@WebServlet("/getadministratorbyparams")

public class GetAdministratorByParams extends HttpServlet {
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

        String adminIdParam = request.getParameter("adminId");
        if (adminIdParam == null) {
            Utils.logError("\"adminId\" parameter is null.", ctx);
            Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (adminIdParam.isEmpty()) {
            Utils.logError("\"adminId\" is empty.", ctx);
            Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int adminId = Integer.parseInt(request.getParameter("adminId"));

            if (getAdminCountById(conn, adminId) < 1) {
                Utils.logError("No admin found using adminId: "+adminId, ctx);
                Utils.printJsonException(responseJson, "No admin found.", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT admin_id, username, creator_id, date_stamp, secret_key " +
                    "FROM admins WHERE admin_id = ?");
            prepStmt.setInt(1, adminId);
            resultSet = prepStmt.executeQuery();

            JSONObject obj = new JSONObject();

            if (resultSet.next()) {
                obj.put("adminId", resultSet.getInt("admin_id"));
                obj.put("username", resultSet.getString("username"));
                obj.put("secretKey", resultSet.getString("secret_key"));
                obj.put("dateStamp", resultSet.getString("date_stamp"));
                obj.put("qrCodeLink", "getqrcodeadmin?adminId="+adminId);
                obj.put("creator", resultSet.getInt("creator_id") != 0 ?
                        Utils.getAdministratorNameById(conn, resultSet.getInt("creator_id")) : "Super Admin");
            }

            out.println(obj);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot get administrator at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot get administrator at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString(),
                    ctx, conn);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }

    private int getAdminCountById(Connection conn, int adminId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT(*) AS adminCount FROM admins WHERE admin_id = ?");
        prepStmt.setInt(1, adminId);

        ResultSet resultSet = prepStmt.executeQuery();
        int adminCount = 0;
        if (resultSet.next()) {
            adminCount = resultSet.getInt("adminCount");
        }

        prepStmt.close();
        resultSet.close();

        return adminCount;
    }
}