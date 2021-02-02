package com.hyd.gtpb.archive;

import java.util.Arrays;

public class EntryPath {

  private final String[] paths;

  public EntryPath(String path) {
    this.paths = path.split("/");
  }

  public EntryPath(String[] paths) {
    this.paths = paths;
  }

  public String getPath() {
    return String.join("/", this.paths);
  }

  public String getName() {
    return paths[paths.length - 1];
  }

  public boolean hasParent() {
    return paths.length > 1;
  }

  public EntryPath getParent() {
    return new EntryPath(Arrays.copyOfRange(paths, 0, paths.length - 1));
  }
}
