package com.hyd.gtpb.archive;

import com.hyd.fx.dialog.AlertDialog;
import com.hyd.fx.system.ZipFileReader;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

  private List<Album> albums = new ArrayList<>();

  public List<Album> getAlbums() {
    return albums;
  }

  public GoogleTakeoutArchive(File archiveFolder) {
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
    albums.sort(Comparator.comparing(Album::getName));
  }

  private void scanArchive(File zipFile) throws IOException {
    try (ZipFileReader reader = new ZipFileReader(zipFile)) {
      reader.readZipEntries("**/元数据.json", entry -> {
        albums.add(new Album(zipFile, new EntryPath(entry.getName()).getParent()));
      });
    }
  }

  public List<Image> readImages(Album album) {
    List<Image> images = new ArrayList<>();

    try (ZipFileReader reader = new ZipFileReader(album.getZipFile())) {
      reader.readZipEntries(album.getEntryPath().getPath() + "/*", zipEntry -> {
        if (!zipEntry.isDirectory() && (
          zipEntry.getName().endsWith(".jpg") || zipEntry.getName().endsWith(".png") || zipEntry.getName().endsWith(".gif")
        )) {
          try {
            images.add(new Image(reader.getInputStream(zipEntry)));
          } catch (IOException e) {
            log.error("", e);
          }
        }
      });
      return images;
    } catch (IOException e) {
      AlertDialog.error("Error reading archive file '" + album.getZipFile().getName() + "'", e);
      return Collections.emptyList();
    }
  }
}
