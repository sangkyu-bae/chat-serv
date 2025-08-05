package com.chat.user.domain.account.service;

import com.chat.user.domain.account.dto.domain.Account;
import com.chat.user.domain.account.dto.request.LoginRequest;
import com.chat.user.domain.account.dto.request.RegisterAccount;
import com.chat.user.infra.error.DataBaseException;
import com.chat.user.infra.error.type.DataBaseError;
import com.chat.user.module.loign.UserAccount;
import com.chat.user.domain.account.entity.AccountEntity;
import com.chat.user.domain.account.repositroy.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.zip.DataFormatException;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AccountEntity accountEntity = accountRepository.findById(username)
                .orElseThrow(()->new UsernameNotFoundException(username));

        Account account = modelMapper.map(accountEntity,Account.class);

        return new UserAccount(account);
    }


    public Account registerAccount(RegisterAccount registerAccount){
        AccountEntity accountEntity = modelMapper.map(registerAccount,AccountEntity.class);
        accountEntity.setCreateAt(LocalDateTime.now());
        accountEntity.setUpdateAt(LocalDateTime.now());
        accountEntity.setPassword(passwordEncoder.encode(accountEntity.getPassword()));

        try{
             accountRepository.insertOnly(accountEntity);
        }catch (DataIntegrityViolationException e){
            throw new DataBaseException(DataBaseError.UNIQUE_CONSTRAINT_VIOLATION,"이미 존재하는 id 입니다.");
        }

        return modelMapper.map(accountEntity,Account.class);
    }


}
