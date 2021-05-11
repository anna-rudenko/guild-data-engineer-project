package com.rudenko.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.rudenko.entities.Genre;
import com.rudenko.entities.Movie;
import com.rudenko.entities.ProductionCompany;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CsvParserUtil {

  private static Set<Movie> movieSet;
  private static Set<Genre> genreSet;
  private static Set<ProductionCompany> productionCompanySet;

  private static final int MOVIE_RECORD_LENGTH = 24;

  private static final int MOVIE_ID_POSITION = 5;
  private static final int TITLE_POSITION = 20;
  private static final int BUDGET_POSITION = 2;
  private static final int POPULARITY_POSITION = 10;
  private static final int RELEASE_DATE_POSITION = 14;
  private static final int REVENUE_POSITION = 15;
  private static final int GENRE_POSITION = 3;
  private static final int PRODUCTION_COMPANY_POSITION = 12;

  private static final String WINDOWS_1252_CHARACTERS = "[^\\x00-\\x7F\\xA0-\\xFF]"; //Windows-1252 only characters

  private static Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());

  public static Set[] parseCsv(String csvFile) {
    movieSet = new HashSet<>();
    genreSet = new HashSet<>();
    productionCompanySet = new HashSet<>();

    try (CSVReader reader = new CSVReader(new FileReader(csvFile))) {
      reader.readNext(); //Skip header

      List<String[]> rawRecords = reader.readAll();
      List<String[]> records = cleanUpRecords(rawRecords);

      parseRecord(records);

      Set[] parsedMovies = {movieSet, genreSet, productionCompanySet};
      return parsedMovies;

    } catch (FileNotFoundException e) {
      String message = "Failed to find file: " + csvFile;
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    } catch (IOException e) {
      String message = "Failed to read file: " + csvFile;
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    } catch (CsvException e) {
      String message = "Failed to parse csv: " + csvFile;
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  protected static List<String[]> cleanUpRecords(List<String[]> rawRecords) {
    List<String[]> records = rawRecords.stream()
        .filter(x -> x.length == MOVIE_RECORD_LENGTH)
        .collect(Collectors.toList());

    LOG.info("Dropped " + (rawRecords.size() - records.size()) + " records with incorrect length.");
    return records;
  }

  private static void parseRecord(List<String[]> records) {

    int recordsParsed = 0;

    for (String[] record : records) {
      Long movieId = Long.parseLong(record[MOVIE_ID_POSITION]);
      String title = record[TITLE_POSITION].replaceAll(WINDOWS_1252_CHARACTERS, "");
      Long budget = Long.parseLong(record[BUDGET_POSITION]);
      Double popularity = Double.parseDouble(record[POPULARITY_POSITION]);
      LocalDate releaseDate =
          (record[RELEASE_DATE_POSITION].isEmpty()) ? null
              : LocalDate.parse(record[RELEASE_DATE_POSITION]);
      Long revenue = Long.parseLong(record[REVENUE_POSITION]);

      String genreString = record[GENRE_POSITION];
      Set<Genre> genres = parseGenreArray(genreString);
      Long[] genreArray = genres.stream()
          .map(Genre::getGenreId)
          .toArray(Long[]::new);
      genreSet.addAll(genres);

      String productionCompanyString = record[PRODUCTION_COMPANY_POSITION];
      Set<ProductionCompany> productionCompanies = parseProductionCompanyArray(
          productionCompanyString);
      Long[] productionCompanyArray = productionCompanies.stream()
          .map(ProductionCompany::getProductionCompanyId)
          .toArray(Long[]::new);
      productionCompanySet.addAll(productionCompanies);

      movieSet.add(new Movie(movieId, title, budget, popularity, releaseDate, revenue, genreArray,
          productionCompanyArray));

      recordsParsed++;
    }

    LOG.info(recordsParsed + " records parsed from csv.");
  }

  protected static Set<Genre> parseGenreArray(String inputGenreString) {
    Set<Genre> genres = new HashSet<>();

    try {
      JSONArray jsonArray = new JSONArray(inputGenreString);

      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        Long id = jsonObject.getLong("id");
        String name = jsonObject.getString("name").replaceAll(WINDOWS_1252_CHARACTERS, "");
        genres.add(new Genre(id, name));
      }
    } catch (JSONException e) {
      String message = "Failed to parse genre array: \"" + inputGenreString + "\". Skipping.";
      LOG.info(message);
    }

    return genres;
  }

  protected static Set<ProductionCompany> parseProductionCompanyArray(
      String inputProdcutionCompanyString) {
    Set<ProductionCompany> productionCompanies = new HashSet<>();

    try {
      JSONArray jsonArray = new JSONArray(inputProdcutionCompanyString);
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject jsonObject = jsonArray.getJSONObject(i);
        Long id = jsonObject.getLong("id");
        String name = jsonObject.getString("name").replaceAll(WINDOWS_1252_CHARACTERS, "");
        productionCompanies.add(new ProductionCompany(id, name));
      }
    } catch (JSONException e) {
      String message = "Failed to parse production company array: \"" + inputProdcutionCompanyString
          + "\". Skipping.";
      LOG.info(message);
    }

    return productionCompanies;
  }
}
