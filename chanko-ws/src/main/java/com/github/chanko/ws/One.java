package com.github.chanko.ws;

import java.math.BigDecimal;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class One {
	@WebMethod
	public Res ok(Req req) {
		Res res = new Res();
		res.setMessage("one --> " + req.getHead() + ":" + req.getBody());
		res.setSuccess(true);
		res.setNums(req.getNums().add(BigDecimal.ONE));
		res.setTs(req.getTs());

		System.out.println(req);
		System.out.println(res);

		return res;
	}
}
