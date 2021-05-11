package com.rudenko.entities;

import java.time.LocalDate;
import java.util.Objects;

public class Movie {

  private Long movieId;
  private String title;
  private Long budget;
  private Double popularity;
  private LocalDate releaseDate;
  private Long revenue;
  private Long[] genres;
  private Long[] productionCompanies;

  public Movie(Long movieId, String title, Long budget, Double popularity, LocalDate releaseDate,
      Long revenue, Long[] genres,
      Long[] productionCompanies) {
    this.movieId = movieId;
    this.title = title;
    this.budget = budget;
    this.popularity = popularity;
    this.releaseDate = releaseDate;
    this.revenue = revenue;
    this.genres = genres;
    this.productionCompanies = productionCompanies;
  }

  public Long getMovieId() {
    return movieId;
  }

  public String getTitle() {
    return title;
  }

  public Long getBudget() {
    return budget;
  }

  public Double getPopularity() {
    return popularity;
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }

  public Long getRevenue() {
    return revenue;
  }

  public Long[] getGenres() {
    return genres;
  }

  public Long[] getProductionCompanies() {
    return productionCompanies;
  }

  /* equals() and hashCode() used to make sure movies with the same ID in Set are considered unique. */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Movie that = (Movie) o;

    return Objects.equals(movieId, that.movieId);

  }

  @Override
  public int hashCode() {
    return (movieId != null ? movieId.hashCode() : 0);
  }
}
