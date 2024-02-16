package com.meteor.setu.util;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class GifEncoder implements Closeable {
    private final ImageWriter writer;
    private final ImageWriteParam params;
    private final IIOMetadata metadata;

    private GifEncoder(ImageOutputStream outputStream, int imageType, int delay, boolean loop) throws IIOInvalidTreeException, IOException {
        writer = ImageIO.getImageWritersBySuffix("gif").next();
        params = writer.getDefaultWriteParam();
        ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
        metadata = writer.getDefaultImageMetadata(typeSpecifier, params);
        configureRootMetadata(delay, loop);
        writer.setOutput(outputStream);
        writer.prepareWriteSequence(null);
    }

    private void configureRootMetadata(int delay, boolean loop) throws IIOInvalidTreeException {
        String metaFormatName = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(delay / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");

        IIOMetadataNode applicationExtensions = getNode(root, "ApplicationExtensions");
        IIOMetadataNode applicationExtension = new IIOMetadataNode("ApplicationExtension");
        applicationExtension.setAttribute("applicationID", "NETSCAPE");
        applicationExtension.setAttribute("authenticationCode", "2.0");
        int loopContinuously = loop ? 0 : 1;
        applicationExtension.setUserObject(new byte[]{0x1, (byte) (loopContinuously & 0xFF), (byte) ((loopContinuously >> 8) & 0xFF)});
        applicationExtensions.appendChild(applicationExtension);

        metadata.setFromTree(metaFormatName, root);
    }

    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        for (int i = 0; i < rootNode.getLength(); i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return (IIOMetadataNode) rootNode.item(i);
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return node;
    }

    public void writeToSequence(BufferedImage img) throws IOException {
        writer.writeToSequence(new IIOImage(img, null, metadata), params);
    }

    @Override
    public void close() throws IOException {
        writer.endWriteSequence();
    }

    public static void convert(BufferedImage[] images, File outputFile, int delay, boolean loop, Integer width, Integer height) throws IOException {
        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputFile)) {
            GifEncoder encoder = new GifEncoder(outputStream, BufferedImage.TYPE_INT_ARGB, delay, loop);
            AffineTransform transform = new AffineTransform();
            transform.scale(width == null ? 1.0 : (double) width / images[0].getWidth(), height == null ? 1.0 : (double) height / images[0].getHeight());
            AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

            for (BufferedImage image : images) {
                encoder.writeToSequence(op.filter(image, null));
            }
        }
    }
}
