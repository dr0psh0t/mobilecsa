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

@WebServlet("/getcontactsphoto")

public class GetContactsPhoto extends HttpServlet {

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

            if (request.getParameter("contactId") == null) {
                Utils.logError("\"contactId\" parameter is null", ctx);
                Utils.invalidImage(response, getServletContext());
                return;
            } else if (request.getParameter("contactId").isEmpty()) {
                Utils.logError("\"contactId\" parameter is empty", ctx);
                Utils.invalidImage(response, getServletContext());
                return;
            }

            int contactId = Integer.parseInt(request.getParameter("contactId"));
            prepStmt = conn.prepareStatement("SELECT COUNT(*) AS contactCount FROM contacts WHERE contact_id = ?");
            prepStmt.setInt(1, contactId);
            resultSet = prepStmt.executeQuery();

            int contactCount = 0;
            if (resultSet.next()) {
                contactCount = resultSet.getInt("contactCount");
            }

            if (contactCount > 0) {
                prepStmt = conn.prepareStatement("SELECT specimen FROM contacts WHERE contact_id = ?");
                prepStmt.setInt(1, contactId);
                resultSet = prepStmt.executeQuery();

                if (resultSet.next()) {
                    ServletOutputStream out = response.getOutputStream();

                    image = resultSet.getBlob("specimen");

                    response.setContentType("image/jpeg");

                    InputStream in = image.getBinaryStream();

                    int length;
                    byte[] buffer = new byte[1024];

                    while ((length = in.read(buffer)) != -1) {
                        out.write(buffer, 0, length);
                    }

                    in.close();
                    out.close();

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
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx,
                    conn);

        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
            if (!success) {
                Utils.invalidImage(response, getServletContext());
            }
        }
    }
}