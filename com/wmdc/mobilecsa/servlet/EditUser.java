package wmdc.mobilecsa.servlet;

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
 * Created by wmdcprog on 2/22/2017.
 */
@WebServlet("/edit")
public class EditUser extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();
        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;

        String lastname = request.getParameter("lastname");
        String firstname = request.getParameter("firstname");
        String csaIdStr = request.getParameter("csaId");

        if (lastname == null) {
            Utils.logError("\"lastname\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Lastname required.", out);
            return;
        } else if (lastname.isEmpty()) {
            Utils.logError("\"lastname\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Lastname required.", out);
            return;
        }

        if (firstname == null) {
            Utils.logError("\"firstname\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Firstname required.", out);
            return;
        } else if (firstname.isEmpty()) {
            Utils.logError("\"firstname\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Firstname required.", out);
            return;
        }

        if (csaIdStr == null) {
            Utils.logError("\"csaIdStr\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (csaIdStr.isEmpty()) {
            Utils.logError("\"csaIdStr\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int csaId = Integer.parseInt(csaIdStr);

            prepStmt = conn.prepareStatement("UPDATE users SET lastname = ?, firstname = ? WHERE csa_id = ?");
            prepStmt.setString(1, lastname);
            prepStmt.setString(2, firstname);
            prepStmt.setInt(3, csaId);
            prepStmt.execute();

            resJson.put("success", true);
            resJson.put("reason", "Successfully updated user");

            out.println(resJson);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(new JSONObject(), "Cannot edit user at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot edit user at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, null, ctx);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}