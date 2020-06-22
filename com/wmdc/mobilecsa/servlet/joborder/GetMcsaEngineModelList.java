package wmdc.mobilecsa.servlet.joborder;

import org.json.JSONException;
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
import java.util.ArrayList;

/**
 * Created by wmdcprog on 7/6/2018.
 */
@WebServlet("/getmcsaenginemodellist")
public class GetMcsaEngineModelList extends HttpServlet {
    private String getMcsaEngineModelList(ServletContext context) {
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT `value` FROM `configs` WHERE `key` = ?");
            prepStmt.setString(1, "getmcsaenginemodellist_servlet");

            resultSet = prepStmt.executeQuery();
            String key = null;

            if (resultSet.next()) {
                key = resultSet.getString("value");
            }

            return key;
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException",
                    sqe.toString(), context, conn);
            return null;
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception",
                    e.toString(), context, conn);
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

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        String folder = Utils.getJoborderFolderName(getServletContext());
        String servlet = getMcsaEngineModelList(ctx);

        if (folder == null || servlet == null) {
            Utils.printJsonException(resJson, "Cannot find path", out);
            Utils.logError("folder: "+folder+", servlet: "+servlet, ctx);
            return;
        }

        String serverUrl = Utils.getServerAddress(getServletContext())+folder+"/"+servlet;
        String akey = Utils.getAPIKey(getServletContext());
        String source = "mcsa";
        String filter = request.getParameter("filter");

        HttpURLConnection conn = null;
        URL url;

        try {
            if (serverUrl == null) {
                Utils.logError("\"serverUrl\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            } else if (serverUrl.isEmpty()) {
                Utils.logError("\"serverUrl\" parameter is empty", ctx);
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            }

            if (akey == null) {
                Utils.logError("\"akey\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            } else if (akey.isEmpty()) {
                Utils.logError("\"akey\" parameter is empty", ctx);
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            }

            if (filter == null) {
                Utils.logError("\"filter\" parameter is null", ctx);
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            } else if (filter.isEmpty()) {
                Utils.logError("\"filter\" parameter is empty", ctx);
                Utils.printJsonException(resJson, "Missing data required. Try again or see logs.", out);
                return;
            }

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
            conn.setRequestProperty("Referer", "getmcsaenginemodellist");
            conn.setRequestProperty("X-Requested-Width", "XMLHttpRequest");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            writer.write("akey="+akey+
                    "&source="+source+
                    "&filter="+URLEncoder.encode(filter, "UTF-8"));

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

                try {
                    resJson = new JSONObject(serverResponse);

                    resJson.put("success", true);
                    out.println(resJson);
                } catch (JSONException pe) {
                    ArrayList<JSONObject> models = new ArrayList<>();
                    resJson = new JSONObject();
                    resJson.put("success", true);
                    resJson.put("models", models);
                    out.println(resJson);
                }

            } else {
                Utils.logError("Request did not succeed. Status code: "+statusCode, ctx);
                Utils.printJsonException(resJson, "Request did not succeed. See logs or try again.", out);
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
}