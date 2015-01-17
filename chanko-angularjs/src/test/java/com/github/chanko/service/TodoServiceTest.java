package com.github.chanko.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.json.Json;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Test;

public class TodoServiceTest {
	private static final String RS_URL = "http://localhost:8080/chanko-angularjs/rs/todo";
	private static final String TODO_PORT = "TodoServicePort";
	private static final String TODO_SERVICE = "TodoServiceService";
	private static final String NAMESPACE_URI = "http://service.chanko.github.com/";
	private static final String WSDL_URL = "http://localhost:8080/chanko-angularjs/TodoService?wsdl";

	@Test
	public void test_ws() throws Exception {
		Service service = Service.create(new URL(WSDL_URL), new QName(
				NAMESPACE_URI, TODO_SERVICE));
		TodoPort todo = service.getPort(new QName(NAMESPACE_URI, TODO_PORT),
				TodoPort.class);

		TodoParam param = new TodoParam();
		param.setTitle("ws-test");
		param.setDone(false);

		Result r = todo.register(param);
		assertThat(r.getResult(), is("success"));
		assertThat(r.getCount(), is(1L));
	}

	@Test
	public void test_ws_null() throws Exception {
		Service service = Service.create(new URL(WSDL_URL), new QName(
				NAMESPACE_URI, TODO_SERVICE));
		TodoPort todo = service.getPort(new QName(NAMESPACE_URI, TODO_PORT),
				TodoPort.class);

		TodoParam param = new TodoParam();
		param.setDone(false);

		try {
			todo.register(param);
		} catch (SOAPFaultException se) {
			assertThat(se.getFault().getFaultCode(), is("ns1:Receiver"));
			assertThat(se.getFault().getFaultString(),
					is(startsWith("Exception")));
		}
	}

	@Test
	public void test_rs() {
		Client client = ClientBuilder.newClient();
		try {
			Response res = client
					.target(new URI(RS_URL))
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(Json.createObjectBuilder()
							.add("title", "rs-test").add("done", true).build()
							.toString()));

			assertThat(res.getStatus(), is(200));

			String result = res.readEntity(String.class);
			assertThat(result, is("{\"result\":\"success\",\"count\":1}"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("exception occurred");
		}
	}

	@Test
	public void test_rs_null() {
		Client client = ClientBuilder.newClient();
		try {
			Response res = client
					.target(new URI(RS_URL))
					.request(MediaType.APPLICATION_JSON)
					.post(Entity.json(Json.createObjectBuilder()
							.add("done", true).build().toString()));

			assertThat(res.getStatus(), is(500));

			String result = res.readEntity(String.class);
			assertThat(result, is("{\"result\":\"failure\",\"count\":-1}"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("exception occurred");
		}
	}

	@Test
	public void test_ejb() throws NamingException {
		Properties jndiProps = new Properties();
		jndiProps.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.jboss.naming.remote.client.InitialContextFactory");
		jndiProps.put(Context.PROVIDER_URL, "http-remoting://localhost:8080");
		jndiProps.put("jboss.naming.client.ejb.context", true);
		jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
		Context ctx = new InitialContext(jndiProps);

		TodoServiceHome todo = (TodoServiceHome) ctx
				.lookup("chanko-angularjs/TodoService!com.github.chanko.service.TodoServiceHome$IRemote");

		TodoParam param = new TodoParam();
		param.setTitle("ejb-test");
		param.setDone(false);

		Result r = todo.register(param);
		assertThat(r.getResult(), is("success"));
		assertThat(r.getCount(), is(1L));

		ctx.close();
	}
}
