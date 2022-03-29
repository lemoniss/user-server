package com.maxlength.aggregate.repository;

import com.maxlength.aggregate.entity.AccountTermsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountTermsRepository extends JpaRepository<AccountTermsEntity, Long>, JpaSpecificationExecutor<AccountTermsEntity> {

    @Modifying
    @Query("DELETE FROM AccountTermsEntity at WHERE at.accountId=:accountIds")
    void deleteAllByAccountId(@Param("accountIds") Long accountIds);
}