package wmdc.mobilecsa.servlet.joborder;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by wmdcprog on 5/7/2018.
 */
@WebServlet("/getmcsajoimage")
public class GetMCSAJOImage extends HttpServlet {
    private String getMcsaJoImage(ServletContext context) {
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT `value` FROM `configs` WHERE `key` = ?");
            prepStmt.setString(1, "getmcsajoimage_servlet");

            resultSet = prepStmt.executeQuery();

            String key = null;
            if (resultSet.next()) {
                key = resultSet.getString("value");
            }

            return key;
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException", sqe.toString(),
                    context, conn);
            return null;
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString(),
                    context, conn);
            return null;
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, context);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext ctx = getServletContext();

        String folder = Utils.getJoborderFolderName(getServletContext());
        String servlet = getMcsaJoImage(ctx);

        if (!Utils.isOnline(request, ctx)) {
            Utils.invalidImage(response, getServletContext());
            return;
        }

        HttpURLConnection conn = null;
        URL url;

        String serverUrl = Utils.getServerAddress(getServletContext())+folder+"/"+servlet;
        String akey = Utils.getAPIKey(getServletContext());
        String cid = request.getParameter("csaId");
        String source = "mcsa";
        String jid = request.getParameter("joId");

        if (!checkParameters(serverUrl, akey, cid, source, jid, new JSONObject(), response.getWriter(), ctx)) {
            return;
        }

        try {
            url = new URL(serverUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15_000);
            conn.setConnectTimeout(15_000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write("akey="+akey+
                    "&cid="+cid+
                    "&source="+source+
                    "&jid="+jid);

            writer.flush();
            writer.close();
            os.close();
            conn.connect();

            response.setContentType(conn.getContentType());

            InputStream in = conn.getInputStream();
            ServletOutputStream out = response.getOutputStream();

            int count;
            byte[] buffer = new byte[8192];

            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }

            in.close();
            out.close();

        } catch (MalformedURLException | ConnectException | SocketTimeoutException sqe) {
            Utils.invalidImage(response, getServletContext());
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "NetworkException",
                    sqe.toString(), ctx, null);

        } catch (Exception e) {
            Utils.invalidImage(response, getServletContext());
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString(), ctx,
                    null);

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public boolean checkParameters(String serverUrl, String akey, String cid, String source, String jid,
                                JSONObject resJson, PrintWriter out, ServletContext ctx) {

        if (serverUrl == null) {
            Utils.logError("\"serverUrl\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data. Try again or see logs.", out);
            return false;
        } else if (serverUrl.isEmpty()) {
            Utils.logError("\"serverUrl\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data. Try again or see logs.", out);
            return false;
        }

        if (akey == null) {
            Utils.logError("\"akey\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data. Try again or see logs.", out);
            return false;
        } else if (akey.isEmpty()) {
            Utils.logError("\"akey\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data. Try again or see logs.", out);
            return false;
        }

        if (source == null) {
            Utils.logError("\"source\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data. Try again or see logs.", out);
            return false;
        } else if (source.isEmpty()) {
            Utils.logError("\"source\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data. Try again or see logs.", out);
            return false;
        }

        if (jid == null) {
            Utils.logError("\"jid\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data. Try again or see logs.", out);
            return false;
        } else if (jid.isEmpty()) {
            Utils.logError("\"jid\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data. Try again or see logs.", out);
            return false;
        }

        if (cid == null) {
            Utils.logError("\"cid\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data. Try again or see logs.", out);
            return false;
        } else if (cid.isEmpty()) {
            Utils.logError("\"cid\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data. Try again or see logs.", out);
            return false;
        }

        return true;
    }
}