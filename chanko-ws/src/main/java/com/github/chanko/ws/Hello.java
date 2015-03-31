package com.github.chanko.ws;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import org.jboss.logging.Logger;

@WebService(name = "Hello")
@SOAPBinding(style = Style.RPC)
public class Hello {
	private static final Logger log = Logger.getLogger(Hello.class);

	@WebMethod
	@WebResult(name = "result")
	public Res say() {
		Res res = new Res();
		res.setMessage("hello");
		res.setSuccess(true);
		res.setNums(BigDecimal.ONE);
		res.setTs(new Timestamp(System.currentTimeMillis()));

		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return res;
	}

	@WebMethod
	@Oneway
	public void sayOneway() {
		log.info("hello one way");
	}
}
