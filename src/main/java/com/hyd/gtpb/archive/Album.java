package com.hyd.gtpb.archive;

import java.io.File;

public class Album {

  private final File zipFile;

  private final EntryPath entryPath;

  private int imageCount;

  public Album(File zipFile, EntryPath entryPath) {
    this.zipFile = zipFile;
    this.entryPath = entryPath;
  }

  public String getName() {
    return this.entryPath.getName();
  }

  public File getZipFile() {
    return zipFile;
  }

  public EntryPath getEntryPath() {
    return entryPath;
  }

  public void addImageCount() {
    this.imageCount += 1;
  }

  public int getImageCount() {
    return imageCount;
  }
}
