package com.chat.user.domain.account.repositroy;

import com.chat.user.domain.account.entity.AccountEntity;

public interface BaseRepository<T> {
    void insertOnly(T entity);
}
