package com.rudenko.util;

import com.rudenko.entities.Genre;
import com.rudenko.entities.Movie;
import com.rudenko.entities.ProductionCompany;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.time.LocalDate;
import java.util.Set;

public class DbWriterUtil {

  private static final String JDBC_DRIVER = "org.postgresql.Driver";
  private static final String DB_SERVER = "localhost";
  private static final String DB_PORT = "1357";
  private static final String DB_INSTANCE = "postgres";
  private static final String DB_URL =
      "jdbc:postgresql://" + DB_SERVER + ":" + DB_PORT + "/" + DB_INSTANCE;
  private static final String DB_USER = "postgres";
  private static final String DB_PASSWORD = "password";

  private static final int BATCH_SIZE = 50;

  private static final String TRUNCATE_TABLE =
      "truncate table movie_genre, movie_production_company, genre, production_company, movie";
  private static final String INSERT_GENRE = "insert into genre ( genre_id, genre_name ) values ( ?, ? )";
  private static final String INSERT_PRODUCTION_COMPANY =
      "insert into production_company ( production_company_id, production_company_name ) values ( ?, ? )";
  private static final String INSERT_MOVIE =
      "insert into movie ( movie_id, title, budget, popularity, release_date, revenue ) values ( ?, ?, ?, ?, ?, ? )";
  private static final String INSERT_M_GENRE = "insert into movie_genre ( movie_id, genre_id ) values ( ?, ? )";
  private static final String INSERT_M_PRODUCTION_COMPANY =
      "insert into movie_production_company ( movie_id, production_company_id ) values ( ?, ? )";

  private static Logger LOG = LogManager.getLogger(MethodHandles.lookup().lookupClass());

  public static void addRecordsToDb(Set[] input) {
    
    Set<Movie> movieSet = input[0];
    Set<Genre> genreSet = input[1];
    Set<ProductionCompany> productionCompanySet = input[2];

    cleanUpDb();

    addGenresToDb(genreSet);
    addProductionCompaniesToDb(productionCompanySet);
    addMoviesToDb(movieSet);

  }

  protected static Connection getPostgreSQLConnection() {

    Connection connection;
    try {
      Class.forName(JDBC_DRIVER).newInstance();
      connection = java.sql.DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
      connection.setAutoCommit(true);
    } catch (Exception e) {
      String msg = "Failed to create PosgreSQL DB connection";
      LOG.error(msg, e);
      throw new RuntimeException(msg, e);
    }

    return connection;
  }

  private static void cleanUpDb() {
    LOG.info("Cleaning up database - truncating all tables.");

    try (Connection connection = getPostgreSQLConnection()) {

      Statement statement = connection.createStatement();

      statement.execute(TRUNCATE_TABLE);
    } catch (SQLException e) {
      String message = "Failed to truncate tables";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }
  }

  private static void addGenresToDb(Set<Genre> genreList) {

    LOG.info("Populating GENRE table.");
    int added = 0;

    try (Connection connection = getPostgreSQLConnection()) {

      PreparedStatement statement = connection.prepareStatement(INSERT_GENRE);

      for (Genre genre : genreList) {
        statement.setLong(1, genre.getGenreId());
        statement.setString(2, genre.getGenreName());

        statement.addBatch();
        added++;

        if (added % BATCH_SIZE == 0) {
          statement.executeBatch();
          statement.clearBatch();
        }
      }

      statement.executeBatch();
    } catch (SQLException e) {
      String message = "Failed to insert batch into GENRE table.";
      LOG.error(message, e.getMessage());
      throw new RuntimeException(message, e);
    }

    LOG.info(added + " records added to GENRE table.");
  }

  private static void addProductionCompaniesToDb(Set<ProductionCompany> productionCompanyList) {

    LOG.info("Populating PRODUCTION_COMPANY table.");
    int added = 0;

    try (Connection connection = getPostgreSQLConnection()) {

      PreparedStatement statement = connection.prepareStatement(INSERT_PRODUCTION_COMPANY);

      for (ProductionCompany productionCompany : productionCompanyList) {
        statement.setLong(1, productionCompany.getProductionCompanyId());
        statement.setString(2, productionCompany.getProductionCompanyName());

        statement.addBatch();
        added++;

        if (added % BATCH_SIZE == 0) {
          statement.executeBatch();
          statement.clearBatch();
        }
      }

      statement.executeBatch();
    } catch (SQLException e) {
      String message = "Failed to insert batch into PRODUCTION_COMPANY table: ";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }

    LOG.info(added + " records added to PRODUCTION_COMPANY table.");
  }

  private static void addMoviesToDb(Set<Movie> movieList) {

    LOG.info("Populating MOVIE table, tying to GENRE and PRODUCTION_COMPANY");
    int added = 0;

    try (Connection connection = getPostgreSQLConnection()) {

      PreparedStatement movieStatement = connection.prepareStatement(INSERT_MOVIE);
      PreparedStatement mGenreStatement = connection.prepareStatement(INSERT_M_GENRE);
      PreparedStatement mProductionCompanyStatement = connection
          .prepareStatement(INSERT_M_PRODUCTION_COMPANY);

      for (Movie movie : movieList) {
        Long movieId = movie.getMovieId();
        LocalDate releaseDate = movie.getReleaseDate();

        movieStatement.setLong(1, movieId);
        movieStatement.setString(2, movie.getTitle());
        movieStatement.setLong(3, movie.getBudget());
        movieStatement.setDouble(4, movie.getPopularity());
        movieStatement.setDate(5, (releaseDate == null) ? null : Date.valueOf(releaseDate));
        movieStatement.setLong(6, movie.getRevenue());

        for (Long genreId : movie.getGenres()) {
          mGenreStatement.setLong(1, movieId);
          mGenreStatement.setLong(2, genreId);

          mGenreStatement.addBatch();
        }

        for (Long productionCompanyId : movie.getProductionCompanies()) {
          mProductionCompanyStatement.setLong(1, movieId);
          mProductionCompanyStatement.setLong(2, productionCompanyId);

          mProductionCompanyStatement.addBatch();
        }

        movieStatement.addBatch();
        added++;

        if (added % BATCH_SIZE == 0) {
          movieStatement.executeBatch();
          movieStatement.clearBatch();

          mGenreStatement.executeBatch();
          mGenreStatement.clearBatch();

          mProductionCompanyStatement.executeBatch();
          mProductionCompanyStatement.clearBatch();
        }
      }

      movieStatement.executeBatch();
    } catch (SQLException e) {
      String message = "Failed to insert record into MOVIE, MOVIE_GENRE, MOVIE_PRODUCTION_COMPANY tables: ";
      LOG.error(message, e);
      throw new RuntimeException(message, e);
    }

    LOG.info(added + " records added to MOVIE table and tied to GENRE and PRODUCTION_COMPANY.");
  }

}
