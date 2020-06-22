package wmdc.mobilecsa.servlet.joborder;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wmdcprog on 5/12/2018.
 */

@WebServlet("/approvemcsaqc")

@MultipartConfig(fileSizeThreshold=1024*1024*6, // 5MB
        maxFileSize=1024*1024*3,      // 3MB
        maxRequestSize=1024*1024*50)   // 50MB

public class ApproveMcsaQC extends HttpServlet {

    private String getApproveMcsaQCServlet(ServletContext context) {
        Connection conn = null;
        PreparedStatement prepStmt = null;
        ResultSet resultSet = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("SELECT `value` FROM `configs` WHERE `key` = ?");
            prepStmt.setString(1, "approvemcsaqc_servlet");

            resultSet = prepStmt.executeQuery();
            String key = null;

            if (resultSet.next()) {
                key = resultSet.getString("value");
            }

            return key;
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException",
                    sqe.toString(), context, null);
            return null;
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString(),
                    context, null);
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
        String servlet = getApproveMcsaQCServlet(ctx);

        if (folder == null || servlet == null) {
            Utils.logError("\"folder\": "+folder+"\"servlet\": "+servlet, ctx);
            Utils.printJsonException(resJson, "Cannot find path", out);
            return;
        }

        if (!Utils.isOnline(request, ctx)) {
            Utils.printJsonException(resJson, "Login first.", out);
            return;
        }

        String serverUrl = Utils.getServerAddress(getServletContext())+folder+"/"+servlet;
        String akey = Utils.getAPIKey(getServletContext());

        String cid = request.getParameter("cid");
        String source = request.getParameter("source");
        String joid = request.getParameter("joid");
        String woid = request.getParameter("woid");
        Part wophoto = request.getPart("wophoto");

        //  check wophoto parameter
        if (wophoto == null) {
            Utils.printJsonException(resJson, "Workorder photo null. Try again.", out);
            return;
        }

        if (wophoto.getSize() < 1) {
            Utils.printJsonException(resJson, "Workorder photo invalid size. Try again.", out);
            return;
        }

        InputStream fileInputStream = wophoto.getInputStream();

        HashMap<String, String> params = new HashMap<>();

        //  check string parameters
        checkParameters(serverUrl, akey, cid, source, joid, woid, resJson, out, params, ctx);

        HttpURLConnection conn = null;
        URL url;

        try {

            url = new URL(serverUrl);

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            int bytesRead, bytesAvailable, bufferSize;
            int maxBufferSize = 1024 * 1024;

            byte[] buffer;

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(20000);
            conn.setConnectTimeout(20000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            conn.setRequestProperty("Cookie", "JSESSIONID="+request.getSession().getId());
            conn.setRequestProperty("Host", "ApproveMcsaQC");
            conn.setRequestProperty("Referer", "approvemcsaqc");
            conn.setRequestProperty("X-Requested-Width", "XMLHttpRequest");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            DataOutputStream outputStream;
            outputStream = new DataOutputStream(conn.getOutputStream());
            outputStream.writeBytes(twoHyphens+boundary+lineEnd);
            outputStream.writeBytes("Content-Type: image/jpeg");
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: " +
                    "form-data; name=\"wophoto\";filename=\"wophotofilename\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            /* comment this first, because it does not set the content-type of photo
            outputStream.writeBytes(twoHyphens+boundary+lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"reference\""+lineEnd);
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes("my_reference_text");
            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: " +
                    "form-data; name=\"wophoto\";filename=\"wophotofilename\"" + lineEnd);
            outputStream.writeBytes(lineEnd);*/

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            outputStream.writeBytes(lineEnd);

            for (Map.Entry<String, String> mapEntry : params.entrySet()) {
                String key = mapEntry.getKey();
                String value = mapEntry.getValue();

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\""+key+"\""+lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(value);
                outputStream.writeBytes(lineEnd);
            }

            outputStream.writeBytes(twoHyphens+boundary+twoHyphens+lineEnd);
            int statusCode = conn.getResponseCode();

            outputStream.flush();
            outputStream.close();
            fileInputStream.close();

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

                String qcResponse = stringBuilder.toString();

                if (qcResponse.isEmpty()) {
                    Utils.printJsonException(resJson, "No response from the server.", out);
                    return;
                }

                if (qcResponse.contains("\"success\"")) {
                    resJson = new JSONObject(qcResponse);
                } else {
                    qcResponse = Utils.getCorrectJson(qcResponse);
                    qcResponse = Utils.getCorrectJsonReason(qcResponse);

                    resJson = new JSONObject(qcResponse);
                }

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

    private void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (out != null) {
                    out.close();
                }

                in.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    public void checkParameters(String serverUrl, String akey, String cid, String source, String joid, String woid,
                                JSONObject resJson, PrintWriter out, HashMap<String, String> params,
                                ServletContext ctx) {

        if (cid == null) {
            Utils.logError("\"cid\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return;
        } else if (cid.isEmpty()) {
            Utils.logError("\"cid\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return;
        }

        if (source == null) {
            Utils.logError("\"source\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return;
        } else if (source.isEmpty()) {
            Utils.logError("\"source\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return;
        }

        if (joid == null) {
            Utils.logError("\"joid\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return;
        } else if (joid.isEmpty()) {
            Utils.logError("\"joid\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return;
        }

        if (akey == null) {
            Utils.logError("\"akey\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return;
        } else if (akey.isEmpty()) {
            Utils.logError("\"akey\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return;
        }

        if (serverUrl == null) {
            Utils.logError("\"serverUrl\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return;
        } else if (serverUrl.isEmpty()) {
            Utils.logError("\"serverUrl\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
            return;
        }

        if (woid == null) {
            Utils.logError("\"woid\" parameter is null", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
        } else if (woid.isEmpty()) {
            Utils.logError("\"woid\" parameter is empty", ctx);
            Utils.printJsonException(resJson, "Rejected. Missing data required. See logs or try again.", out);
        }

        params.put("cid", cid);
        params.put("akey", akey);
        params.put("source", source);
        params.put("joid", joid);
        params.put("woid", woid);
    }
}