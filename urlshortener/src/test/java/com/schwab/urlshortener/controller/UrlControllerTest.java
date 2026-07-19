package com.schwab.urlshortener.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.schwab.urlshortener.dto.UrlResponse;
import com.schwab.urlshortener.exception.ResourceNotFoundException;
import com.schwab.urlshortener.service.UrlService;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private UrlService urlService;;

	@Test
	@DisplayName("Shorten URL API")
	void testShortenUrl() throws Exception {

		UrlResponse response = new UrlResponse("abc123", "http://localhost:8080/api/v1/url/abc123");

		when(urlService.shortenUrl(any())).thenReturn(response);

		mockMvc.perform(post("/api/v1/url/shorten").contentType(MediaType.APPLICATION_JSON).content("""
				{
				  "originalUrl":"https://www.google.com"
				}
				""")).andExpect(status().isOk());
	}

	@Test
	@DisplayName("Redirect API")
	void testRedirect() throws Exception {

		when(urlService.getOriginalUrl("abc123")).thenReturn("https://www.google.com");

		mockMvc.perform(get("/api/v1/url/abc123")).andExpect(status().isFound())
				.andExpect(header().string("Location", "https://www.google.com"));
	}

	@Test
	@DisplayName("Redirect Invalid URL")
	void testRedirectInvalid() throws Exception {

		when(urlService.getOriginalUrl("invalid")).thenThrow(new ResourceNotFoundException("URL_404", "Not Found"));

		mockMvc.perform(get("/api/v1/url/invalid")).andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("Invalid Request")
	void testInvalidRequest() throws Exception {

		mockMvc.perform(post("/api/v1/url/shorten").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest());
	}
}