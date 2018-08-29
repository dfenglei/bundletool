/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com.android.tools.build.bundletool.testing;

import com.android.bundle.Commands.BuildApksResult;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

/** Utility methods to manipulate APK Sets. */
public final class ApkSetUtils {

  public static BuildApksResult parseTocFromFile(File file) throws Exception {
    try (FileInputStream inputStream = new FileInputStream(file)) {
      BuildApksResult result = BuildApksResult.parseFrom(inputStream);
      return result;
    }
  }

  public static BuildApksResult extractTocFromApkSetFile(ZipFile apkSetFile, Path outputDirPath)
      throws Exception {
    return parseTocFromFile(extractFromApkSetFile(apkSetFile, "toc.pb", outputDirPath));
  }

  public static File extractFromApkSetFile(ZipFile apkSetFile, String path, Path outputDirPath)
      throws Exception {
    File extractedFile = outputDirPath.resolve(path).toFile();
    Files.createDirectories(extractedFile.toPath().getParent());
    try (FileOutputStream fileOutputStream = new FileOutputStream(extractedFile)) {
      ByteStreams.copy(apkSetFile.getInputStream(apkSetFile.getEntry(path)), fileOutputStream);
    }
    return extractedFile;
  }

  private ApkSetUtils() {}
}
