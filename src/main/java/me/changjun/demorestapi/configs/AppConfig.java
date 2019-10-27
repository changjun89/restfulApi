package me.changjun.demorestapi.configs;

import java.util.HashSet;
import java.util.Set;
import me.changjun.demorestapi.accounts.Account;
import me.changjun.demorestapi.accounts.AccountRole;
import me.changjun.demorestapi.accounts.AccountService;
import me.changjun.demorestapi.common.AppProperties;
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

      @Autowired
      private AppProperties appProperties;

      @Override
      public void run(ApplicationArguments args) throws Exception {
        Set adminRoles = new HashSet();
        adminRoles.add(AccountRole.USER);
        adminRoles.add(AccountRole.ADMIN);
        Account admin = Account.builder()
            .email(appProperties.getAdminUserName())
            .password(appProperties.getUserPassword())
            .accountRoles(adminRoles)
            .build();

        accountService.saveAccount(admin);

        Set userRoles = new HashSet();
        adminRoles.add(AccountRole.USER);
        Account user = Account.builder()
            .email(appProperties.getUserUserName())
            .password(appProperties.getUserPassword())
            .accountRoles(userRoles)
            .build();

        accountService.saveAccount(user);
      }
    };
  }

}
