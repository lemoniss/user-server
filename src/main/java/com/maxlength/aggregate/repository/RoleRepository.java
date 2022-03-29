package com.maxlength.aggregate.repository;

import com.maxlength.aggregate.entity.RoleEntity;
import com.maxlength.spec.enums.Yesno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    RoleEntity findByNameAndDelYn(String name, Yesno delYn);
}