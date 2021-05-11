package com.rudenko.util;

import com.rudenko.entities.Genre;
import com.rudenko.entities.ProductionCompany;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CsvParserTest {

  private List<String[]> inputRecordsBadLength = new ArrayList<>();
  private List<String[]> expectedCleanedRecords = new ArrayList<>();

  private Set<Genre> expectedGenreResult = new HashSet<>();

  private Set<ProductionCompany> expectedProductionCompanyResult = new HashSet<>();

  @BeforeClass
  public void setUp() {
    String[] inputRecord0 = {"False", "\"{'id': 10194, 'name': 'Toy Story Collection'}\"",
        "30000000", "\"[{'id': 16, 'name': 'Animation'}]\"", "http://toystory.disney.com/toy-story",
        "862", "tt0114709", "en", "Toy Story", "\"Woody and Buzz Lightyear.\"", "21.946943",
        "/rhIRbceoE9lR4veEXuwCC2wARtG.jpg", "\"[{'name': 'Pixar Animation Studios', 'id': 3}]\"",
        "\"[{'iso_3166_1': 'US', 'name': 'United States of America'}]\"", "1995-10-30", "373554033",
        "81.0", "\"[{'iso_639_1': 'en', 'name': 'English'}]\"", "Released", "", "Toy Story",
        "False", "7.7", "5415"};
    String[] inputRecord1 = {"False", "", "65000000", "\"[{'id': 12, 'name': 'Adventure'}]\"", "",
        "8844", "tt0113497", "en", "Jumanji", "\"Siblings Judy and Peter.\"", "17.015539",
        "/vzmL6fP7aPKNKPRTFnZmiUfciyV.jpg", "\"[{'name': 'TriStar Pictures', 'id': 559}]\"",
        "\"[{'iso_3166_1': 'US', 'name': 'United States of America'}]\"", "1995-12-15", "262797249",
        "104.0", "\"[{'iso_639_1': 'en', 'name': 'English'}]\"", "Released",
        "Roll the dice and unleash the excitement!", "Jumanji", "False", "6.9", "2413",
        "bad"};
    String[] inputRecord2 = {"False", "\"{'id': 645, 'name': 'James Bond Collection'}\"",
        "58000000", "\"[{'id': 28, 'name': 'Action'}]\"",
        "http://www.mgm.com/view/movie/757/Goldeneye/", "710", "tt0113189", "en", "GoldenEye",
        "James Bond.", "14.686036", "/5c0ovjT41KnYIHYuF4AWsTe3sKh.jpg",
        "\"[{'name': 'United Artists', 'id': 60}]\"",
        "\"[{'iso_3166_1': 'GB', 'name': 'United Kingdom'}]\"", "1995-11-16", "352194034", "130.0",
        "\"[{'iso_639_1': 'en', 'name': 'English'}, {'iso_639_1': 'ru', 'name': 'Pусский'}]\"",
        "Released", "No limits. No fears. No substitutes.", "GoldenEye", "False", "6.6", "1194"};

    inputRecordsBadLength.add(inputRecord0);
    inputRecordsBadLength.add(inputRecord1);
    inputRecordsBadLength.add(inputRecord2);

    expectedCleanedRecords.add(inputRecord0);
    expectedCleanedRecords.add(inputRecord2);

    Genre genre0 = new Genre(35L, "Comedy");
    Genre genre1 = new Genre(10749L, "Romance");

    expectedGenreResult.add(genre0);
    expectedGenreResult.add(genre1);

    ProductionCompany productionCompany0 = new ProductionCompany(5L, "Columbia Pictures");
    ProductionCompany productionCompany1 = new ProductionCompany(97L, "Castle Rock Entertainment");
    ProductionCompany productionCompany2 = new ProductionCompany(6368L, "Enigma Pictures");

    expectedProductionCompanyResult.add(productionCompany0);
    expectedProductionCompanyResult.add(productionCompany1);
    expectedProductionCompanyResult.add(productionCompany2);
  }

  @Test
  public void testCleanUpRecords() {

    List<String[]> actualCleanedRecords = CsvParserUtil.cleanUpRecords(inputRecordsBadLength);

    Assert.assertEquals(actualCleanedRecords, expectedCleanedRecords);
  }

  @Test
  public void testParseGenreArray() {

    String inputGenreString = "[{'id': 10749, 'name': 'Romance'}, {'id': 35, 'name': 'Comedy'}]";
    Set<Genre> actualGenreResult = CsvParserUtil.parseGenreArray(inputGenreString);

    Assert.assertEquals(actualGenreResult, expectedGenreResult);
  }

  @Test
  public void testParseGenreArrayBad() {

    String inputGenreString = "[{'id': 10749, 'name': 'Romance'";
    Set<Genre> actualGenreResult = CsvParserUtil.parseGenreArray(inputGenreString);

    Assert.assertTrue(actualGenreResult.isEmpty());
  }

  @Test
  public void testParseProductionCompanyArray() {

    String inputProductionCompanyString = "[{'name': 'Columbia Pictures', 'id': 5}, "
        + "{'name': 'Castle Rock Entertainment', 'id': 97}, "
        + "{'name': 'Enigma Pictures', 'id': 6368}]";
    Set<ProductionCompany> actualProductionCompanyResult = CsvParserUtil
        .parseProductionCompanyArray(inputProductionCompanyString);

    Assert.assertEquals(actualProductionCompanyResult, expectedProductionCompanyResult);
  }

  @Test
  public void testParseProductionCompanyArrayBad() {

    String inputProductionCompanyString = "['name': 'Columbia Pictures', 'id': 5}]";
    Set<ProductionCompany> actualProductionCompanyResult = CsvParserUtil
        .parseProductionCompanyArray(inputProductionCompanyString);

    Assert.assertTrue(actualProductionCompanyResult.isEmpty());
  }
}
