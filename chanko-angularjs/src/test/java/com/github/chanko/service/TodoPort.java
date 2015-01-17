package com.github.chanko.service;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface TodoPort {
	@WebMethod
	Result register(TodoParam param);
}
