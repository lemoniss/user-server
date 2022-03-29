package com.maxlength.aggregate.repository;

import com.maxlength.aggregate.entity.SecedeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecedeRepository extends JpaRepository<SecedeEntity, Long> {

}