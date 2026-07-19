package com.schwab.urlshortener.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.schwab.urlshortener.dto.UrlRequest;
import com.schwab.urlshortener.dto.UrlResponse;
import com.schwab.urlshortener.entity.UrlMapping;
import com.schwab.urlshortener.exception.ResourceNotFoundException;
import com.schwab.urlshortener.repository.UrlRepository;

@ExtendWith(MockitoExtension.class)
class UrlServiceImplTest {

    @Mock
    private UrlRepository repository;

    @InjectMocks
    private UrlServiceImpl service;

    private UrlRequest request;

    @BeforeEach
    void setup() {
        request = new UrlRequest();
        request.setOriginalUrl("https://www.google.com");
    }

    @Test
    @DisplayName("Should shorten URL successfully")
    void testShortenUrl() {
        when(repository.findByOriginalUrl(anyString())).thenReturn(Optional.empty());
        when(repository.existsByShortCode(anyString())).thenReturn(false);
        when(repository.save(any(UrlMapping.class))).thenAnswer(i -> i.getArgument(0));

        UrlResponse response = service.shortenUrl(request);

        assertNotNull(response);
        assertNotNull(response.getShortCode());
        assertNotNull(response.getShortUrl());
        verify(repository, times(1)).save(any(UrlMapping.class));
    }

    @Test
    @DisplayName("Short code collision should generate another code")
    void testShortCodeCollision() {
        when(repository.findByOriginalUrl(anyString())).thenReturn(Optional.empty());
        when(repository.existsByShortCode(anyString()))
                .thenReturn(true)
                .thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        UrlResponse response = service.shortenUrl(request);

        assertNotNull(response);
        verify(repository, times(2)).existsByShortCode(anyString());
    }

    @Test
    @DisplayName("Click count should increment")
    void testClickCountIncrement() {
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode("abc123");
        mapping.setOriginalUrl("https://www.google.com");
        mapping.setClickCount(5L);

        when(repository.findByShortCode("abc123")).thenReturn(Optional.of(mapping));
        when(repository.save(any())).thenReturn(mapping);

        service.getOriginalUrl("abc123");

        assertEquals(6L, mapping.getClickCount());
    }

    @Test
    @DisplayName("Click count should increment correctly on multiple sequential calls")
    void testClickCountIncrementMultipleCalls() {
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode("abc123");
        mapping.setOriginalUrl("https://www.google.com");
        mapping.setClickCount(0L);

        when(repository.findByShortCode("abc123")).thenReturn(Optional.of(mapping));
        when(repository.save(any())).thenAnswer(i -> {
            UrlMapping saved = i.getArgument(0);
            mapping.setClickCount(saved.getClickCount());
            return saved;
        });

        service.getOriginalUrl("abc123");
        service.getOriginalUrl("abc123");
        service.getOriginalUrl("abc123");

        assertEquals(3L, mapping.getClickCount());
    }

    @Test
    @DisplayName("Duplicate URL should throw IllegalArgumentException")
    void testDuplicateUrl() {
        UrlMapping mapping = new UrlMapping();
        mapping.setOriginalUrl("https://www.google.com");

        when(repository.findByOriginalUrl(anyString())).thenReturn(Optional.of(mapping));

        assertThrows(IllegalArgumentException.class, () -> service.shortenUrl(request));
    }

    @Test
    @DisplayName("Get Original URL")
    void testGetOriginalUrl() {
        UrlMapping mapping = new UrlMapping();
        mapping.setShortCode("abc123");
        mapping.setOriginalUrl("https://www.google.com");
        mapping.setClickCount(0L);

        when(repository.findByShortCode("abc123")).thenReturn(Optional.of(mapping));
        when(repository.save(any())).thenReturn(mapping);

        String result = service.getOriginalUrl("abc123");

        assertEquals("https://www.google.com", result);
        verify(repository).save(any());
    }

    @Test
    @DisplayName("URL Not Found should throw ResourceNotFoundException")
    void testUrlNotFound() {
        when(repository.findByShortCode(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getOriginalUrl("xyz"));
    }

    @Test
    @DisplayName("Repository Save Verification")
    void testRepositorySave() {
        when(repository.findByOriginalUrl(anyString())).thenReturn(Optional.empty());
        when(repository.existsByShortCode(anyString())).thenReturn(false);
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.shortenUrl(request);

        verify(repository, times(1)).save(any());
    }
}
