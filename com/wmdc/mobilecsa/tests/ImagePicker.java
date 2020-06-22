package wmdc.mobilecsa.tests;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/imagepicker")

@MultipartConfig(fileSizeThreshold=1024*1024*6, // 5MB
        maxFileSize=1024*1024*4,      // 3MB
        maxRequestSize=1024*1024*50)   // 50MB

public class ImagePicker extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();

        Part wophoto = request.getPart("image");

        /*
        resJson.put("success", true);
        resJson.put("reason", image.getSize()+"-"+image.getContentType());

        System.out.println(resJson);
        out.println(resJson);*/

        InputStream wophotoStream = wophoto.getInputStream();

        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            prepStmt = conn.prepareStatement("INSERT INTO qc_photos (photo, content_type) VALUES (?, ?)");
            prepStmt.setBlob(1, wophotoStream);
            prepStmt.setString(2, wophoto.getContentType());
            prepStmt.execute();

            System.out.println();
            Utils.printSuccessJson(resJson, "OK", out);

        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.printJsonException(resJson, sqe.toString(), out);

        } catch (Exception e) {
            Utils.printJsonException(resJson, e.toString(), out);

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
