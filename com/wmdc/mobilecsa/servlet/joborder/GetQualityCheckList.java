package wmdc.mobilecsa.servlet.joborder;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

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
@WebServlet("/getqclist")
public class GetQualityCheckList extends HttpServlet {
    private String getQcList() {
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT `value` FROM `configs` WHERE `key` = ?");
            prepStmt.setString(1, "getqclist_servlet");

            resultSet = prepStmt.executeQuery();

            String key = null;
            if (resultSet.next()) {
                key = resultSet.getString("value");
            }

            return key;
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException", sqe.toString());
            return null;
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException", e.toString());
            return null;
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();

        if (!Utils.isOnline(request)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        String folder = Utils.getJoborderFolderName(getServletContext());
        String servlet = getQcList();

        if (folder == null || servlet == null) {
            Utils.printJsonException(resJson, "Cannot find path", out);
            Utils.logError("folder: "+folder+", servlet: "+servlet);
            return;
        }

        HttpURLConnection conn = null;
        URL url;

        String serverUrl = Utils.getServerAddress(getServletContext())+folder+"/"+servlet;
        String akey = Utils.getAPIKey(getServletContext());
        String cid = request.getParameter("cid");
        String source = request.getParameter("source");
        String page = request.getParameter("page");

        checkParameters(serverUrl, akey, cid, source, page, resJson, out);

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
            conn.setRequestProperty("Referer", "GetQualityCheckList");
            conn.setRequestProperty("X-Requested-Width", "XMLHttpRequest");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write(
                "akey="+akey+
                "&cid="+cid+
                "&source="+source+
                "&page="+page);

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

                String serverResponse = stringBuilder.toString();

                if (serverResponse.isEmpty()) {
                    Utils.printJsonException(resJson, "No response from server.", out);
                    return;
                }

                resJson = new JSONObject(serverResponse);
                resJson.put("success", true);
                out.println(resJson);
            } else {
                Utils.logError("Request did not succeed. Status code: "+statusCode);
                Utils.printJsonException(resJson, "Request did not succeed.", out);
            }
        } catch (MalformedURLException | ConnectException | SocketTimeoutException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "NetworkException",
                    sqe.toString());
            Utils.printJsonException(new JSONObject(), sqe.toString(), out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void checkParameters(String serverUrl, String akey, String cid, String source, String page,
                                JSONObject resJson, PrintWriter out) {
        try {
            if (serverUrl == null) {
                Utils.logError("\"serverUrl\" parameter is empty");
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            } else if (serverUrl.isEmpty()) {
                Utils.logError("\"serverUrl\" parameter is empty");
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            }

            if (akey == null) {
                Utils.logError("\"akey\" parameter is empty");
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            } else if (akey.isEmpty()) {
                Utils.logError("\"akey\" parameter is empty");
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            }

            if (cid == null) {
                Utils.logError("\"cid\" parameter is empty");
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            } else if (cid.isEmpty()) {
                Utils.logError("\"cid\" parameter is empty");
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            }

            if (source == null) {
                Utils.logError("\"source\" parameter is empty");
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            } else if (source.isEmpty()) {
                Utils.logError("\"source\" parameter is empty");
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            }

            if (page == null) {
                Utils.logError("\"page\" parameter is empty");
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            } else if (page.isEmpty()) {
                Utils.logError("\"page\" parameter is empty");
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            }
        } catch (IOException ie) {
            System.err.println(ie.toString());
        }
    }
}