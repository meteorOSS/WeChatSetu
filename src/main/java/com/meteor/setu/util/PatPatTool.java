package com.meteor.setu.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PatPatTool {

    public static void getPat(String userId, File avatarFile, File tmp, int delay) throws IOException {
        File savePath = new File(tmp, userId + "_pat.gif");
        if (!savePath.exists()) {
            if (!tmp.exists()) tmp.mkdir();
            mkImg(avatarFile, savePath, delay);
        }
    }

    public static void getImagePat(String imageId, File imageFile, File tmp, int delay) throws IOException {
        File savePath = new File(tmp, imageId + "_pat.gif");
        mkImg(imageFile, savePath, delay);
    }

    private static void mkImg(File imageFile, File savePath, int delay) throws IOException {
        BufferedImage avatarImage = ImageIO.read(imageFile);
        int targetSize = avatarImage.getWidth();
        BufferedImage roundImage = makeRoundedImage(avatarImage, targetSize);
        BufferedImage scaledImage = scaleImage(roundImage, 112, 112);

        BufferedImage[] images = new BufferedImage[5];
        for (int i = 0; i < images.length; i++) {
            images[i] = processImage(scaledImage, i, 100, 100, 12, 16, 0);
        }
        // Adjust parameters as needed for different frames
        // This is a simplified example, you should adjust x, y, and hy values based on your animation frames

        GifEncoder.convert(images, savePath, delay, true, null, null);
    }

    private static BufferedImage makeRoundedImage(BufferedImage image, int targetSize) {
        BufferedImage roundImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = roundImage.createGraphics();
        // Apply rendering hints for quality
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.Src);
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, targetSize, targetSize, targetSize, targetSize));
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        return roundImage;
    }

    private static BufferedImage scaleImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = outputImage.createGraphics();
        g2.drawImage(resultingImage, 0, 0, null);
        g2.dispose();
        return outputImage;
    }

    private static BufferedImage processImage(BufferedImage image, int i, int w, int h, int x, int y, int hy) throws IOException {
        BufferedImage handImage = ImageIO.read(PatPatTool.class.getResourceAsStream("/data/PatPat/img" + i + ".png"));
        BufferedImage processingImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = processingImage.createGraphics();
        g2d.drawImage(image, x, y, w, h, null);
        g2d.drawImage(handImage, 0, hy, null);
        g2d.dispose();
        return processingImage;
    }
}
