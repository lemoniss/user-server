package com.maxlength.aggregate.repository;

import com.maxlength.aggregate.entity.UserEntity;
import com.maxlength.spec.enums.Yesno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByCiAndDelYn(String ci, Yesno delYn);

    UserEntity findByUuidAndDelYn(String uuid, Yesno delYn);

    UserEntity findByIdAndDelYn(Long id, Yesno delYn);
}