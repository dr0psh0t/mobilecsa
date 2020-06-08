package wmdc.mobilecsa.servlet.joborder;

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

/**
 * Created by wmdcprog on 5/3/2018.
 */
@WebServlet("/getinitialjoborderphoto")
public class GetInitialJoborderPhoto extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        ServletContext ctx = getServletContext();

        if (!Utils.isOnline(request, ctx)) {
            Utils.invalidImage(response, getServletContext());
            return;
        }

        String initialJoborderIdStr = request.getParameter("initialJoborderId");

        if (initialJoborderIdStr == null) {
            Utils.logError("\"initialJoborderId\" parameter is null", ctx);
            Utils.invalidImage(response, getServletContext());
            return;
        } else if (initialJoborderIdStr.isEmpty()) {
            Utils.logError("\"initialJoborderId\" parameter is empty", ctx);
            Utils.invalidImage(response, getServletContext());
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int initialJoborderId = Integer.parseInt(initialJoborderIdStr);

            prepStmt = conn.prepareStatement("SELECT COUNT(*) AS joCount FROM initial_joborder_image WHERE " +
                    "initial_joborder_id = ?");
            prepStmt.setInt(1, initialJoborderId);
            resultSet = prepStmt.executeQuery();

            int joCount = 0;
            if (resultSet.next()) {
                joCount = resultSet.getInt("joCount");
            }

            if (joCount < 1) {
                Utils.logError("No image found using initial_joborder_id: "+initialJoborderId, ctx);
                Utils.invalidImage(response, getServletContext());
            }

            prepStmt = conn.prepareStatement("SELECT image FROM initial_joborder_image WHERE initial_joborder_id = ?");
            prepStmt.setInt(1, initialJoborderId);
            resultSet = prepStmt.executeQuery();

            Blob image;
            if (resultSet.next()) {
                ServletOutputStream out = response.getOutputStream();
                image = resultSet.getBlob("image");

                response.setContentType("image/jpeg");
                InputStream in = image.getBinaryStream();

                int length;
                byte[] buffer = new byte[1024];

                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }

                in.close();
                out.close();
            } else {
                Utils.invalidImage(response, getServletContext());
            }
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.SERVLET_PACKAGE, "DBException",
                    sqe.toString(), ctx);
            Utils.invalidImage(response, getServletContext());
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.SERVLET_PACKAGE, "Exception", e.toString(), ctx);
            Utils.invalidImage(response, getServletContext());
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, ctx);
        }
    }
}
