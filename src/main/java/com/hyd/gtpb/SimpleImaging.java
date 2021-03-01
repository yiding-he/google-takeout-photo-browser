package com.hyd.gtpb;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class SimpleImaging {

  public static Image scale(Image origin, int maxWidth, int maxHeight) {
    BufferedImage bi = SwingFXUtils.fromFXImage(origin, null);
    if (bi == null) {
      return null;
    }
    double ratio = 1 / Math.max(((double) bi.getWidth()) / maxWidth, ((double) bi.getHeight()) / maxHeight);
    AffineTransform at = AffineTransform.getScaleInstance(ratio, ratio);
    BufferedImage result = new BufferedImage(
      (int) (bi.getWidth() * ratio), (int) (bi.getHeight() * ratio), BufferedImage.TYPE_INT_RGB
    );
    result.createGraphics().drawRenderedImage(bi, at);
    return SwingFXUtils.toFXImage(result, null);
  }
}
