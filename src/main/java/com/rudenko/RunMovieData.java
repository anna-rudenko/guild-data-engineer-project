package com.rudenko;

import com.rudenko.util.CsvParserUtil;
import com.rudenko.util.DbResultsUtil;
import com.rudenko.util.DbWriterUtil;
import com.rudenko.util.FileUtil;
import java.io.File;
import java.util.Set;

public class RunMovieData {

  private static final String DEFAULT_ENDPOINT = "s3://com.guild.us-west-2.public-data/project-data/the-movies-dataset.zip";
  private static final String ZIP_FILE = "the-movies-dataset.zip";
  private static final String WORKING_DIRECTORY = "working_directory";
  private static final String MOVIE_FILENAME = "movies_metadata.csv";
  private static final String RESULTS_DIRECTORY = "movie_results";

  public static void main(String[] args) {

    String inputEndpoint = (args.length == 0) ? DEFAULT_ENDPOINT : args[0];

    execute(inputEndpoint);
  }

  public static void execute(String inputEndpoint) {

    FileUtil.downloadFile(inputEndpoint, ZIP_FILE);
    FileUtil.unzipDirectory(ZIP_FILE, WORKING_DIRECTORY);

    Set[] parsedMovies = CsvParserUtil
        .parseCsv(WORKING_DIRECTORY + File.separator + MOVIE_FILENAME);
    DbWriterUtil.addRecordsToDb(parsedMovies);

    DbResultsUtil.writeResults(WORKING_DIRECTORY + File.separator + RESULTS_DIRECTORY);
  }
}
