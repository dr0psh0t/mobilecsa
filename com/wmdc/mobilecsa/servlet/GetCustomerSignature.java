package wmdc.mobilecsa.servlet;

import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

@WebServlet("/getcustomersignature")
public class GetCustomerSignature extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        procRequest(request, response);
    }

    protected void procRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        if (!Utils.isOnline(request)) {
            Utils.invalidImage(response, getServletContext());
            return;
        }

        Blob image;
        boolean success = false;

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            if (request.getParameter("customerId") == null) {
                System.err.println("\"customerId\" parameter is null");
                Utils.invalidImage(response, getServletContext());
                return;
            } else if (request.getParameter("customerId").isEmpty()) {
                System.err.println("\"customerId\" parameter is empty");
                Utils.invalidImage(response, getServletContext());
                return;
            }

            int customerId = Integer.parseInt(request.getParameter("customerId"));
            prepStmt = conn.prepareStatement("SELECT COUNT (*) AS customerCount FROM customers WHERE customer_id = ?");
            prepStmt.setInt(1, customerId);
            resultSet = prepStmt.executeQuery();

            int customerCount = 0;
            if (resultSet.next()) {
                customerCount = resultSet.getInt("customerCount");
            }

            if (customerCount > 0) {
                prepStmt = conn.prepareStatement("SELECT signature FROM customers WHERE customer_id = ?");
                prepStmt.setInt(1, customerId);
                resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    ServletOutputStream out = response.getOutputStream();

                    image = resultSet.getBlob("signature");

                    response.setContentType("image/png");

                    InputStream in = image.getBinaryStream();

                    int length;

                    byte[] buffer = new byte[1024];

                    while ((length = in.read(buffer)) != -1) {
                        out.write(buffer, 0, length);
                    }

                    in.close();
                    out.flush();

                    success = true;
                } else {
                    Utils.invalidImage(response, getServletContext());
                }
            } else {
                Utils.invalidImage(response, getServletContext());
            }
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString());
            Utils.invalidImage(response, getServletContext());
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString());
            Utils.invalidImage(response, getServletContext());
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
            if (!success) {
                Utils.invalidImage(response, getServletContext());
            }
        }
    }
}
