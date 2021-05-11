package com.rudenko.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DbResultsConstants {

  protected static final NumberFormat CURRENCY_FORMAT = new DecimalFormat("'$'#,###");

  protected static final String PRODUCTION_COMPANY_NAME = "production_company_name";
  protected static final String YEAR = "year";
  protected static final String TOTAL_BUDGET = "total_budget";
  protected static final String TOTAL_REVENUE = "total_revenue";
  protected static final String PROFIT = "profit";
  protected static final String GENRE_NAME = "genre_name";
  protected static final String RELEASES = "releases";
  protected static final String AVERAGE_POPULARITY = "average_popularity";

  protected static final String PRODUCTION_COMPANY_BUDGET_YEAR_CSV = "production_company_budget_per_year.csv";
  protected static final String PRODUCTION_COMPANY_REVENUE_YEAR_CSV = "production_company_revenue_per_year.csv";
  protected static final String PRODUCTION_COMPANY_PROFIT_YEAR_CSV = "production_company_profit_per_year.csv";
  protected static final String PRODUCTION_COMPANY_RELEASES_GENRE_YEAR_CSV = "production_company_releases_per_genre_per_year.csv";
  protected static final String PRODUCTION_COMPANY_POPULARITY_YEAR_CSV = "production_company_popularity_per_year.csv";

  protected static final String GENRE_POPULAR_YEAR_CSV = "genre_most_popular_by_year.csv";
  protected static final String GENRE_BUDGET_YEAR_CSV = "genre_budget_per_year.csv";
  protected static final String GENRE_REVENUE_YEAR_CSV = "genre_revenue_per_year.csv";
  protected static final String GENRE_PROFIT_YEAR_CSV = "genre_profit_per_year.csv";

  protected static final String PRODUCTION_COMPANY_BUDGET_YEAR =
      "select production_company_name, date_part( 'year', release_date ) as year, sum( budget ) as total_budget "
          + "from movie "
          + "join movie_production_company using ( movie_id ) "
          + "join production_company using ( production_company_id ) "
          + "group by production_company_id, production_company_name, year "
          + "having sum( budget ) <> 0 "
          + "order by production_company_id, year";

  protected static final String PRODUCTION_COMPANY_REVENUE_YEAR =
      "select production_company_name, date_part( 'year', release_date ) as year, sum( revenue ) as total_revenue "
          + "from movie "
          + "join movie_production_company using ( movie_id ) "
          + "join production_company using ( production_company_id ) "
          + "group by production_company_id, production_company_name, year "
          + "having sum( revenue ) <> 0 "
          + "order by production_company_id, year";

  protected static final String PRODUCTION_COMPANY_PROFIT_YEAR =
      "select production_company_name, date_part( 'year', release_date ) as year, sum( revenue ) - sum( budget ) as profit "
          + "from movie "
          + "join movie_production_company using ( movie_id ) "
          + "  join production_company using ( production_company_id ) "
          + "group by production_company_id, production_company_name, year "
          + "having sum( budget ) <> 0 and sum( revenue ) <> 0 "
          + "order by production_company_id, year";

  protected static final String PRODUCTION_COMPANY_RELEASES_GENRE_YEAR =
      "select production_company_name, genre_name, date_part( 'year', release_date ) as year, count(*) as releases "
          + "from movie "
          + "join movie_production_company using ( movie_id ) "
          + "join production_company using ( production_company_id ) "
          + "join movie_genre using ( movie_id ) "
          + "join genre using ( genre_id ) "
          + "group by production_company_id, production_company_name, genre_name, year "
          + "order by production_company_id, year, releases desc";

  protected static final String PRODUCTION_COMPANY_POPULARITY_YEAR =
      "select production_company_name, date_part( 'year', release_date ) as year,  avg( popularity ) as average_popularity "
          + "from movie "
          + "join movie_production_company using ( movie_id ) "
          + "join production_company using ( production_company_id ) "
          + "group by production_company_id, production_company_name, year "
          + "having avg( popularity ) <> 0 "
          + "order by production_company_id, year";

  protected static final String GENRE_POPULAR_YEAR =
      "select year, genre_name, average_popularity from ( "
          + "select genre_name, year, average_popularity, row_number() over ( partition by year order by average_popularity desc ) as ranking "
          + "from ( "
          + "select genre_id, genre_name, date_part( 'year', release_date ) as year, avg( popularity ) as average_popularity "
          + "from movie "
          + "join movie_genre using ( movie_id ) "
          + "join genre using ( genre_id ) "
          + "group by genre_id, genre_name, year "
          + " having date_part( 'year', release_date ) is not null "
          + ") as average_pouplarity_by_genre "
          + ") as ranked "
          + "where ranking = 1 "
          + "order by year desc";

  protected static final String GENRE_BUDGET_YEAR =
      "select genre_name, date_part( 'year', release_date ) as year,  sum( budget ) as total_budget "
          + "from movie "
          + "join movie_genre using ( movie_id ) "
          + "join genre using ( genre_id ) "
          + "group by genre_id, genre_name, year "
          + "having sum( budget ) <> 0 "
          + "order by genre_id, year";

  protected static final String GENRE_REVENUE_YEAR =
      "select genre_name, date_part( 'year', release_date ) as year,  sum( revenue ) as total_revenue "
          + "from movie "
          + "join movie_genre using ( movie_id ) "
          + "join genre using ( genre_id ) "
          + "group by genre_id, genre_name, year "
          + "having sum( revenue ) <> 0 "
          + "order by genre_id, year";

  protected static final String GENRE_PROFIT_YEAR =
      "select genre_name, date_part( 'year', release_date ) as year,  sum( revenue ) - sum( budget ) as profit "
          + "from movie "
          + "join movie_genre using ( movie_id ) "
          + "join genre using ( genre_id ) "
          + "group by genre_id, genre_name, year "
          + "having sum( budget ) <> 0 and sum( revenue ) <> 0 "
          + "order by genre_id, year";

}
