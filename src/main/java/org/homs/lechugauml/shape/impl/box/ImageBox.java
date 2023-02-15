package org.homs.lechugauml.shape.impl.box;

import org.homs.lechugauml.PropsParser;
import org.homs.lechugauml.shape.Shape;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class ImageBox extends Box {

    static class ImagesCache {
        final Map<String, Image> images = new LinkedHashMap<>();

        public Image getImage(String imageFile) {
            if (images.containsKey(imageFile)) {
                return images.get(imageFile);
            }

            try {
                var imageIcon = new ImageIcon(ImageIO.read(new File(imageFile)));
                var image = imageIcon.getImage();
                images.put(imageFile, image);
                return image;
            } catch (Exception e) {
                var e2 = new RuntimeException("trying to load: '" + imageFile + "'", e);
                e2.printStackTrace();
                return null;
            }
        }
    }

    final static ImagesCache imagesCache = new ImagesCache();
    Image image = null;

    public ImageBox(int x, int y, int width, int height, String attributesText) {
        super(x, y, width, height, attributesText);
        setAttributesText(attributesText);
    }

    @Override
    public void setAttributesText(String attributesText) {
        super.setAttributesText(attributesText);

        Map<String, String> props = PropsParser.parseProperties(attributesText);
        paintBackground = Boolean.parseBoolean(props.getOrDefault("paintbackground", "false"));

        if (props.containsKey("image")) {
            var imageFile = props.get("image");


//            try {
//                var imageIcon = new ImageIcon(ImageIO.read(new File(imageFile)));
//                this.image = imageIcon.getImage();
//            } catch (Exception e) {
//                var e2 = new RuntimeException("trying to load: '" + imageFile + "'", e);
//                e2.printStackTrace();
//            }
            this.image = imagesCache.getImage(imageFile);
        }
    }

    @Override
    public Shape duplicate(int translatex, int translatey) {
        var r = new ImageBox((int) x + translatex, (int) y + translatey, (int) width, (int) height, attributesText);
        r.setAttributesText(attributesText);
        return r;
    }

    @Override
    public void draw(Graphics g) {
        int ix = (int) x;
        int iy = (int) y;
        int iwidth = (int) width;
        int iheight = (int) height;

        Graphics2D g2 = (Graphics2D) g;

        if (paintBackground) {
            g.setColor(backgroundColor);
            g2.fillRect(ix, iy, iwidth, iheight);
        }

        if (this.image != null) {
            g2.drawImage(image, ix, iy, iwidth, iheight, null);
        }
    }
}
