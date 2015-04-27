package com.github.chanko.proxy;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

public class SetRequestHeaderHandler implements HttpHandler {
	private HttpHandler next;
	private HttpString header;
	private String value;

	public SetRequestHeaderHandler(HttpHandler next, String header, String value) {
		this.next = next;
		this.header = HttpString.tryFromString(header);
		this.value = value;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		exchange.getRequestHeaders().put(header, value);
		next.handleRequest(exchange);
	}

}
