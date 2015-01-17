package com.github.chanko.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.jboss.logging.Logger;

import com.github.chanko.cdi.ElapsedTime;

@Named
@ApplicationScoped
public class ServiceLocator {
	@Inject
	private ServletContext sc;

	@Inject
	private Logger log;

	private Properties props;

	@PostConstruct
	public void init() {
		props = new Properties();
		try {
			props.load(this.getClass().getResourceAsStream(
					"/service-location.properties"));
		} catch (IOException e) {
			log.fatal(
					"service-location.properties can not load. please check this file",
					e);
		}
	}

	@ElapsedTime
	public <T> T get(Class<T> clazz) {
		String simpleServiceName = clazz.getSimpleName().replace("Home", "");
		String canonicalServiceName = clazz.getCanonicalName().replace("Home",
				"");
		try {
			return getLocalBean(simpleServiceName, canonicalServiceName);
		} catch (NamingException e) {
			log.infof("LocalBean not found %s", canonicalServiceName);
		}

		String portName = simpleServiceName + "Port";

		try {
			String host = props.getProperty(canonicalServiceName + ".host",
					props.getProperty("default.host"));
			String namespace = props.getProperty(canonicalServiceName
					+ ".namespace", props.getProperty("default.namespace"));

			Service service = Service.create(
					new URL("http://" + host + sc.getContextPath() + "/"
							+ simpleServiceName + "?wsdl"), new QName(
							namespace, simpleServiceName + "Service"));

			return service.getPort(new QName(namespace, portName), clazz);
		} catch (MalformedURLException e) {
			log.error("Web Service requester cannot create", e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getLocalBean(String simpleName, String canonicalName)
			throws NamingException {
		InitialContext ctx = new InitialContext();

		String ref = "java:app" + sc.getContextPath() + "/" + simpleName + "!"
				+ canonicalName;

		log.infof("EJB lookup JNDI path [%s]", ref);

		return (T) ctx.lookup(ref);
	}
}
