package com.mops.bb_backend.service;

import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.model.Role;
import com.mops.bb_backend.model.User;
import com.mops.bb_backend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import com.mops.bb_backend.exception.AccountDoesNotExist;
import com.mops.bb_backend.exception.AccountAlreadyExists;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public Account createUserAccount(String email, String password) {
        if (accountRepository.findByEmail(email).isPresent()) {
            throw new AccountAlreadyExists();
        }

        final String hashedPassword = passwordEncoder.encode(password);
        var account = new Account(email, hashedPassword, Role.USER);
        return accountRepository.save(account);
    }

    public Optional<Account> findAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    public String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }

    public Account getAuthenticatedUserAccount() {
        return accountRepository.findByEmail(getAuthenticatedUserEmail()).orElseThrow(AccountDoesNotExist::new);
    }

    public boolean linkProfileToAccount(User user, String email) {
        var updatedRowsCount = accountRepository.updateUserIfUnset(email, user);
        return updatedRowsCount > 0;
    }

}
