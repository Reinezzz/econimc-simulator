package org.example.economicssimulatorclient.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ReportImageUtil {


    public static String toBase64Png(Node node, int width, int height) {

        WritableImage snapshot = new WritableImage(width, height);
        node.snapshot(new SnapshotParameters(), snapshot);

        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException(I18n.t("error.chart_to_png"), e);
        }
    }
}
