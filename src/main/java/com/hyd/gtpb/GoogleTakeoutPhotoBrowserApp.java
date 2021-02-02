package com.hyd.gtpb;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.gtpb.ui.MainPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GoogleTakeoutPhotoBrowserApp extends Application {

  @Override
  public void start(Stage stage) throws Exception {
    AppPrimaryStage.setPrimaryStage(stage);

    stage.setTitle("Google Takeout Photo Browser");
    stage.setScene(new Scene(new MainPanel(), 800, 600));
    stage.show();
  }
}
