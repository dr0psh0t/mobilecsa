package wmdc.mobilecsa.servlet.joborder;

import org.json.JSONObject;
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

/**
 * Created by wmdcprog on 7/12/2018.
 */
@WebServlet("/getinitialjobordersignature")
public class GetInitialJoborderSignature extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        JSONObject resJson = new JSONObject();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(resJson, "Login to continue", response.getWriter());
            return;
        }

        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        String initialJoborderIdStr = request.getParameter("initialJoborderId");

        if (initialJoborderIdStr == null) {
            Utils.logError("\"initialJoborderId\" parameter is null");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.",
                    response.getWriter());
            return;
        } else if (initialJoborderIdStr.isEmpty()) {
            Utils.logError("\"initialJoborderId\" parameter is empty");
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.",
                    response.getWriter());
            return;
        }

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int initialJoborderId = Integer.parseInt(initialJoborderIdStr);

            prepStmt = conn.prepareStatement("SELECT COUNT (*) AS joCount FROM initial_joborder WHERE " +
                    "initial_joborder_id = ?");
            prepStmt.setInt(1, initialJoborderId);
            resultSet = prepStmt.executeQuery();

            int joCount = 0;
            if (resultSet.next()) {
                joCount = resultSet.getInt("joCount");
            }

            if (joCount < 1) {
                Utils.logError("No signature found using initial_joborder_id: "+initialJoborderId);
                Utils.printJsonException(resJson, "Cannot find signature with the associated id",
                        response.getWriter());
                return;
            }

            prepStmt = conn.prepareStatement("SELECT signature FROM initial_joborder WHERE initial_joborder_id = ?");
            prepStmt.setInt(1, initialJoborderId);
            resultSet = prepStmt.executeQuery();

            Blob signImage;
            if (resultSet.next()) {
                ServletOutputStream out = response.getOutputStream();
                signImage = resultSet.getBlob("signature");

                int length;
                byte[] buffer = new byte[1024];

                response.setContentType("image/jpeg");
                InputStream in = signImage.getBinaryStream();

                while ((length = in.read(buffer)) != -1) {
                    out.write(buffer, 0, length);
                }

                in.close();
                out.close();
            } else {
                Utils.printJsonException(resJson, "System cannot find signature", response.getWriter());
                return;
            }
        } catch (ClassNotFoundException | SQLException sqe) {
            response.setContentType("application/json");
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), sqe.toString(), response.getWriter());
        } catch (Exception e) {
            response.setContentType("application/json");
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", response.getWriter());
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
        }
    }
}
