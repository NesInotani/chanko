package local.sandbox.rs;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HelloTest {
	private UndertowJaxrsServer server;

	@Before
	public void initClass() {
		server = new UndertowJaxrsServer().start();
		server.deploy(App.class);
	}

	@After
	public void stop() {
		server.stop();
	}

	@Test
	public void test_hello() throws Exception {
		Client client = ClientBuilder.newClient();
		String res = client.target(TestPortProvider.generateURL("/rs/hello"))
				.request().get(String.class);

		assertThat(res, is("hello world"));
		client.close();
	}
}
