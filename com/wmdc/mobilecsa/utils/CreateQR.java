package wmdc.mobilecsa.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/createqr")
public class CreateQR extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
        final GoogleAuthenticatorKey googleAuthenticatorKey = googleAuthenticator.createCredentials();

        String secretKey = googleAuthenticatorKey.getKey();
        String email = "test@gmail.com";
        String companyName = "Test Company";
        String barCodeUrl = getGoogleAuthenticatorBarCode(secretKey, email, companyName);

        try {
            createQRCode(barCodeUrl, "C:\\Users\\wmdcprog\\Pictures\\qrcode.png", 400, 400, getServletContext(),
                    response.getWriter());

        } catch (WriterException we) {
            response.getWriter().println(we.toString());
        }
    }

    public static void createQRCode(String barCodeData, String filePath, int height, int width, ServletContext context,
                                    PrintWriter out) throws WriterException, IOException {

        BitMatrix matrix = new MultiFormatWriter().encode(
                barCodeData, BarcodeFormat.QR_CODE, width, height);

        //try (FileOutputStream out = new FileOutputStream(filePath)) {
            //MatrixToImageWriter.writeToStream(matrix, "png", out);
        //}

        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        try {

            Utils.databaseForName(context);
            Connection conn = Utils.getConnection(context);

            PreparedStatement prepStmt = conn.prepareStatement("INSERT INTO qrcodes (qr_image) VALUES (?)");
            prepStmt.setBlob(1, inputStream);

            out.println(prepStmt.executeUpdate());

        } catch (ClassNotFoundException | SQLException sqe) {
            sqe.printStackTrace();
        }
    }

    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");

        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
