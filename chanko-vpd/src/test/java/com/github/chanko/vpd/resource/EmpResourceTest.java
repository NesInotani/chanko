package com.github.chanko.vpd.resource;

import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.junit.Test;

public class EmpResourceTest {
	private static final String URL = "http://localhost:8080/chanko-vpd/rs/emp";
	private static final String URL_APP = URL + "/app";
	private static final String URL_USER01 = URL + "/user01";
	private static final String URL_USER02 = URL + "/user02";

	@Test
	public void test_all() {
		Client client = ClientBuilder.newClient();
		try {
			client.target(new URI(URL_APP)).request(MediaType.TEXT_PLAIN).get();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("exception occurred");
		}

	}

	@Test
	public void test_user01() {
		Client client = ClientBuilder.newClient();
		try {
			client.target(new URI(URL_USER01)).request(MediaType.TEXT_PLAIN)
					.get();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("exception occurred");
		}

	}

	@Test
	public void test_user02() {
		Client client = ClientBuilder.newClient();
		try {
			client.target(new URI(URL_USER02)).request(MediaType.TEXT_PLAIN)
					.get();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("exception occurred");
		}

	}
}
