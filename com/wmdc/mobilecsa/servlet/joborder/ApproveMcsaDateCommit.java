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
 * Created by wmdcprog on 5/12/2018.
 */
@WebServlet("/approvemcsadatecommit")
public class ApproveMcsaDateCommit extends HttpServlet {
    private String getApproveMcsaDateCommitServlet(ServletContext context) {
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT `value` FROM `configs` WHERE `key` = ?");
            prepStmt.setString(1, "approvemcsadatecommit_servlet");

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
            Utils.closeDBResource(conn, prepStmt, resultSet, context
            );
        }
    }

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

        String folder = Utils.getJoborderFolderName(getServletContext());
        String servlet = getApproveMcsaDateCommitServlet(ctx);

        if (folder == null || servlet == null) {
            Utils.logError("\"folder\": "+folder+"\"servlet\": "+servlet, ctx);
            Utils.printJsonException(resJson, "Cannot find path", out);
            return;
        }

        HttpURLConnection conn = null;
        URL url;

        String serverUrl = Utils.getServerAddress(getServletContext())+folder+"/"+servlet;
        String akey = Utils.getAPIKey(getServletContext());
        String cid = request.getParameter("cid");
        String source = request.getParameter("source");
        String joid = request.getParameter("joid");

        if (!checkParameters(cid, source, joid, akey, serverUrl, resJson, out, ctx)) {
            return;
        }

        try {
            url = new URL(serverUrl);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15_000);
            conn.setConnectTimeout(15_000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            conn.setRequestProperty("Cookie", "JSESSIONID="+request.getSession().getId());
            conn.setRequestProperty("Host", "localhost:8080");
            conn.setRequestProperty("Referer", "approvemcsadatecommit");
            conn.setRequestProperty("X-Requested-Width", "XMLHttpRequest");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write("cid="+cid+
                    "&akey="+akey+
                    "&source="+source+
                    "&joid="+joid);

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

                String dcResponse = stringBuilder.toString();

                if (dcResponse.isEmpty()) {
                    Utils.printJsonException(resJson, "No response from server.", out);
                    return;
                }

                if (dcResponse.contains("\"success\"")) {
                    resJson = new JSONObject(dcResponse);
                } else {
                    dcResponse = getCorrectJson(dcResponse);
                    dcResponse = getCorrectJsonReason(dcResponse);
                    resJson = new JSONObject(dcResponse);
                }

                out.println(resJson);

            } else {
                Utils.logError("Approve request did not succeed. Status code: "+statusCode, ctx);
                Utils.printJsonException(resJson, "Approve request did not succeed.", out);
            }

        } catch (MalformedURLException | ConnectException | SocketTimeoutException ne) {
            Utils.printJsonException(new JSONObject(), "Cannot approve joborder at this time.", out);

            Utils.displayStackTraceArray(ne.getStackTrace(), Utils.JOBORDER_PACKAGE, "NetworkException",
                    ne.toString(), ctx, null);
        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot approve joborder at the moment.", out);
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

    private String getCorrectJson(String incorrectJsonStr) {
        int start = incorrectJsonStr.indexOf("success");
        int end = incorrectJsonStr.lastIndexOf("s:") + 1;

        String correctJson;

        correctJson = incorrectJsonStr.substring(0, start) + "\"" + incorrectJsonStr.substring(start, end) + "\""
                + incorrectJsonStr.substring(end, incorrectJsonStr.length());

        return correctJson;
    }

    private String getCorrectJsonReason(String incorrectJsonStr) {
        if (incorrectJsonStr.contains("reason")) {
            int start = incorrectJsonStr.indexOf("reason");
            int end = incorrectJsonStr.lastIndexOf("n:") + 1;

            String correctJson;

            correctJson = incorrectJsonStr.substring(0, start) + "\"" + incorrectJsonStr.substring(start, end) + "\""
                    + incorrectJsonStr.substring(end, incorrectJsonStr.length());

            return correctJson;
        } else {
            return incorrectJsonStr;
        }
    }

    public boolean checkParameters(String cid, String source, String joid, String aKey, String serverUrl,
                                JSONObject resJson, PrintWriter out, ServletContext ctx) {

        if (cid == null) {
            Utils.logError("\"cid\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return false;
        } else if (cid.isEmpty()) {
            Utils.logError("\"cid\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return false;
        }

        if (source == null) {
            Utils.logError("\"source\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return false;
        } else if (source.isEmpty()) {
            Utils.logError("\"source\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return false;
        }

        if (joid == null) {
            Utils.logError("\"joid\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return false;
        } else if (joid.isEmpty()) {
            Utils.logError("\"joid\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return false;
        }

        if (aKey == null) {
            Utils.logError("\"akey\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return false;
        } else if (aKey.isEmpty()) {
            Utils.logError("\"akey\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return false;
        }

        if (serverUrl == null) {
            Utils.logError("\"serverUrl\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return false;
        } else if (serverUrl.isEmpty()) {
            Utils.logError("\"serverUrl\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return false;
        }

        return true;
    }
}