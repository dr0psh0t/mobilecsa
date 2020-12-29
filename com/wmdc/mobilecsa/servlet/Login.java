package wmdc.mobilecsa.servlet;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.BCrypt;
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

/*** Created by wmdcprog on 2/14/2017.*/

@WebServlet("/Login")

public class Login extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();
        ServletContext ctx = getServletContext();

        HttpSession httpSession = request.getSession();

        Connection conn = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            if (username == null) {
                Utils.logError("\"username\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Username required.", out);
                return;
            } else if (username.isEmpty()) {
                Utils.logError("\"username\" parameter is empty", ctx);
                Utils.printJsonException(resJson, "Username required.", out);
                return;
            }

            if (password == null) {
                Utils.logError("\"password\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Password required.", out);
                return;
            } else if (password.isEmpty()) {
                Utils.logError("\"password\" parameter is empty", ctx);
                Utils.printJsonException(resJson, "Password required.", out);
                return;
            }

            if(username.matches(".*[a-zA-Z].*")) {
                if ((password.length() > 7 && password.length() < 33) && username.length() < 33) {

                    /*
                    * this is the backend checking of password. front-end checking of password is
                    * also implemented. this scans if the password contains capital, small and symbols.                *
                    * */
                    int upcaseLen = password.replaceAll("[^A-Z]", "").length();
                    int lowcaseLen = password.replaceAll("[^a-z]", "").length();
                    int specialCharsLen = password.replaceAll("[A-Za-z0-9\\s+]", "").length();

                    if (upcaseLen < 1 || lowcaseLen < 1 || specialCharsLen < 1) {
                        Utils.printJsonException(resJson,
                                "Password must contain capital and small letters and symbols.", out);
                        return;
                    }

                    int administratorId = getAdministratorId(username, conn);
                    if (administratorId > 0) {  //  user is an admin
                        String adminPass = getAdministratorPassword(conn, administratorId);

                        if (!adminPass.equals("")) {
                            if (BCrypt.checkpw(password, adminPass)) {
                                httpSession.setAttribute("interimUsername", username);
                                httpSession.setAttribute("interimAdminId", administratorId);
                                httpSession.setAttribute("isAdmin", true);

                                resJson.put("success", true);
                                resJson.put("isAdmin", true);
                                resJson.put("reason", "Ok");

                                out.println(resJson);
                            } else {
                                Utils.printJsonException(resJson, "Login credentials are incorrect.", out);
                            }
                        } else {
                            Utils.printJsonException(resJson, "Login credentials are incorrect.", out);
                        }
                    } else {    //  username is a user
                        int csaId = getCsaId(username, conn);
                        if (csaId > 0) {
                            String userPassword = getUserPassword(csaId, conn);
                            if (!userPassword.equals("")) {
                                if (BCrypt.checkpw(password, userPassword)) {
                                    httpSession.setAttribute("interimUsername", username);
                                    httpSession.setAttribute("interimUserId", csaId);

                                    resJson.put("csaId", csaId);
                                    resJson.put("csaUsername", username);
                                    resJson.put("success", true);
                                    resJson.put("isAdmin", false);
                                    resJson.put("reason", "Successfully logged in");
                                    resJson.put("isLocked", false);
                                    resJson.put("sessionId", httpSession.getId());

                                    httpSession.setAttribute("isAdmin", true);
                                    out.println(resJson);

                                    /*response.sendRedirect("/verifycode.jsp");
                                    RequestDispatcher dispatcher = getServletContext()
                                            .getRequestDispatcher("/verifycode.jsp");
                                    dispatcher.forward(request, response);*/

                                } else {
                                    Utils.printJsonException(resJson, "Login credentials incorrect.", out);
                                }
                            } else {
                                String oldUserPassword = getOldUserPassword(csaId, conn);
                                if (oldUserPassword.equals("")) {
                                    Utils.printJsonException(resJson, "Unable to find user.", out);
                                } else {
                                    if (BCrypt.checkpw(password, oldUserPassword)) {
                                        resJson.put("success", false);
                                        resJson.put("isLocked", true);
                                        resJson.put("csaId", csaId);
                                        resJson.put("reason", "Account is locked.");
                                        resJson.put("sessionId", httpSession.getId());
                                        resJson.put("interimUsername", username);
                                        resJson.put("lockedUsername", username);

                                        httpSession.setAttribute("interimUsername", username);
                                        httpSession.setAttribute("lockedUsername", username);

                                        out.println(resJson);

                                        /*response.sendRedirect("/verifycode.jsp");
                                        RequestDispatcher dispatcher = getServletContext()
                                                .getRequestDispatcher("/verifycode.jsp");
                                        dispatcher.forward(request, response);*/

                                    } else {
                                        Utils.printJsonException(resJson, "Login credentials incorrect.", out);
                                    }
                                }
                            }
                        } else {
                            Utils.printJsonException(resJson, "Login credentials incorrect.", out);
                        }

                        httpSession.setAttribute("isAdmin", false);
                    }
                } else {
                    Utils.printJsonException(resJson, "Password should be 8 - 32 characters in length.", out);
                }
            } else {
                Utils.printJsonException(resJson, "Invalid username format.\nPlease try again.", out);
            }

        } catch (ClassNotFoundException | SQLException sqe) {
            sqe.printStackTrace();
            Utils.printJsonException(resJson, "Cannot login at this time.", out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.printJsonException(resJson, "Cannot login at the moement.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, null, null, ctx);
            out.close();
        }
    }

    private String getAdministratorPassword(Connection connection, int adminId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT admin_password FROM admins WHERE admin_id = ?");

        preparedStatement.setInt(1, adminId);
        ResultSet resultSet = preparedStatement.executeQuery();

        String adminPass = "";
        if (resultSet.next()) {
            adminPass = resultSet.getString("admin_password");
        }

        preparedStatement.close();
        resultSet.close();

        return adminPass;
    }

    private int getAdministratorId(String username, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT admin_id FROM admins WHERE username = ?");

        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();

        int adminId = 0;
        if (resultSet.next()) {
            adminId = resultSet.getInt("admin_id");
        }

        preparedStatement.close();
        resultSet.close();

        return adminId;
    }

    private int getCsaId(String username, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT csa_id FROM users WHERE username = ?");

        preparedStatement.setString(1, username);
        ResultSet resultSet = preparedStatement.executeQuery();

        int csaId = 0;
        if (resultSet.next()) {
            csaId = resultSet.getInt("csa_id");
        }

        preparedStatement.close();
        resultSet.close();

        return csaId;
    }

    private String getUserPassword(int csaId, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT password FROM users WHERE csa_id = ?");

        preparedStatement.setInt(1, csaId);
        ResultSet resultSet = preparedStatement.executeQuery();

        String hashedPassword = "";
        if (resultSet.next()) {
            hashedPassword = resultSet.getString("password");
        }

        preparedStatement.close();
        resultSet.close();

        return hashedPassword;
    }

    private String getOldUserPassword(int csaId, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT old_password FROM users WHERE csa_id = ?");

        preparedStatement.setInt(1, csaId);
        ResultSet resultSet = preparedStatement.executeQuery();

        String oldPassword = "";
        if (resultSet.next()) {
            oldPassword = resultSet.getString("old_password");
        }

        preparedStatement.close();
        resultSet.close();

        return oldPassword;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }
}