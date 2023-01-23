package org.homs.houmls.shape.impl.box;

import org.homs.houmls.PropsParser;
import org.homs.houmls.shape.Shape;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.homs.houmls.LookAndFeel.basicStroke;

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
        if (props.containsKey("image")) {
            var imageFile = props.get("image");
            try {
                BufferedImage img = ImageIO.read(new File(imageFile));
                this.image = img;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        g2.setColor(backgroundColor);
        g2.fillRect(ix, iy, iwidth, iheight);

        g2.setStroke(basicStroke);
        g2.setColor(Color.BLACK);

        if (this.image != null) {
            g2.drawImage(image, ix, iy, iwidth, iheight, null);
        }
    }
}
