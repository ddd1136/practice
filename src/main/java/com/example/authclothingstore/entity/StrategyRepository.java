package com.example.authclothingstore.entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyRepository extends JpaRepository<Strategy, Integer> {
    List<Strategy> findAllByUserId(Integer userId);
    boolean existsById(Integer id);
}