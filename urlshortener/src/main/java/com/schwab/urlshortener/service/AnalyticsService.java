package com.schwab.urlshortener.service;

import java.util.List;

import com.schwab.urlshortener.entity.ClickAnalytics;

public interface AnalyticsService {

    void saveAnalytics(String shortCode, String ip, String browser);

    List<ClickAnalytics> getAnalytics(String shortCode);

}