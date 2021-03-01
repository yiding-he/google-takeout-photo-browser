package com.hyd.gtpb.ui;

import com.hyd.fx.app.AppPrimaryStage;
import com.hyd.fx.app.AppThread;
import com.hyd.fx.builders.ButtonBuilder;
import com.hyd.fx.builders.ImageBuilder;
import com.hyd.fx.builders.MenuBuilder;
import com.hyd.fx.cells.ListCellFactory;
import com.hyd.fx.concurrency.BackgroundTask;
import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.dialog.BasicDialog;
import com.hyd.fx.dialog.DialogBuilder;
import com.hyd.fx.enhancements.ListViewEnhancements;
import com.hyd.fx.system.ClipboardHelper;
import com.hyd.fx.system.ZipFileReader;
import com.hyd.gtpb.Value;
import com.hyd.gtpb.archive.Album;
import com.hyd.gtpb.archive.AlbumImage;
import com.hyd.gtpb.archive.GoogleTakeoutArchive;
import javafx.geometry.BoundingBox;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import static com.hyd.fx.builders.LayoutBuilder.vbox;

@Slf4j
public class MainPanel extends BorderPane {

  private final Label albumTitleLabel = new Label();

  private ListView<Album> albumListView = new ListView<>();

  private FlowPane photosPane = new FlowPane(10, 10);

  public MainPanel() {

    albumListView.setCellFactory(new ListCellFactory<Album>()
      .withTextFunction(album -> album.getName() + "(" + album.getImageCount() + ")"));

    albumListView.setMaxWidth(300);
    ListViewEnhancements.onSelectionChanged(albumListView, this::albumSelected);
    photosPane.setPadding(new Insets(10));
    setCenter(ButtonBuilder.button("Open archive directory...", this::openArchiveDirectory));
  }

  private void albumSelected(Album album) {
    albumListView.setDisable(true);
    photosPane.getChildren().clear();

    BackgroundTask.runTask(() -> {
      GoogleTakeoutArchive.getInstance().forEachImage(album, image -> AppThread.runUIThread(() -> {
        photosPane.getChildren().add(buildThumbnailImageView(image));
      }));

      AppThread.runUIThread(() -> albumTitleLabel.setText(album.getName() +
        "(" + album.getImageCount() + " photos) : " + album.getZipFile().getName()));

    }).whenTaskFinish(() -> AppThread.runUIThread(
      () -> albumListView.setDisable(false)
    )).start();
  }

  private ImageView buildThumbnailImageView(AlbumImage albumImage) {
    ImageView imageView = ImageBuilder.imageView(albumImage.getThumbnail(), 300);
    imageView.setOnMouseClicked(mouseEvent -> {
      if (mouseEvent.getClickCount() == 2) {
        showImage(albumImage.getZipFile(), albumImage.getPath());
      }
    });
    return imageView;
  }

  private Value<BoundingBox> imageDialogBounds = Value.empty();

  private void showImage(String zipFile, String path) {

    final ImageView imageView = new ImageView();
    imageView.setOnContextMenuRequested(e -> {
      ImageView iv = (ImageView) e.getSource();
      MenuBuilder.contextMenu(
        MenuBuilder.menuItem("复制", () -> ClipboardHelper.putImage(iv.getImage()))
      ).show(iv, e.getScreenX(), e.getScreenY());
    });

    ScrollPane sp = new ScrollPane(imageView);
    sp.setPannable(true);

    BorderPane root = new BorderPane(sp);
    root.setPadding(new Insets(10));
    root.setPrefWidth(800);
    root.setPrefHeight(600);

    new DialogBuilder().buttons(ButtonType.CLOSE)
      .onButtonClicked(ButtonType.CLOSE, e -> ((Stage) ((Button) e.getSource()).getScene().getWindow()).close())
      .body(root)
      .resizable(true)
      .onStageShown(e -> {
        BasicDialog s = (BasicDialog) e.getSource();
        s.setOnCloseRequest(r -> imageDialogBounds.set(
          new BoundingBox(s.getX(), s.getY(), s.getWidth(), s.getHeight())
        ));
        if (!imageDialogBounds.isEmpty()) {
          s.setX(imageDialogBounds.get().getMinX());
          s.setY(imageDialogBounds.get().getMinY());
          s.setWidth(imageDialogBounds.get().getWidth());
          s.setHeight(imageDialogBounds.get().getHeight());
        }
        new Thread(() -> {
          try {
            Image image = new Image(new ZipFileReader(zipFile).readZipEntryStream(path));
            AppThread.runUIThread(() -> imageView.setImage(image));
          } catch (IOException ee) {
            log.error("", ee);
          }
        }).start();
      })
      .build().show();
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
    List<Album> albums = GoogleTakeoutArchive.getInstance().getAlbums();
    albums.sort(Comparator.comparing(Album::getName));

    albumListView.getItems().addAll(albums);
  }
}
