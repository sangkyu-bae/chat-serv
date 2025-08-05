package com.chat.user.domain.account.repositroy;

import com.chat.user.domain.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity,String> , BaseRepository<AccountEntity> {
}
