package wmdc.mobilecsa.utils;

import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/loggers")

@MultipartConfig(fileSizeThreshold=1024*1024*6, // 5MB
        maxFileSize=1024*1024*3,      // 3MB
        maxRequestSize=1024*1024*50)   // 50MB

public class Loggers extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("text/html");
        JSONObject resJson = new JSONObject();

        /*
        Utils.logError("Logging error message", getServletContext());
        Utils.logMsg("Logging message", getServletContext());*/

        Part wophoto = request.getPart("wophoto");

        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("INSERT INTO qc_photos (photo) VALUES (?)");
            prepStmt.setBlob(1, wophoto.getInputStream());
            prepStmt.execute();

            resJson.put("success", true);
            resJson.put("reason", wophoto.getSize());
            response.getWriter().println(resJson);

            System.out.println(wophoto.getSize());

        } catch (ClassNotFoundException | SQLException sqe) {
            sqe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Utils.closeDBResource(conn, prepStmt, null, getServletContext());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
