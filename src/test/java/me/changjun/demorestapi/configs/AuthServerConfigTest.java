package me.changjun.demorestapi.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import me.changjun.demorestapi.common.AppProperties;
import me.changjun.demorestapi.common.BaseControllerTest;
import me.changjun.demorestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthServerConfigTest extends BaseControllerTest {

  @Autowired
  private AppProperties appProperties;

  @Test
  @TestDescription("인증토킁을 받는 서비스")
  public void getAuthToken() throws Exception {
    mockMvc.perform(post("/oauth/token")
        .with(httpBasic(appProperties.getClientName(), appProperties.getClientSecret()))
        .param("username", appProperties.getUserUserName())
        .param("password", appProperties.getUserPassword())
        .param("grant_type", "password")
    )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("access_token").exists());
  }
}
