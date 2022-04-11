package com.willams.twowaytls;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class RequestClientService {

	private static final String SERVER_PATH = "/resource/{id}";

	@Autowired
	private WebClient webClientFe;

	public Mono<String> getRequest(String id) {
		Map<String, String> context = MDC.getCopyOfContextMap();
		return webClientFe.get()
			.uri(SERVER_PATH, id)
			.retrieve()
			.bodyToMono(String.class)
			.doOnEach(t -> setContext(context));
	}

	private void setContext(Map<String, String> context) {
		if (context != null) {
			MDC.setContextMap(context);
		}
	}
}
