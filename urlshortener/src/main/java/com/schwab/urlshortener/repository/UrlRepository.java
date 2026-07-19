package com.schwab.urlshortener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.schwab.urlshortener.entity.UrlMapping;

@Repository
public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

	Optional<UrlMapping> findByShortCode(String shortCode);

	Optional<UrlMapping> findByOriginalUrl(String originalUrl);

	boolean existsByShortCode(String shortCode);

}