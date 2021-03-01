package com.hyd.gtpb;

import com.hyd.fx.builders.ImageBuilder;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SimpleImagingTest extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    Image image = new Image(SimpleImagingTest.class.getResourceAsStream("/1.jpg"));
    Image scaled = SimpleImaging.scale(image, 400, 400);
    primaryStage.setScene(new Scene(new BorderPane(ImageBuilder.imageView(scaled)), 500, 400));
    primaryStage.show();
  }
}
