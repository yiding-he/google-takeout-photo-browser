package com.hyd.gtpb.archive;

import javafx.scene.image.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class AlbumImage {

  private String zipFile;

  private String path;

  private Image thumbnail;
}
