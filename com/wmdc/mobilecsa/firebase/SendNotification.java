package wmdc.mobilecsa.firebase;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wmdcprog on 4/10/2019.
 */
@WebServlet("/sendnotification")
public class SendNotification extends HttpServlet {

    final String legacyServerKey = "AIzaSyAmabKmgBRgnkxyNGQahWfLSBcmMpBnlPM";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        JSONObject resJson = new JSONObject();
        PrintWriter out = response.getWriter();

        String title;
        String body;
        String deviceToken;

        try
        {
            title = request.getParameter("title");
            body = request.getParameter("body");
            deviceToken = request.getParameter("deviceToken");

            send(deviceToken, title, body, resJson, out);
        }
        catch (Exception e)
        {
            Utils.printJsonException(resJson, e.toString(), out);
            Utils.displayStackTraceArray(e.getStackTrace(), "wmdc.mobilecsa.firebase",
                    "Exception", e.toString(), getServletContext(), null);
        }
    }

    HttpURLConnection conn = null;
    OutputStreamWriter wr = null;

    public void send(String token, String title, String body, JSONObject resJson, PrintWriter out)
    {
        try
        {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            conn = (HttpURLConnection) url.openConnection();

            conn.setUseCaches(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "key="+legacyServerKey);
            conn.setRequestProperty("Content-Type", "application/json");

            JSONObject json = new JSONObject();
            json.put("to", token);

            JSONObject info = new JSONObject();
            info.put("title", title);
            info.put("body", body);

            json.put("notification", info);

            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();
            conn.getInputStream();

            Utils.printSuccessJson(resJson, "Message sent!", out);
        }
        catch (Exception e)
        {
            Utils.printJsonException(resJson, e.toString(), out);
            Utils.displayStackTraceArray(e.getStackTrace(), "wmdc.mobilecsa.firebase",
                    "Exception", e.toString(), getServletContext(), null);
        }
        finally
        {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}