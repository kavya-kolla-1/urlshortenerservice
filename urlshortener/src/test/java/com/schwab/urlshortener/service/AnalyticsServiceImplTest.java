package com.schwab.urlshortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.schwab.urlshortener.entity.ClickAnalytics;
import com.schwab.urlshortener.repository.AnalyticsRepository;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceImplTest {

    @Mock
    private AnalyticsRepository repository;

    @InjectMocks
    private AnalyticsServiceImpl service;

    @Test
    @DisplayName("Should save analytics successfully")
    void testSaveAnalytics() {
        ClickAnalytics analytics = new ClickAnalytics();
        when(repository.save(any(ClickAnalytics.class))).thenReturn(analytics);

        service.saveAnalytics("abc123", "127.0.0.1", "Chrome");

        verify(repository, times(1)).save(any(ClickAnalytics.class));
    }

    @Test
    @DisplayName("Should save analytics with correct shortCode")
    void testSaveAnalyticsShortCode() {
        when(repository.save(any(ClickAnalytics.class))).thenAnswer(i -> i.getArgument(0));

        service.saveAnalytics("abc123", "127.0.0.1", "Chrome");

        ArgumentCaptor<ClickAnalytics> captor = ArgumentCaptor.forClass(ClickAnalytics.class);
        verify(repository).save(captor.capture());
        assertEquals("abc123", captor.getValue().getShortCode());
    }

    @Test
    @DisplayName("Should save analytics with correct IP address")
    void testSaveAnalyticsIpAddress() {
        when(repository.save(any(ClickAnalytics.class))).thenAnswer(i -> i.getArgument(0));

        service.saveAnalytics("abc123", "192.168.1.1", "Firefox");

        ArgumentCaptor<ClickAnalytics> captor = ArgumentCaptor.forClass(ClickAnalytics.class);
        verify(repository).save(captor.capture());
        assertEquals("192.168.1.1", captor.getValue().getIpAddress());
    }

    @Test
    @DisplayName("Should return analytics list")
    void testGetAnalytics() {
        ClickAnalytics analytics = new ClickAnalytics();
        analytics.setShortCode("abc123");
        when(repository.findByShortCode("abc123")).thenReturn(Arrays.asList(analytics));

        List<ClickAnalytics> result = service.getAnalytics("abc123");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("abc123", result.get(0).getShortCode());
    }

    @Test
    @DisplayName("Should return empty list when no analytics found")
    void testEmptyAnalytics() {
        when(repository.findByShortCode("xyz")).thenReturn(Collections.emptyList());

        List<ClickAnalytics> result = service.getAnalytics("xyz");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Verify repository findByShortCode invocation")
    void testRepositoryInvocation() {
        when(repository.findByShortCode("abc123")).thenReturn(Collections.emptyList());

        service.getAnalytics("abc123");

        verify(repository, times(1)).findByShortCode("abc123");
    }

    @Test
    @DisplayName("Verify save invocation")
    void testSaveInvocation() {
        when(repository.save(any())).thenReturn(new ClickAnalytics());

        service.saveAnalytics("abc123", "127.0.0.1", "Chrome");

        verify(repository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return multiple analytics records")
    void testMultipleAnalyticsRecords() {
        ClickAnalytics a1 = new ClickAnalytics();
        a1.setShortCode("abc123");
        ClickAnalytics a2 = new ClickAnalytics();
        a2.setShortCode("abc123");
        ClickAnalytics a3 = new ClickAnalytics();
        a3.setShortCode("abc123");

        when(repository.findByShortCode("abc123")).thenReturn(Arrays.asList(a1, a2, a3));

        List<ClickAnalytics> result = service.getAnalytics("abc123");

        assertEquals(3, result.size());
    }
}
