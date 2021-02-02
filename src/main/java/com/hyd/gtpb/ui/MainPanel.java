package com.hyd.gtpb.ui;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.app.AppThread;
import com.hyd.fx.builders.ButtonBuilder;
import com.hyd.fx.builders.ImageBuilder;
import com.hyd.fx.cells.ListCellFactory;
import com.hyd.fx.concurrency.BackgroundTask;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.enhancements.ListViewEnhancements;
import com.hyd.gtpb.archive.Album;
import com.hyd.gtpb.archive.GoogleTakeoutArchive;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.List;

import static com.hyd.fx.builders.LayoutBuilder.vbox;

public class MainPanel extends BorderPane {

  private final Label albumTitleLabel = new Label();

  private ListView<Album> albumListView = new ListView<>();

  private FlowPane photosPane = new FlowPane(10, 10);

  public MainPanel() {

    albumListView.setCellFactory(new ListCellFactory<Album>().withTextFunction(Album::getName));
    ListViewEnhancements.onSelectionChanged(albumListView, this::albumSelected);

    setCenter(ButtonBuilder.button("Open archive directory...", this::openArchiveDirectory));
  }

  private void albumSelected(Album album) {
    albumListView.setDisable(true);
    photosPane.getChildren().clear();

    BackgroundTask.runTask(() -> {
      List<Image> images = GoogleTakeoutArchive.getInstance().readImages(album);
      AppThread.runUIThread(() -> {
        for (Image image : images) {
          photosPane.getChildren().add(ImageBuilder.imageView(image, 300));
        }
        albumTitleLabel.setText(album.getName() + "(" + images.size() + " photos) : " + album.getZipFile().getName());
      });
    }).whenTaskFinish(() -> AppThread.runUIThread(
      () -> albumListView.setDisable(false)
    )).start();
  }

  private void openArchiveDirectory() {
    DirectoryChooser dc = new DirectoryChooser();
    dc.setInitialDirectory(new File("."));

    File folder = dc.showDialog(AppPrimaryStage.getPrimaryStage());
    if (folder != null) {
      try {
        GoogleTakeoutArchive.initInstance(folder);
        initMainUI();
      } catch (Exception e) {
        AlertDialog.error("Unable to read", e.getMessage());
      }
    }
  }

  private void initMainUI() {
    ScrollPane scrollPane = new ScrollPane(photosPane);
    scrollPane.setFitToWidth(true);

    setCenter(new SplitPane(
      albumListView,
      vbox(10, 10,
        albumTitleLabel,
        scrollPane
      )
    ));

    VBox.setVgrow(scrollPane, Priority.ALWAYS);
    albumListView.getItems().addAll(GoogleTakeoutArchive.getInstance().getAlbums());
  }
}
