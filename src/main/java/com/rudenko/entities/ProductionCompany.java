package com.rudenko.entities;

import java.util.Objects;

public class ProductionCompany {

  private Long productionCompanyId;
  private String productionCompanyName;

  public ProductionCompany(Long productionCompanyId, String productionCompanyName) {
    this.productionCompanyId = productionCompanyId;
    this.productionCompanyName = productionCompanyName;
  }

  public Long getProductionCompanyId() {
    return productionCompanyId;
  }

  public String getProductionCompanyName() {
    return productionCompanyName;
  }

  /* equals() and hashCode() used to make sure production companies with the same ID in Set are
     considered equal. */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ProductionCompany that = (ProductionCompany) o;

    return Objects.equals(productionCompanyId, that.productionCompanyId);

  }

  @Override
  public int hashCode() {
    return (productionCompanyId != null ? productionCompanyId.hashCode() : 0);
  }
}
