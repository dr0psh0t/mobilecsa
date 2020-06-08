package wmdc.mobilecsa.servlet;

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
import java.sql.ResultSet;
import java.sql.SQLException;

@MultipartConfig(fileSizeThreshold = 1024*1024*6,   //  6MB
        maxFileSize=1024*1024*3,                    //  3MB
        maxRequestSize = 1024*1024*50)

@WebServlet("/uploadphoto")

public class UploadPhoto extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        JSONObject resJson = new JSONObject();

        Connection conn = null;
        PreparedStatement prepStmt = null;

        InputStream photoStream = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int num = Integer.parseInt(request.getParameter("num"));
            if (num % 2 != 0) {
                Utils.printJsonException(resJson, "num must be even", out);
                return;
            }

            Part photoPart = request.getPart("photo");

            if (photoPart.getSize() < 1) {
                Utils.printJsonException(resJson, "photo size is 0", out);
                return;
            }

            photoStream = photoPart.getInputStream();

            if (photoPart.getSize() > (Utils.REQUIRED_IMAGE_BYTES-100_000)) {
                photoStream = Utils.reduceImage(photoStream);
            }

            if (photoStream == null) {
                Utils.printJsonException(resJson, "File resource is empty. Try again or see logs.", out);
                return;
            }

            prepStmt = conn.prepareStatement("INSERT INTO photos(photo) VALUES (?)");
            prepStmt.setBlob(1, photoStream);

            if (prepStmt.executeUpdate() > 0) {
                Utils.printSuccessJson(resJson, "Uploaded", out);
            } else {
                Utils.printJsonException(resJson, "No photo was uploaded", out);
            }
        } catch (SQLException | ClassNotFoundException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "SQLException", sqe.toString(),
                    getServletContext());
            Utils.printJsonException(resJson, "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString(),
                    getServletContext());
            Utils.printJsonException(resJson, "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, null, getServletContext());
            if (photoStream != null) {
                photoStream.close();
            }
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}