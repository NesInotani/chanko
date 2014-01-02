package com.github.chanko.web.util;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.logging.Logger;

public class Resource {

	@Produces
	public Logger createLogger(InjectionPoint inject) {
		return Logger.getLogger(inject.getMember().getDeclaringClass());
	}
}
