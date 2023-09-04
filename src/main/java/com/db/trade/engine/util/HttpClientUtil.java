package com.db.trade.engine.util;

import java.util.Arrays;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.db.trade.engine.model.Trade;

@Component
public class HttpClientUtil {

	public static ResponseEntity<Object> httpClientCall(Trade trade, String endPoint) {
		RestTemplate restTemplate = new RestTemplate();
		ParameterizedTypeReference<Object> responseType =  new ParameterizedTypeReference<Object>() {};
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		HttpEntity<Trade> entity = new HttpEntity<>(trade, headers);
			return restTemplate.exchange(
					endPoint,
					HttpMethod.POST, entity,responseType);
	}
}
