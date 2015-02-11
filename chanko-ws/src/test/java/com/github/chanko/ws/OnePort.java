package com.github.chanko.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface OnePort {
	@WebMethod
	public Res ok(Req req);

}
