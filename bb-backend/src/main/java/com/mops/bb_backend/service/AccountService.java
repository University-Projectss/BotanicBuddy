package com.mops.bb_backend.service;

import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.model.Role;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account createAccount(String email, String password) {
        final var hashedPassword = passwordEncoder.encode(password);
        var account = new Account(email, hashedPassword, Role.USER);
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }

    public void linkUserToAccount(User user, Account account) {
        account.setUser(user);
        accountRepository.save(account);
    }

}
