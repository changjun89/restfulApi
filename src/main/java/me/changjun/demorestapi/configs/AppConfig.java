package me.changjun.demorestapi.configs;

import java.util.HashSet;
import java.util.Set;
import me.changjun.demorestapi.accounts.Account;
import me.changjun.demorestapi.accounts.AccountRole;
import me.changjun.demorestapi.accounts.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {


  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public ApplicationRunner applicationRunner() {
    return new ApplicationRunner() {
      @Autowired
      private AccountService accountService;

      @Override
      public void run(ApplicationArguments args) throws Exception {
        Set roles = new HashSet();
        roles.add(AccountRole.USER);
        roles.add(AccountRole.ADMIN);
        Account account = Account.builder()
            .email("leechang0423@naver.com")
            .password("changjun")
            .accountRoles(roles)
            .build();

        accountService.saveAccount(account);
      }
    };

  }

}
