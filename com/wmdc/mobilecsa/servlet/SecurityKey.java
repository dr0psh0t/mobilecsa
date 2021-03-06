package wmdc.mobilecsa.servlet;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
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
        ServletContext ctx = getServletContext();
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
                Utils.logError("The session \"interimUsername\" is null", ctx);
                return;
            }

            if (isAdminObjectSession == null) {
                Utils.printJsonException(resJson, "Login first.", out);
                Utils.logError("The session \"isAdmin\" is null", ctx);
                return;
            }

            if (securityKeyStr == null) {
                Utils.printJsonException(resJson, "Security key is required.", out);
                Utils.logError("The parameter \"securityKey\" is null", ctx);
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
                Utils.logError("The session \"interimUserId\" or \"interimAdminId\" is null", ctx);
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

                httpSession.invalidate();
                httpSession = request.getSession();
                httpSession.setAttribute("username", interimUsername);
                httpSession.setAttribute("csaId", id);

                resJson.put("sessionId", httpSession.getId());
                resJson.put("success", true);
                resJson.put("isAdmin", isAdmin);
                resJson.put("csaId", id);
                resJson.put("username", interimUsername);
                resJson.put("reason", "Security key is valid");
                resJson.put("apiKey", Utils.getAPIKey(getServletContext()));
                resJson.put("localAddressNorth", Utils.getPropertyValue("localAddressNorth", getServletContext()));
                resJson.put("publicAddressNorth", Utils.getPropertyValue("publicAddressNorth", getServletContext()));
                resJson.put("serverAddress", Utils.getPropertyValue("serverAddress", getServletContext()));

                if (!isAdmin) {
                    resJson.put("csaFullName", Utils.getCsaNameById(id, conn));
                }

                out.println(resJson);

            } else {
                Utils.printJsonException(resJson, "Invalid security key.", out);
            }

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot verify security key at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot verify security key at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.redirectToLogin(response);
    }
}