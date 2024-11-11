package com.mops.bb_backend.runner;


import com.mops.bb_backend.model.Account;
import com.mops.bb_backend.model.Role;
import com.mops.bb_backend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class Seeder implements ApplicationRunner {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(ApplicationArguments args) {
        Account acc = new Account("user1@example.com", passwordEncoder.encode("password"), Role.USER);
        accountRepository.save(acc);
    }
}