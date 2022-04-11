package com.willams.twowaytls;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.ssl.SSLFactory;
import nl.altindag.ssl.util.NettySslUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

@Configuration
@Slf4j
public class WebClientConfig {

	private static final String HEADER_CACHE_CONTROL = "cache-control";
	private static final String HEADER_CACHE_CONTROL_VALUE = "false";
	private static final String SERVER_URL = "https://localhost:8443";

	@Value("${client.ssl.one-way-authentication-enabled:false}") boolean oneWayAuthenticationEnabled;
	@Value("${client.ssl.two-way-authentication-enabled:false}") boolean twoWayAuthenticationEnabled;
	@Value("${client.ssl.key-store:}") String keyStorePath;
	@Value("${client.ssl.key-store-password:}") char[] keyStorePassword;
	@Value("${client.ssl.trust-store:}") String trustStorePath;
	@Value("${client.ssl.trust-store-password:}") char[] trustStorePassword;

	@Bean
	public WebClient webClientFe() throws SSLException {
		HttpClient httpClient = HttpClient.create()
				.secure(sslSpec -> sslSpec.sslContext(getTwoWaySslContext()));

		return WebClient
				.builder()
				.uriBuilderFactory(getBaseFeUri())
				.defaultHeader(HEADER_CACHE_CONTROL, HEADER_CACHE_CONTROL_VALUE)
				.baseUrl(SERVER_URL)
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();
	}

	private DefaultUriBuilderFactory getBaseFeUri() {
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(SERVER_URL);
		factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
		return factory;
	}

	private SslContext getTwoWaySslContext() {

		try(FileInputStream keyStoreFileInputStream = new FileInputStream(ResourceUtils.getFile(keyStorePath));
		    FileInputStream trustStoreFileInputStream = new FileInputStream(ResourceUtils.getFile(trustStorePath));
		) {
			KeyStore keyStore = KeyStore.getInstance("jks");
			keyStore.load(keyStoreFileInputStream, keyStorePassword);
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, keyStorePassword);

			KeyStore trustStore = KeyStore.getInstance("jks");
			trustStore.load(trustStoreFileInputStream, trustStorePassword);
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(trustStore);

			return SslContextBuilder.forClient()
					.keyManager(keyManagerFactory)
					.trustManager(trustManagerFactory)
					.build();

		} catch (Exception e) {
			log.error("An error has occurred: ", e);
		}

		return null;
	}
}
