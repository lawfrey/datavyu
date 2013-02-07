/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2009, 2010 Caprica Software Limited.
 */

package org.datavyu.plugins.vlcoop;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class ImagePane extends JComponent {

    public enum Mode {
        DEFAULT, CENTER, FIT
    }

    private Mode mode;

    private BufferedImage sourceImage;

    private float opacity;

    private BufferedImage image;

    private int lastWidth;

    private int lastHeight;

    public ImagePane(Mode mode, String imageFileName, float opacity) {
        this.mode = mode;
        this.opacity = opacity;
        newImage(imageFileName);
    }

    public void setImage(String imageFileName) {
        newImage(imageFileName);
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return image != null ? new Dimension(image.getWidth(), image.getHeight()) : super.getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        prepareImage();

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.black);
        g2.fill(getBounds());

        if(image != null) {
            int x = 0;
            int y = 0;

            if(mode != Mode.DEFAULT) {
                x = (getWidth() - image.getWidth()) / 2;
                y = (getHeight() - image.getHeight()) / 2;
            }

            Composite oldComposite = g2.getComposite();

            if(opacity != 1.0f) {
                g2.setComposite(AlphaComposite.SrcOver.derive(opacity));
            }
            g2.drawImage(image, null, x, y);

            g2.setComposite(oldComposite);
        }
    }

    @Override
    public boolean isOpaque() {
        return true;
    }

    private void newImage(String imageFileName) {
        image = null;
        if(imageFileName != null) {
            try {
                sourceImage = ImageIO.read(new File(imageFileName));
            }
            catch(IOException e) {
            }
        }
    }

    private void prepareImage() {
        if(lastWidth != getWidth() || lastHeight != getHeight()) {
            lastWidth = getWidth();
            lastHeight = getHeight();
            if(sourceImage != null) {
                switch(mode) {
                    case DEFAULT:
                    case CENTER:
                        image = sourceImage;
                        break;

                    case FIT:
                        image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                        Graphics2D g2 = image.createGraphics();
                        AffineTransform at = AffineTransform.getScaleInstance((double)image.getWidth() / sourceImage.getWidth(), (double)image.getHeight() / sourceImage.getHeight());
                        g2.drawRenderedImage(sourceImage, at);
                        g2.dispose();
                        break;
                }
            }
            else {
                image = null;
            }
        }
    }
}
