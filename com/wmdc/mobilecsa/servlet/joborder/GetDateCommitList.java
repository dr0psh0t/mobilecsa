package wmdc.mobilecsa.servlet.joborder;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
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
 * Created by wmdcprog on 4/23/2018.
 */
@WebServlet("/getdclist")
public class GetDateCommitList extends HttpServlet {
    private String getDateCommitListServlet(ServletContext context) {
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT `value` FROM `configs` WHERE `key` = ?");
            prepStmt.setString(1, "getdatecommitlist_servlet");

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
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString(), context,
                    conn);
            return null;
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, context);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();
        ServletContext ctx = getServletContext();

        String folder = Utils.getJoborderFolderName(getServletContext());
        String servlet = getDateCommitListServlet(ctx);

        if (folder == null || servlet == null) {
            Utils.logError("\"folder\": "+folder+"\"servlet\": "+servlet, ctx);
            Utils.printJsonException(resJson, "Cannot find path", out);
            return;
        }

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        HttpURLConnection conn = null;
        URL url;

        String serverUrl = Utils.getServerAddress(getServletContext())+folder+"/"+servlet;
        String akey = Utils.getAPIKey(getServletContext());
        String cid = request.getParameter("cid");
        String source = request.getParameter("source");

        checkParameter(serverUrl, akey, cid, source, resJson, out, ctx);

        try {
            url = new URL(serverUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            conn.setRequestProperty("Cookie", "JSESSIONID="+request.getSession().getId());
            conn.setRequestProperty("Host", "localhost:8080");
            conn.setRequestProperty("Referer", "GetDateCommitList");
            conn.setRequestProperty("X-Requested-Width", "XMLHttpRequest");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write("akey="+akey+
                    "&cid="+cid+
                    "&source="+source);

            writer.flush();
            writer.close();
            os.close();
            conn.connect();

            int statusCode = conn.getResponseCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                InputStream connIStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connIStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                connIStream.close();
                bufferedReader.close();

                String serverResponse = stringBuilder.toString();

                if (serverResponse.isEmpty()) {
                    Utils.printJsonException(resJson, "No response from server.", out);
                    return;
                }

                resJson = new JSONObject(serverResponse);
                out.println(resJson);

            } else {
                Utils.logError("Approve request did not succeed. Status code: "+statusCode, ctx);
                Utils.printJsonException(resJson, "Approve request did not succeed.", out);
            }

        } catch (MalformedURLException | ConnectException | SocketTimeoutException sqe) {
            Utils.printJsonException(new JSONObject(), sqe.toString(), out);
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "NetworkException",
                    sqe.toString(), ctx, null);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString(), ctx,
                    null);

        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    private void checkParameter(String serverUrl, String akey, String cid, String source, JSONObject resJson,
                               PrintWriter out, ServletContext ctx) {

        if (cid == null) {
            Utils.logError("\"cid\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (cid.isEmpty()) {
            Utils.logError("\"cid\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        if (source == null) {
            Utils.logError("\"source\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (source.isEmpty()) {
            Utils.logError("\"source\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        if (serverUrl == null) {
            Utils.logError("\"serverUrl\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        } else if (serverUrl.isEmpty()) {
            Utils.logError("\"qtype\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
            return;
        }

        if (akey == null) {
            Utils.logError("\"akey\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
        } else if (akey.isEmpty()) {
            Utils.logError("\"akey\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
        }
    }
}