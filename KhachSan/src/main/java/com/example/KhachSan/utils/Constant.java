package com.example.KhachSan.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Base64;

public class Constant {
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String IMAGE_SAVE_PATH = "C:\\T3H_Group3\\Source\\Images\\product\\";
    public static final String INVOICE_PDF_PATH = Paths.get("").toAbsolutePath().getParent().toString() + "/Pdf/invoice/";
    public static String VNP_TMN_CODE = "HRWL34TM";
    public static String VNP_HASH_SECRET = "5P36IUXFLSJPVCTQHQ9IP46G77Z1V5JL";
    public static String VNP_PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static String VNP_PAY_VERSION = "2.1.0";
    public static Integer ORDER_STATUS_UNPAID = 1;
    public static Float WIDTH_PDF = 570f;

    public static String encodeImage(String imgPath) throws Exception {
        FileInputStream stream = new FileInputStream(imgPath);

        int bufLength = 2048;
        byte[] buffer = new byte[2048];

        byte[] data;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int readLength;
        while ((readLength = stream.read(buffer, 0, bufLength)) != -1) {
            out.write(buffer, 0, readLength);
        }

        data = out.toByteArray();

        String imageString = Base64.getEncoder().encodeToString(data);

        out.close();
        stream.close();

        return imageString;
    }
}
