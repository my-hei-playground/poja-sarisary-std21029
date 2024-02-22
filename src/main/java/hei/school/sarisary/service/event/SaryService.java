package hei.school.sarisary.service.event;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;

@Service
public class SaryService {
  public File convertByteArrayToGrayScaleFile(byte[] imageData, String fileName)
      throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(imageData);
    BufferedImage bufferedImage = ImageIO.read(bis);

    for (int y = 0; y < bufferedImage.getHeight(); y++) {
      for (int x = 0; x < bufferedImage.getWidth(); x++) {
        Color color = new Color(bufferedImage.getRGB(x, y));
        int grayValue = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
        Color grayColor = new Color(grayValue, grayValue, grayValue);
        bufferedImage.setRGB(x, y, grayColor.getRGB());
      }
    }

    File file = new File(fileName.endsWith(".jpg") ? fileName : fileName + ".jpg");
    try (FileOutputStream fos = new FileOutputStream(file)) {
      ImageIO.write(bufferedImage, "jpg", fos);
    }

    return file;
  }

  public File convertByteArrayToFile(byte[] imageData, String fileName) throws IOException {
    File file = new File(fileName);

    String lowerCaseFileName = fileName.toLowerCase();
    if (!lowerCaseFileName.endsWith(".jpg")
        && !lowerCaseFileName.endsWith(".jpeg")
        && !lowerCaseFileName.endsWith(".png")
        && !lowerCaseFileName.endsWith(".gif")) {
      fileName += ".jpg";
    }

    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(imageData);
    }

    return file;
  }
}
