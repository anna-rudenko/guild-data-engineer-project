package com.rudenko.entities;

import java.util.Objects;

public class Genre {

  private Long genreId;
  private String genreName;

  public Genre(Long genreId, String genreName) {
    this.genreId = genreId;
    this.genreName = genreName;
  }

  public Long getGenreId() {
    return genreId;
  }

  public String getGenreName() {
    return genreName;
  }

  /* equals() and hashCode() used to make sure genes with the same ID in Set are considered unique. */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Genre that = (Genre) o;

    return Objects.equals(genreId, that.genreId);

  }

  @Override
  public int hashCode() {
    return (genreId != null ? genreId.hashCode() : 0);
  }
}
