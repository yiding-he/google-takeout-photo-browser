package com.hyd.gtpb.archive;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.system.ZipFileReader;
import com.hyd.gtpb.SimpleImaging;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class GoogleTakeoutArchive {

  private static GoogleTakeoutArchive instance;

  public static GoogleTakeoutArchive getInstance() {
    return instance;
  }

  public static void initInstance(File archiveFolder) {
    instance = new GoogleTakeoutArchive(archiveFolder);
  }

  private File archiveFolder;

  private AlbumManager albumManager;

  public List<Album> getAlbums() {
    return albumManager.getAlbums();
  }

  public GoogleTakeoutArchive(File archiveFolder) {
    this.albumManager = new AlbumManager();

    File[] zipFiles = archiveFolder.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".zip"));
    if (zipFiles == null || zipFiles.length == 0) {
      throw new IllegalStateException("Folder '" + archiveFolder.getAbsolutePath() + "' does not contain any archive files.");
    }

    log.info("Found {} zip files.", zipFiles.length);
    try {
      scanArchives(zipFiles);
    } catch (IOException e) {
      AlertDialog.error("Error reading zip files", e);
    }
  }

  private void scanArchives(File[] zipFiles) throws IOException {
    for (File zipFile : zipFiles) {
      scanArchive(zipFile);
    }
  }

  private void scanArchive(File zipFile) throws IOException {
    try (ZipFileReader reader = new ZipFileReader(zipFile)) {
      reader.readZipEntries("**/*.jpg", entry -> {
        albumManager.addImage(zipFile, entry);
      });
    }
  }

  public void forEachImage(Album album, Consumer<AlbumImage> imageConsumer) {
    try (ZipFileReader reader = new ZipFileReader(album.getZipFile())) {
      reader.readZipEntries(album.getEntryPath().getPath() + "/*", zipEntry -> {
        if (!zipEntry.isDirectory() && (zipEntry.getName().endsWith(".jpg"))) {
          try {
            String zipPath = album.getZipFile().getAbsolutePath();
            Image image = new Image(reader.getInputStream(zipEntry));
            Image thumbnail = SimpleImaging.scale(image, 300, 300);
            image = null;

            if (thumbnail != null) {
              imageConsumer.accept(new AlbumImage(
                zipPath, zipEntry.getName(), thumbnail));
            } else {
              log.error("Failed creating thumbnail for {}!{}", zipPath, zipEntry.getName());
            }
          } catch (IOException e) {
            log.error("", e);
          }
        }
      });
    } catch (IOException e) {
      AlertDialog.error("Error reading archive file '" + album.getZipFile().getName() + "'", e);
    }
  }
}
