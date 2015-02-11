package com.github.chanko.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public class Two {
	@WebMethod
	public Res ok(Req req) {
		Res res = new Res();
		res.setMessage("two --> " + req.getHead() + ":" + req.getBody());

		return res;
	}
}
