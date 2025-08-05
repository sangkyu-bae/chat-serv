package com.chat.user.domain.account.repositroy;

import com.chat.user.domain.account.entity.AccountEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class BaseRepositoryImpl<T> implements BaseRepository<T> {
    @PersistenceContext
    private EntityManager em;
    @Override
    public void insertOnly(T entity) {
        em.persist(entity);
        em.flush();
    }
}
