package wmdc.mobilecsa.servlet.getJsonData;

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
 * Created by wmdcprog on 7/2/2018.
 */
@WebServlet("/getmcsacustomerlist")
public class GetMcsaCustomerList extends HttpServlet {
    private String getMcsaCustomerListServlet(ServletContext context) {
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT `value` FROM `configs` WHERE `key` = ?");
            prepStmt.setString(1, "getmcsacustomerlist_servlet");

            resultSet = prepStmt.executeQuery();
            String key = null;

            if (resultSet.next()) {
                key = resultSet.getString("value");
            }

            return key;
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE,
                    "db_exception", sqe.toString(), context, conn);
            return null;
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE,
                    "exception", e.toString(), context, conn);
            return null;
        } finally {
            Utils.closeDBResource(conn, prepStmt, resultSet, getServletContext());
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

        String serverUrl;
        String akey;
        String source;
        String filter;
        String cid;

        HttpURLConnection conn = null;

        String folder = Utils.getJoborderFolderName(getServletContext());
        String servlet = getMcsaCustomerListServlet(ctx);

        if (folder == null || servlet == null) {
            Utils.printJsonException(resJson, "Cannot find path", out);
            Utils.logError("Error servlet path: folder= "+folder+", servlet= "+servlet+"\n\n", ctx);
            return;
        }

        try {
            serverUrl = Utils.getServerAddress(getServletContext())+folder+"/"+servlet;
            akey = Utils.getAPIKey(getServletContext());
            source = "mcsa";
            filter = request.getParameter("filter");
            cid = request.getParameter("cid");

            if (serverUrl == null) {
                Utils.logError("\"serverUrl\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (serverUrl.isEmpty()) {
                Utils.logError("\"serverUrl\" parameter is empty", ctx);
                Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (akey == null) {
                Utils.logError("\"akey\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (akey.isEmpty()) {
                Utils.logError("\"akey\" parameter is empty", ctx);
                Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (filter == null) {
                Utils.logError("\"filter\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (filter.isEmpty()) {
                Utils.logError("\"filter\" parameter is empty", ctx);
                Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
                return;
            }

            if (cid == null) {
                Utils.logError("\"cid\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
                return;
            } else if (cid.isEmpty()) {
                Utils.logError("\"cid\" parameter is empty", ctx);
                Utils.printJsonException(resJson, "Missing data required. See logs or try again.", out);
                return;
            }

            URL url = new URL(serverUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(20_000);
            conn.setConnectTimeout(20_000);
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

            writer.write("akey="+akey+"&source="+source+"&cid="+cid+
                    "&filter="+URLEncoder.encode(filter, "UTF-8"));

            writer.flush();
            writer.close();
            os.close();
            conn.connect();

            int statusCode = conn.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                InputStream connStream = conn.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connStream));
                StringBuilder stringBuilder = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                connStream.close();
                bufferedReader.close();

                resJson = new JSONObject(stringBuilder.toString());
                out.println(resJson);

            } else {
                Utils.printJsonException(resJson, "Request did not succeed.", out);
            }

        } catch (MalformedURLException | ConnectException | SocketTimeoutException e) {
            Utils.printJsonException(new JSONObject(), "Cannot get customer at this time.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Network Exception",
                    e.toString(), ctx, null);

        } catch (Exception e) {
            Utils.printJsonException(new JSONObject(), "Cannot get customer at the moment.", out);
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.GET_JSON_DATA_PACKAGE, "Exception", e.toString(),
                    ctx, null);

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
}