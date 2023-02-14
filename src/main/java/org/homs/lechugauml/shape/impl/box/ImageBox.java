package org.homs.lechugauml.shape.impl.box;

import org.homs.lechugauml.PropsParser;
import org.homs.lechugauml.shape.Shape;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

/**
 * Lechuga UML - Powered with LechugaScript and with bocadillos
 *
 * @author mohms
 */
public class ImageBox extends Box {

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
//                BufferedImage img = ImageIO.read(new File(imageFile));
//                this.image=image;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

//            ImageIcon imageIcon = new ImageIcon(imageFile);
//            Image tmpImage = imageIcon.getImage();
//            BufferedImage img = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
//            img.getGraphics().drawImage(tmpImage, 0, 0, null);
//            tmpImage.flush();
//            this.image = img;

            try {
                var imageIcon = new ImageIcon(ImageIO.read(new File(imageFile)));
                this.image = imageIcon.getImage();
            } catch (Exception e) {
                var e2 = new RuntimeException("trying to load: '" + imageFile + "'", e);
                e2.printStackTrace();
            }

//            BufferedImage img = null;
//            try {
//                img = ImageIO.read(new File(imageFile));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            int w = img.getWidth(null);
//            int h = img.getHeight(null);
//            BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//            Graphics g = bi.getGraphics();
//            g.drawImage(img, 0, 0, null);
//
//            this.image=bi;

//            Image image = Toolkit.getDefaultToolkit().getImage(new File(imageFile).getAbsolutePath());


//            Image image = Toolkit.getDefaultToolkit().createImage(new File(imageFile).getAbsolutePath());
//            this.image=image;
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
