package wmdc.mobilecsa.servlet;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.json.simple.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * Created by wmdcprog on 2/17/2017.
 */
@WebServlet("/securitykey")
public class SecurityKey extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();
        HttpSession httpSession = request.getSession(false);

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        String secretKey = "";
        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            Object usernameObjectSession = httpSession.getAttribute("interimUsername");
            Object isAdminObjectSession = httpSession.getAttribute("isAdmin");
            String securityKeyStr = request.getParameter("securityKey");

            if (usernameObjectSession == null) {
                Utils.printJsonException(resJson, "Login first.", out);
                System.err.println("The session \"interimUsername\" is null");
                return;
            }

            if (isAdminObjectSession == null) {
                Utils.printJsonException(resJson, "Login first.", out);
                System.err.println("The session \"isAdmin\" is null");
                return;
            }

            if (securityKeyStr == null) {
                Utils.printJsonException(resJson, "Security key is required.", out);
                System.err.println("The parameter \"securityKey\" is null");
                return;
            }

            //  value of verified parameters
            String interimUsername = usernameObjectSession.toString();
            boolean isAdmin = Boolean.parseBoolean(isAdminObjectSession.toString());
            int securityKey = Integer.parseInt(securityKeyStr);
            int id;

            if (httpSession.getAttribute("interimUserId") != null) {
                id = Integer.parseInt(httpSession.getAttribute("interimUserId").toString());
            } else if (httpSession.getAttribute("interimAdminId") != null) {
                id = Integer.parseInt(httpSession.getAttribute("interimAdminId").toString());
            } else {
                Utils.printJsonException(resJson, "Security key attempt failed due to missing data.", out);
                System.err.println("The session \"interimUserId\" or \"interimAdminId\" is null");
                return;
            }

            if (isAdmin) {
                prepStmt = conn.prepareStatement("SELECT secret_key FROM admins WHERE admin_id = ?");
            } else {
                prepStmt = conn.prepareStatement("SELECT secret_key FROM users WHERE csa_id = ?");
            }

            prepStmt.setInt(1, id);
            resultSet = prepStmt.executeQuery();

            if (resultSet.next()) {
                secretKey = resultSet.getString("secret_key");
            }

            if(googleAuthenticator.authorize(secretKey, securityKey)) {
                Utils.logLogin(conn, interimUsername, isAdmin);

                httpSession.setAttribute("username", interimUsername);
                httpSession.setAttribute("csaId", id);
                httpSession.setAttribute("interimUsername", null);
                httpSession.setAttribute("interimUserId", null);
                httpSession.setAttribute("interimAdminId", null);
                httpSession.setAttribute("lockedUsername", null);

                resJson.put("success", true);
                resJson.put("isAdmin", isAdmin);
                resJson.put("csaId", id);
                resJson.put("username", interimUsername);
                resJson.put("reason", "Security key is valid");
                resJson.put("apiKey", Utils.getAPIKey(getServletContext()));
                resJson.put("localAddressNorth", Utils.getPropertyValue("localAddressNorth",
                        getServletContext()));
                resJson.put("publicAddressNorth", Utils.getPropertyValue("publicAddressNorth",
                        getServletContext()));

                if (!isAdmin) {
                    resJson.put("csaFullName", Utils.getCsaNameById(id, conn));
                }

                out.println(resJson);
            } else {
                Utils.printJsonException(resJson, "Invalid security key.", out);
            }

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.redirectToLogin(response);
    }
}