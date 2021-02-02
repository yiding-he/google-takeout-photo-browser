package com.hyd.gtpb.archive;

import java.io.File;

public class Album {

  private final File zipFile;

  private final EntryPath entryPath;

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
}
