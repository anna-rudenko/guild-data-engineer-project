package com.rudenko.util;

import com.opencsv.CSVWriter;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DbResultsUtil {

  private static Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());

  public static void writeResults(String outputLocation) {

    File fOutputLocation = new File(outputLocation);
    if (!fOutputLocation.exists()) {
      fOutputLocation.mkdir();
    }

    write(DbResultsConstants.PRODUCTION_COMPANY_BUDGET_YEAR_CSV, outputLocation,
        getProductionCompanyBudgetYear());
    write(DbResultsConstants.PRODUCTION_COMPANY_REVENUE_YEAR_CSV, outputLocation,
        getProductionCompanyRevenueYear());
    write(DbResultsConstants.PRODUCTION_COMPANY_PROFIT_YEAR_CSV, outputLocation,
        getProductionCompanyProfitYear());
    write(DbResultsConstants.PRODUCTION_COMPANY_RELEASES_GENRE_YEAR_CSV, outputLocation,
        getProductionCompanyReleasesGenreYear());
    write(DbResultsConstants.PRODUCTION_COMPANY_POPULARITY_YEAR_CSV, outputLocation,
        getProductionCompanyPopularityYear());

    write(DbResultsConstants.GENRE_POPULAR_YEAR_CSV, outputLocation, getGenrePopularYear());
    write(DbResultsConstants.GENRE_BUDGET_YEAR_CSV, outputLocation, getGenreBudgetYear());
    write(DbResultsConstants.GENRE_REVENUE_YEAR_CSV, outputLocation, getGenreRevenueYear());
    write(DbResultsConstants.GENRE_PROFIT_YEAR_CSV, outputLocation, getGenreProfitYear());
  }

  private static void write(String outputFilename, String outputLocation, List<String[]> output) {

    LOG.info("Working on " + outputFilename.split("\\.")[0].replaceAll("_", " "));
    String outputFilepath = outputLocation + File.separator + outputFilename;

    try (CSVWriter writer = new CSVWriter(new FileWriter(outputFilepath))) {
      writer.writeAll(output);
    } catch (IOException e) {
      String message = "Failed to write to file: " + outputFilename;
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }

    LOG.info("Results written to file: " + outputFilename);
  }

  private static ResultSet getDbResult(String query) {
    try (Connection connection = DbWriterUtil.getPostgreSQLConnection()) {
      PreparedStatement statement = connection.prepareStatement(query);
      return statement.executeQuery();
    } catch (SQLException e) {
      String message = "Failed to pull from database.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static List<String[]> getProductionCompanyBudgetYear() {

    try (ResultSet result = getDbResult(DbResultsConstants.PRODUCTION_COMPANY_BUDGET_YEAR)) {

      List<String[]> output = new ArrayList<>();
      String[] header = {"Production Company", "Year", "Budget"};
      output.add(header);

      while (result.next()) {
        String productionCompany = result.getString(DbResultsConstants.PRODUCTION_COMPANY_NAME);
        Long year = result.getLong(DbResultsConstants.YEAR);
        Long budget = result.getLong(DbResultsConstants.TOTAL_BUDGET);

        String[] nextLine = {productionCompany, year.toString(),
            DbResultsConstants.CURRENCY_FORMAT.format(budget)};

        output.add(nextLine);
      }

      return output;
    } catch (SQLException e) {
      String message = "Failed to parse database result.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static List<String[]> getProductionCompanyRevenueYear() {

    try (ResultSet result = getDbResult(DbResultsConstants.PRODUCTION_COMPANY_REVENUE_YEAR)) {

      List<String[]> output = new ArrayList<>();
      String[] header = {"Production Company", "Year", "Revenue"};
      output.add(header);

      while (result.next()) {
        String productionCompany = result.getString(DbResultsConstants.PRODUCTION_COMPANY_NAME);
        Long year = result.getLong(DbResultsConstants.YEAR);
        Long revenue = result.getLong(DbResultsConstants.TOTAL_REVENUE);

        String[] nextLine = {productionCompany, year.toString(),
            DbResultsConstants.CURRENCY_FORMAT.format(revenue)};
        output.add(nextLine);
      }

      return output;
    } catch (SQLException e) {
      String message = "Failed to parse database result.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static List<String[]> getProductionCompanyProfitYear() {

    try (ResultSet result = getDbResult(DbResultsConstants.PRODUCTION_COMPANY_PROFIT_YEAR)) {

      List<String[]> output = new ArrayList<>();
      String[] header = {"Production Company", "Year", "Profit"};
      output.add(header);

      while (result.next()) {
        String productionCompany = result.getString(DbResultsConstants.PRODUCTION_COMPANY_NAME);
        Long year = result.getLong(DbResultsConstants.YEAR);
        Long profit = result.getLong(DbResultsConstants.PROFIT);

        String[] nextLine = {productionCompany, year.toString(),
            DbResultsConstants.CURRENCY_FORMAT.format(profit)};
        output.add(nextLine);
      }

      return output;
    } catch (SQLException e) {
      String message = "Failed to pull from database.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static List<String[]> getProductionCompanyReleasesGenreYear() {

    try (ResultSet result = getDbResult(
        DbResultsConstants.PRODUCTION_COMPANY_RELEASES_GENRE_YEAR)) {

      List<String[]> output = new ArrayList<>();
      String[] header = {"Production Company", "Year", "Genre", "Releases"};
      output.add(header);

      while (result.next()) {
        String productionCompany = result.getString(DbResultsConstants.PRODUCTION_COMPANY_NAME);
        Long year = result.getLong(DbResultsConstants.YEAR);
        String genre = result.getString(DbResultsConstants.GENRE_NAME);
        Long releases = result.getLong(DbResultsConstants.RELEASES);

        String[] nextLine = {productionCompany, year.toString(), genre, releases.toString()};
        output.add(nextLine);
      }

      return output;
    } catch (SQLException e) {
      String message = "Failed to pull from database.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static List<String[]> getProductionCompanyPopularityYear() {

    try (ResultSet result = getDbResult(DbResultsConstants.PRODUCTION_COMPANY_POPULARITY_YEAR)) {

      List<String[]> output = new ArrayList<>();
      String[] header = {"Production Company", "Year", "Average Popularity"};
      output.add(header);

      while (result.next()) {
        String productionCompany = result.getString(DbResultsConstants.PRODUCTION_COMPANY_NAME);
        Long year = result.getLong(DbResultsConstants.YEAR);
        Double averagePopularity = result.getDouble(DbResultsConstants.AVERAGE_POPULARITY);

        String[] nextLine = {productionCompany, year.toString(),
            String.format("%.2f", averagePopularity)};
        output.add(nextLine);
      }

      return output;
    } catch (SQLException e) {
      String message = "Failed to pull from database.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static List<String[]> getGenrePopularYear() {

    try (ResultSet result = getDbResult(DbResultsConstants.GENRE_POPULAR_YEAR)) {

      List<String[]> output = new ArrayList<>();
      String[] header = {"Year", "Genre", "Popularity"};
      output.add(header);

      while (result.next()) {
        Long year = result.getLong(DbResultsConstants.YEAR);
        String genre = result.getString(DbResultsConstants.GENRE_NAME);
        Double popularity = result.getDouble(DbResultsConstants.AVERAGE_POPULARITY);

        String[] nextLine = {year.toString(), genre, String.format("%.2f", popularity)};
        output.add(nextLine);
      }

      return output;
    } catch (SQLException e) {
      String message = "Failed to pull from database.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static List<String[]> getGenreBudgetYear() {

    try (ResultSet result = getDbResult(DbResultsConstants.GENRE_BUDGET_YEAR)) {

      List<String[]> output = new ArrayList<>();
      String[] header = {"Genre", "Year", "Budget"};
      output.add(header);

      while (result.next()) {
        String genre = result.getString(DbResultsConstants.GENRE_NAME);
        Long year = result.getLong(DbResultsConstants.YEAR);
        Long budget = result.getLong(DbResultsConstants.TOTAL_BUDGET);

        String[] nextLine = {genre, year.toString(),
            DbResultsConstants.CURRENCY_FORMAT.format(budget)};
        output.add(nextLine);
      }

      return output;
    } catch (SQLException e) {
      String message = "Failed to pull from database.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static List<String[]> getGenreRevenueYear() {

    try (ResultSet result = getDbResult(DbResultsConstants.GENRE_REVENUE_YEAR)) {

      List<String[]> output = new ArrayList<>();
      String[] header = {"Genre", "Year", "Revenue"};
      output.add(header);

      while (result.next()) {
        String genre = result.getString(DbResultsConstants.GENRE_NAME);
        Long year = result.getLong(DbResultsConstants.YEAR);
        Long revenue = result.getLong(DbResultsConstants.TOTAL_REVENUE);

        String[] nextLine = {genre, year.toString(),
            DbResultsConstants.CURRENCY_FORMAT.format(revenue)};
        output.add(nextLine);
      }

      return output;
    } catch (SQLException e) {
      String message = "Failed to pull from database.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static List<String[]> getGenreProfitYear() {

    try (ResultSet result = getDbResult(DbResultsConstants.GENRE_PROFIT_YEAR)) {

      List<String[]> output = new ArrayList<>();
      String[] header = {"Genre", "Year", "Profit"};
      output.add(header);

      while (result.next()) {
        String genre = result.getString(DbResultsConstants.GENRE_NAME);
        Long year = result.getLong(DbResultsConstants.YEAR);
        Long profit = result.getLong(DbResultsConstants.PROFIT);

        String[] nextLine = {genre, year.toString(),
            DbResultsConstants.CURRENCY_FORMAT.format(profit)};
        output.add(nextLine);
      }

      return output;
    } catch (SQLException e) {
      String message = "Failed to pull from database.";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }
}
