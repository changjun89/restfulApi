package me.changjun.demorestapi.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;
import me.changjun.demorestapi.accounts.Account;
import me.changjun.demorestapi.accounts.AccountRole;
import me.changjun.demorestapi.accounts.AccountService;
import me.changjun.demorestapi.common.BaseControllerTest;
import me.changjun.demorestapi.common.TestDescription;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthServerConfigTest extends BaseControllerTest {

  @Autowired
  private AccountService accountService;

  @Test
  @TestDescription("인증토킁을 받는 서비스")
  public void getAuthToken() throws Exception {
    Set roles = new HashSet();
    roles.add(AccountRole.USER);
    roles.add(AccountRole.ADMIN);
    String userName = "leechang@naver.com";
    String password = "changjun";
    Account account = Account.builder()
        .email(userName)
        .password(password)
        .accountRoles(roles)
        .build();

    accountService.saveAccount(account);

    String id = "myApp";
    String secret = "secret";
    mockMvc.perform(post("/oauth/token")
        .with(httpBasic(id, secret))
        .param("username", userName)
        .param("password", password)
        .param("grant_type", "password")
    )
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("access_token").exists());
  }
}
