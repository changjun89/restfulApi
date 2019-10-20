package me.changjun.demorestapi.accounts;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

  private final AccountRepository accountRepository;

  public AccountService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Account account = accountRepository.findByEmail(email)
        .orElseThrow(
            () -> new UsernameNotFoundException(email)
        );
    return new User(account.getEmail(), account.getPassword(), authorities(account.getAccountRoles()));
  }

  private Collection<? extends GrantedAuthority> authorities(Set<AccountRole> roles) {
    return roles.stream().map(role ->
        new SimpleGrantedAuthority("ROLE_" + role.name())
    ).collect(Collectors.toSet());
  }
}
