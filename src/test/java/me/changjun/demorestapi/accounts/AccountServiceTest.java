package me.changjun.demorestapi.accounts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.HashSet;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Autowired
  private AccountService accountService;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  public void findByUserName() {
    //given
    Set<AccountRole> roles = new HashSet<>();
    roles.add(AccountRole.ADMIN);
    roles.add(AccountRole.USER);

    String password = "changjun";
    String email = "leechang0423@naver.com";
    Account account = Account.builder()
        .email(email)
        .password(password)
        .accountRoles(roles)
        .build();

    this.accountService.saveAccount(account);

    //when
    UserDetailsService userDetailsService = (UserDetailsService) accountService;
    UserDetails userDetails = userDetailsService.loadUserByUsername("leechang0423@naver.com");

    //then
    assertThat(this.passwordEncoder.matches(password,userDetails.getPassword())).isTrue();
  }

  @Test
  public void findByUserNameFail_1() {
    String name = "noUser@naver.com";
    try {
      accountService.loadUserByUsername(name);
      fail("suppose to be failed");
    } catch (UsernameNotFoundException e) {
      assertThat(e.getMessage()).containsSequence(name);
    }
  }

  @Test(expected = UsernameNotFoundException.class)
  public void findByUserNameFail_2() {
    String name = "noUser@naver.com";
    accountService.loadUserByUsername(name);
  }

  @Test
  public void findByUserNameFail_3() {
    //expected
    String name = "noUser@naver.com";
    expectedException.expect(UsernameNotFoundException.class);
    expectedException.expectMessage(Matchers.containsString(name));

    //when
    accountService.loadUserByUsername(name);
  }
}
