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
import java.sql.*;

/**
 * Created by wmdcprog on 3/31/2017.
 */

@WebServlet("/getcsabyparams")

public class GetCsaByParams extends HttpServlet {
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

        String csaIdParam = request.getParameter("csaId");

        if (csaIdParam == null) {
            Utils.logError("\"csaId\" parameter is null", ctx);
            Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (csaIdParam.isEmpty()) {
            Utils.logError("\"csaId\" parameter is empty", ctx);
            Utils.printJsonException(responseJson, "Missing data required. See logs or try again.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int csaId = Integer.parseInt(csaIdParam);
            if (getCsaCountById(conn, csaId) < 1) {
                Utils.printJsonException(responseJson, "No CSA found.", out);
                return;
            }

            prepStmt = conn.prepareStatement("SELECT csa_id, username, lastname, firstname, secret_key FROM users " +
                    "WHERE csa_id = ?");
            prepStmt.setInt(1, csaId);
            resultSet = prepStmt.executeQuery();

            JSONObject obj = new JSONObject();

            if (resultSet.next()) {
                obj.put("csaId", resultSet.getInt("csa_id"));
                obj.put("username", resultSet.getString("username"));
                obj.put("lastname", resultSet.getString("username"));
                obj.put("firstname", resultSet.getString("username"));
                obj.put("secretKey", resultSet.getString("secret_key"));
                obj.put("qrCodeLink", "getqrcode?csaId="+csaId);
            }

            out.println(obj);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "DBException",
                    sqe.toString(), ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString(),
                    ctx, conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }

    private int getCsaCountById(Connection conn,int csaId) throws SQLException {
        PreparedStatement prepStmt = conn.prepareStatement("SELECT COUNT(*) AS userCount FROM users WHERE csa_id = ?");
        prepStmt.setInt(1, csaId);

        ResultSet resultSet = prepStmt.executeQuery();
        int userCount = 0;
        if (resultSet.next()) {
            userCount = resultSet.getInt("userCount");
        }

        prepStmt.close();
        resultSet.close();

        return userCount;
    }
}