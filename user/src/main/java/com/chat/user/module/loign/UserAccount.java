package com.chat.user.module.loign;

import com.chat.user.domain.account.dto.domain.Account;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;

@Getter
public class UserAccount extends User {

    private Account Account;

    public UserAccount(Account account){
        super(account.getUserId(),account.getPassword(), account.getAuthList());
        this.Account = account;
    }
}
