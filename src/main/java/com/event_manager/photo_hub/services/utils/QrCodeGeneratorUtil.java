package com.event_manager.photo_hub.services.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class QrCodeGeneratorUtil {

  public static String generateQrCode(String data) {
    try {
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
      return Base64.getUrlEncoder().withoutPadding().encodeToString(outputStream.toByteArray());
    } catch (Exception e) {
      throw new RuntimeException("Erro ao gerar QR Code", e);
    }
  }
}
