package com.github.chanko.jpa;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Test;

public class EmpTest {
	@Test
	public void test_emp_findAll() {
		Client client = ClientBuilder.newClient();
		Response res = client
				.target("http://localhost:8080/chanko-oracle/rs/emp")
				.request(MediaType.APPLICATION_JSON).get();

		assertThat(res.getStatus(), is(200));
		System.out.println(res.readEntity(String.class));
	}

	@Test
	public void test_emp_matchName() {
		Client client = ClientBuilder.newClient();
		Response res = client
				.target("http://localhost:8080/chanko-oracle/rs/emp/emp001")
				.request(MediaType.APPLICATION_JSON).get();

		assertThat(res.getStatus(), is(200));
		System.out.println(res.readEntity(String.class));
	}
}
