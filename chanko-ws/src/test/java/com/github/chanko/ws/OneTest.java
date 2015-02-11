package com.github.chanko.ws;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.junit.Test;

public class OneTest {
	private static final String SERVICE_PORT = "OnePort";
	private static final String SERVICE = "OneService";
	private static final String NAMESPACE_URI = "http://ws.chanko.github.com/";
	private static final String WSDL_URL = "http://localhost:8080/chanko-ws/One?wsdl";

	@Test
	public void test_one() throws Exception {
		Service service = Service.create(new URL(WSDL_URL), new QName(
				NAMESPACE_URI, SERVICE));
		OnePort one = service.getPort(new QName(NAMESPACE_URI, SERVICE_PORT),
				OnePort.class);

		long serialTime = System.currentTimeMillis();

		Req req = new Req();
		req.setHead("test_header");
		req.setBody("test_body");
		req.setNums(new BigDecimal(Long.MAX_VALUE));
		req.setTs(new Timestamp(serialTime));

		Res res = one.ok(req);

		assertThat(res.getMessage(),
				is("one --> " + req.getHead() + ":" + req.getBody()));
		assertThat(res.isSuccess(), is(true));
		assertThat(res.getNums(),
				is(new BigDecimal(Long.MAX_VALUE).add(BigDecimal.ONE)));
		assertThat(res.getTs(), is(new Timestamp(serialTime)));
	}
}
