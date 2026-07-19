package com.schwab.urlshortener.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.schwab.urlshortener.entity.ClickAnalytics;
import com.schwab.urlshortener.repository.AnalyticsRepository;

@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private AnalyticsRepository repository;

    @Override
    public void saveAnalytics(String shortCode, String ip, String browser) {

        ClickAnalytics analytics = new ClickAnalytics();

        analytics.setShortCode(shortCode);
        analytics.setIpAddress(ip);
        analytics.setBrowser(browser);
        analytics.setClickedAt(LocalDateTime.now());

        repository.save(analytics);

    }

    @Override
    public List<ClickAnalytics> getAnalytics(String shortCode) {

        return repository.findByShortCode(shortCode);

    }

}