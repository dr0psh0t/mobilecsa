package wmdc.mobilecsa.servlet;

import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
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

    private void procRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
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
                Utils.logError("\"customerId\" parameter is null", ctx);
                Utils.invalidImage(response, getServletContext());
                return;
            } else if (request.getParameter("customerId").isEmpty()) {
                Utils.logError("\"customerId\" parameter is empty", ctx);
                Utils.invalidImage(response, getServletContext());
                return;
            }

            int customerId = Integer.parseInt(request.getParameter("customerId"));
            prepStmt = conn.prepareStatement("SELECT COUNT(*) AS customerCount FROM customers WHERE customer_id = ?");
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
            Utils.invalidImage(response, getServletContext());
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException", sqe.toString(),
                    ctx, conn);

        } catch (Exception e) {
            Utils.invalidImage(response, getServletContext());
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(),
                    ctx, conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            if (!success) {
                Utils.invalidImage(response, getServletContext());
            }
        }
    }
}
