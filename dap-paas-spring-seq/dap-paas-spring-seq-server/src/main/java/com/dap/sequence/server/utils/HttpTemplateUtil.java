package com.dap.sequence.server.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class HttpTemplateUtil {
	private static final Logger log = LoggerFactory.getLogger("HttpTemplateUtil");
	private static int maxTotalConnect = 500;

	private static int maxConnectPerRoute = 500;

	private static int readTimeout = 60000;

	private static int connectTimeout = 60000;

	private static class SingletonRestTemplate {

		static RestTemplate INSTANCE = new RestTemplate(createFactory());
	}

	public void setReadTimeout(int readTimeout) {
		HttpTemplateUtil.readTimeout = readTimeout;
	}

	public void setMaxConnectPerRoute(int maxConnectPerRoute) {
		HttpTemplateUtil.maxConnectPerRoute = maxConnectPerRoute;
	}

	public Map<String, Object> postForEntity(String url, String params) {
		log.info("postForEntity restTemplate:" + HttpTemplateUtil.getInstance());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
		HttpEntity<String> requestEntity = new HttpEntity<String>(params, headers);
		try {
			ResponseEntity<String> responseEntity = HttpTemplateUtil.getInstance().postForEntity(url, requestEntity,
					String.class);
			HttpStatus status = responseEntity.getStatusCode();
			responseEntity.getBody();
			log.info("HttpStatus is:" + String.valueOf(status.value()));
			resultMap.put("RetVal",responseEntity.getBody());
			if (status.is2xxSuccessful()) {
				resultMap.put("RetCode", "000000");
			} else {
				resultMap.put("RetCode", "99999");
			}
		} catch (Exception e) {
			log.error("Post Error!" + e.getMessage(),e);
		}
		return resultMap;
	}

	public static RestTemplate getInstance() {
		return SingletonRestTemplate.INSTANCE;
	}

	private static ClientHttpRequestFactory createFactory() {

		if (maxTotalConnect <= 0) {
			SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
			factory.setConnectTimeout(connectTimeout);
			factory.setReadTimeout(readTimeout);
			return factory;
		}
		HttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(maxTotalConnect)
				.setMaxConnPerRoute(maxConnectPerRoute).build();
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
		factory.setConnectTimeout(connectTimeout);
		factory.setReadTimeout(readTimeout);
		return factory;
	}

	public void setConnectTimeout(int connectTimeout) {
		HttpTemplateUtil.connectTimeout = connectTimeout;

		HttpTemplateUtil.readTimeout = HttpTemplateUtil.connectTimeout;
	}

	public String postForObject(String url, String params) {
		log.info("postForEntity restTemplate:" + HttpTemplateUtil.getInstance());
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
		HttpEntity<String> requestEntity = new HttpEntity<String>(params, headers);
		String result = HttpTemplateUtil.getInstance().postForObject(url, requestEntity, String.class);
		return result;
	}

	public void setMaxTotalConnect(int maxTotalConnect) {
		HttpTemplateUtil.maxTotalConnect = maxTotalConnect;
	}

	public Map<String, Object> getForEntity(String url) {
		log.info("postForEntity restTemplate:" + HttpTemplateUtil.getInstance());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		try {
			ResponseEntity<String> responseEntity = HttpTemplateUtil.getInstance().getForEntity(url, String.class,
					requestEntity);
			HttpStatus status = responseEntity.getStatusCode();
			log.info("HttpStatus is:" + String.valueOf(status.value()));
			resultMap.put("RetVal",responseEntity.getBody());
			if (status.is2xxSuccessful()) {
				resultMap.put("RetCode", "000000");
			} else {
				resultMap.put("RetCode", "99999");
			}
		} catch (Exception e) {
			log.error("Post Error!" + e.getMessage(),e);
		}
		return resultMap;
	}

}
