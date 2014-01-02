package com.github.chanko.web.websocket;

import javax.inject.Inject;
import javax.websocket.OnMessage;
import javax.websocket.server.ServerEndpoint;

import org.jboss.logging.Logger;

@ServerEndpoint(value="/echo")
public class EchoEndopoint {
	@Inject
	private Logger log;
	
	@OnMessage
	public String echo(String message) {
		log.infof("message[%s]", message);
		return message;
	}
}
