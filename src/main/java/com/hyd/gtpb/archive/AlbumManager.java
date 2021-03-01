package com.hyd.gtpb.archive;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;

public class AlbumManager {

  private Map<String, Album> albums = new HashMap<>();

  public List<Album> getAlbums() {
    return new ArrayList<>(albums.values());
  }

  public void addImage(File zipFile, ZipEntry entry) {
    EntryPath parent = new EntryPath(entry.getName()).getParent();
    String albumName = parent.getName();

    albums.computeIfAbsent(albumName, __ -> new Album(zipFile, parent)).addImageCount();
  }
}
