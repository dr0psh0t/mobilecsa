package wmdc.mobilecsa.servlet.joborder;

import org.json.JSONObject;
import wmdc.mobilecsa.utils.Utils;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by wmdcprog on 7/30/2018.
 */
@MultipartConfig(fileSizeThreshold = 1024*1024*6,   //  6MB
        maxFileSize=1024*1024*3,                    //  3MB
        maxRequestSize = 1024*1024*50)

@WebServlet("/updateinitialjoborder")

public class UpdateInitialJoborder extends HttpServlet {
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

        String initialJoborderId = request.getParameter("initialJoborderId");
        String customerId = request.getParameter("customerId");
        String customer = request.getParameter("customer");
        String source = request.getParameter("source");
        String mobile = request.getParameter("mobile");
        String purchaseOrder = request.getParameter("purchaseOrder");
        String poDate = request.getParameter("poDate");
        String make = request.getParameter("make");
        String cat = request.getParameter("cat");
        String modelId = request.getParameter("modelId");
        String serialNo = request.getParameter("serialNo");
        String dateReceive = request.getParameter("dateReceive");
        String dateCommit = request.getParameter("dateCommit");
        String refNo = request.getParameter("refNo");
        String remarks = request.getParameter("remarks");
        String preparedBy = request.getParameter("preparedBy");
        String joSignature = request.getParameter("joSignature");
        String joNumber = request.getParameter("joNumber");
        Part photoPart = request.getPart("photo");

        Connection conn = null;
        PreparedStatement prepStmt = null;

        try {
            Utils.databaseForName(getServletContext());
            conn = Utils.getConnection(getServletContext());

            int customerIdInt = Integer.parseInt(customerId);
            int modelIdInt = Integer.parseInt(modelId);
            int preparedByInt = Integer.parseInt(preparedBy);
            int initialJoborderIdInt = Integer.parseInt(initialJoborderId);

            prepStmt = conn.prepareStatement("UPDATE initial_joborder SET jo_number = ?, customer_id = ?, " +
                    "customer_source = ?, mobile = ?, model_id = ?, serial_num = ?, po_date = ?, date_received = ?, " +
                    "date_committed = ?, category = ?, prepared_by = ?, remarks = ?, purchase_order = ?, " +
                    "reference_number = ?, make = ?, csa_id = ?, customer = ? WHERE initial_joborder_id = ?");

            prepStmt.setString(1, joNumber);
            prepStmt.setInt(2, customerIdInt);
            prepStmt.setString(3, source);
            prepStmt.setString(4, mobile);
            prepStmt.setInt(5, modelIdInt);
            prepStmt.setString(6, serialNo);
            prepStmt.setString(7, poDate);
            prepStmt.setString(8, dateReceive);
            prepStmt.setString(9, dateCommit);
            prepStmt.setString(10, cat);
            prepStmt.setInt(11, preparedByInt);
            prepStmt.setString(12, remarks);
            prepStmt.setString(13, purchaseOrder);
            prepStmt.setString(14, refNo);
            prepStmt.setString(15, make);
            prepStmt.setInt(16, preparedByInt);
            prepStmt.setString(17, customer);
            prepStmt.setInt(18, initialJoborderIdInt);
            prepStmt.executeUpdate();

            InputStream photoStream;
            InputStream inputStream;

            String updatePhotoStr = "";

            if (photoPart != null) {
                if (photoPart.getSize() > (Utils.REQUIRED_IMAGE_BYTES-100_000)) {
                    photoStream = Utils.reduceImage(photoPart.getInputStream());
                    inputStream = Utils.reduceImage(photoPart.getInputStream());
                } else {
                    photoStream = photoPart.getInputStream();
                    inputStream = photoPart.getInputStream();
                }

                updatePhotoStr = updatePhoto(conn, initialJoborderIdInt, preparedByInt, photoStream, inputStream);
            }

            updateSignature(conn, joSignature, initialJoborderIdInt);

            Utils.printSuccessJson(resJson, "Initial joborder has been updated "+updatePhotoStr, out);
        } catch (ClassNotFoundException | SQLException sqe) {
            Utils.displayStackTraceArray(sqe.getStackTrace(), Utils.JOBORDER_PACKAGE, "DBException", sqe.toString());
            Utils.printJsonException(new JSONObject(), "Database error occurred.", out);
        } catch (Exception e) {
            Utils.displayStackTraceArray(e.getStackTrace(), Utils.JOBORDER_PACKAGE, "Exception", e.toString());
            Utils.printJsonException(new JSONObject(), "Exception has occurred.", out);
        } finally {
            Utils.closeDBResource(conn, prepStmt, null);
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Utils.illegalRequest(response);
    }

    private int[] getImageDimension(InputStream inputStream) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(inputStream);
        return new int[]{bufferedImage.getWidth(), bufferedImage.getHeight()};
    }

    private void updateSignature(Connection conn, String joSignature, int initialJoborderId) throws Exception {
        if (!joSignature.isEmpty()) {
            InputStream signatureStream = Utils.getSignatureInputStream(joSignature);

            PreparedStatement prepStmt = conn.prepareStatement("UPDATE initial_joborder SET signature = ? " +
                    "WHERE initial_joborder_id = ?");

            prepStmt.setBlob(1, signatureStream);
            prepStmt.setInt(2, initialJoborderId);
            prepStmt.executeUpdate();
            prepStmt.close();
        }
    }

    private String updatePhoto(Connection conn, int initialJoborderId, int preparedBy, InputStream photoStream,
                             InputStream inputStream) throws Exception {

        int[] imageDimension = getImageDimension(inputStream);
        PreparedStatement prepStmt;

        if (isPictureSaved(initialJoborderId, conn)) {

            prepStmt = conn.prepareStatement("UPDATE initial_joborder_image SET image = ?, width = ?, " +
                    "height = ? WHERE initial_joborder_id = ?");

            prepStmt.setBlob(1, photoStream);
            prepStmt.setInt(2, imageDimension[0]);
            prepStmt.setInt(3, imageDimension[1]);
            prepStmt.setInt(4, initialJoborderId);
            prepStmt.executeUpdate();
            prepStmt.close();

            return "";
        } else {

            prepStmt = conn.prepareStatement("INSERT INTO initial_joborder_image (image, initial_joborder_id, " +
                    "prepared_by, date_stamp, image_type, width, height) VALUES (?, ?, ?, NOW(), ?, ?, ?)");

            prepStmt.setBlob(1, photoStream);
            prepStmt.setInt(2, initialJoborderId);
            prepStmt.setInt(3, preparedBy);
            prepStmt.setString(4, "jpeg");
            prepStmt.setInt(5, imageDimension[0]);
            prepStmt.setInt(6, imageDimension[1]);
            prepStmt.execute();
            prepStmt.close();

            if (isPictureSaved(initialJoborderId, conn)) {
                return "";
            } else {
                return "but no picture was saved.";
            }
        }
    }

    private boolean isPictureSaved(int initialJoId, Connection conn) throws SQLException {

        PreparedStatement prepStmt = conn.prepareStatement(
                "SELECT COUNT (*) AS pictureCount FROM initial_joborder_image WHERE initial_joborder_id = ?");

        prepStmt.setInt(1, initialJoId);
        ResultSet resultSet = prepStmt.executeQuery();

        int pictureCount = 0;
        if (resultSet.next()) {
            pictureCount = resultSet.getInt("pictureCount");
        }

        prepStmt.close();
        resultSet.close();

        return pictureCount > 0;
    }
}