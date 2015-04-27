package com.github.chanko.proxy;

import io.undertow.Undertow;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.server.handlers.proxy.SimpleProxyClientProvider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Server {
	public static void main(String... args) throws IOException {
		Undertow server;
		try {
			server = Undertow
					.builder()
					.addHttpListener(8080, "localhost")
					.setHandler(
							new SetRequestHeaderHandler(new ProxyHandler(
									new SimpleProxyClientProvider(new URI(
											"http://centos7:8080/")), 500,
									null, true, true), "Hoge", "hoge")).build();
			server.start();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
