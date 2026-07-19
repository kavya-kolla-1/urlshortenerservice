package com.schwab.urlshortener.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schwab.urlshortener.entity.ClickAnalytics;

@Repository
public interface AnalyticsRepository extends JpaRepository<ClickAnalytics, Long> {

    List<ClickAnalytics> findByShortCode(String shortCode);

}