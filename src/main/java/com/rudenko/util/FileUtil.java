package com.rudenko.util;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.S3Object;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {

  private static Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());

  public static void downloadFile(String inputFilename, String outputFilename) {
    try {

      InputStream reader = new BufferedInputStream(
          getAmazonS3Object(inputFilename).getObjectContent());
      OutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFilename));

      LOG.info("Pulling file: " + inputFilename);

      int read;
      while ((read = reader.read()) != -1) {
        writer.write(read);
      }

      LOG.info("File downloaded to: " + outputFilename);

      writer.flush();
      writer.close();
      reader.close();
    } catch (IOException e) {
      String message = "Failed reading or writing file.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  public static void unzipDirectory(String inputArchive, String outputDirectory) {
    try {
      ZipFile zipFile = new ZipFile(inputArchive);

      Enumeration zipEntries = zipFile.entries();

      File fOutputDirectory = new File(outputDirectory);
      if (!fOutputDirectory.exists()) {
        LOG.info("Creating output directory: " + fOutputDirectory);
        fOutputDirectory.mkdir();
      }

      while (zipEntries.hasMoreElements()) {
        ZipEntry zipEntry = (ZipEntry) zipEntries.nextElement();
        if (zipEntry.isDirectory()) {
          String newDirectory = fOutputDirectory + File.separator + zipEntry.getName();
          LOG.info("Extracting directory: " + newDirectory);

          new File(newDirectory).mkdir();
          continue;
        }

        String outputFile = fOutputDirectory + File.separator + zipEntry.getName();
        LOG.info("Extracting file: " + outputFile);

        InputStream reader = zipFile.getInputStream(zipEntry);
        OutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));

        int read;
        while ((read = reader.read()) != -1) {
          writer.write(read);
        }

        writer.flush();
        writer.close();
        reader.close();
      }
    } catch (IOException e) {
      String message = "Failed reading or writing file.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static S3Object getAmazonS3Object(String inputFilename) {
    try {
      AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_2).build();
      AmazonS3URI s3URI = new AmazonS3URI(new URI(inputFilename));
      return s3Client.getObject(s3URI.getBucket(), s3URI.getKey());
    } catch (URISyntaxException e) {
      String message = "Failed to read URI.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }
}
