package me.changjun.demorestapi.common;

import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "my-app")
@Getter @Setter
public class AppProperties {

  @NotEmpty
  private String adminUserName;
  @NotEmpty
  private String adminPassword;
  @NotEmpty
  private String userUserName;
  @NotEmpty
  private String userPassword;
  @NotEmpty
  private String clientName;
  @NotEmpty
  private String clientSecret;


}
