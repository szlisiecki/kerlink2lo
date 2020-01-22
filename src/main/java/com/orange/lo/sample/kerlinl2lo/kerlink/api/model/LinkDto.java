package com.orange.lo.sample.kerlinl2lo.kerlink.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkDto {

  @JsonProperty("rel")
  private String rel = null;

  @JsonProperty("href")
  private String href = null;
  
  public String getRel() {
    return rel;
  }
  public void setRel(String rel) {
    this.rel = rel;
  }
  public String getHref() {
    return href;
  }
  public void setHref(String href) {
    this.href = href;
  }
}
