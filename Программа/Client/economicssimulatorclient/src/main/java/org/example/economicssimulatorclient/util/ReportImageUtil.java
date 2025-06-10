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

    /**
     * Преобразует любой JavaFX Node (чаще всего Chart) в base64 PNG.
     *
     * @param node  JavaFX-график или любой визуальный компонент
     * @param width ширина итогового изображения (например, 900)
     * @param height высота итогового изображения (например, 600)
     * @return base64 PNG
     */
    public static String toBase64Png(Node node, int width, int height) {
        // Снимаем снимок с ноды
        WritableImage snapshot = new WritableImage(width, height);
        node.snapshot(new SnapshotParameters(), snapshot);

        // Преобразуем в BufferedImage
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

        // Сохраняем в PNG
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка преобразования графика в base64 PNG", e);
        }
    }
}
