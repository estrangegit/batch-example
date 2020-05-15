package com.foo.bar.batchexample.model;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "caisse")
public class Caisse {

  @Id
  @GeneratedValue
  private long id;

  @Column(name = "caisse")
  private String caisse;

  @Column(name = "codeservice")
  private String codeService;

  public Caisse() {}

  public Caisse(final String caisse, final String codeService,
      final LocalDateTime dateValidationDCF) {
    super();
    this.caisse = caisse;
    this.codeService = codeService;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getCaisse() {
    return caisse;
  }

  public void setCaisse(final String caisse) {
    this.caisse = caisse;
  }

  public String getCodeService() {
    return codeService;
  }

  public void setCodeService(final String codeService) {
    this.codeService = codeService;
  }

  @Override
  public String toString() {
    return "Caisse [id=" + id + ", caisse=" + caisse + ", codeService=" + codeService + "]";
  }
}
