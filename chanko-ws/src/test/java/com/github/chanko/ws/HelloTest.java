package com.github.chanko.ws;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Test;

public class HelloTest {
	@Test
	public void test_hello() throws Exception {
		URL hello = new URL("http://localhost:8080/chanko-ws/hello");
		HttpURLConnection con = (HttpURLConnection) hello.openConnection();

		assertThat(con.getResponseCode(), is(200));
	}
}
