package com.github.chanko.service;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.jws.WebMethod;
import javax.jws.WebService;

import com.github.chanko.jpa.Todo;

@WebService
public interface TodoServiceHome {
	@WebMethod
	Result register(TodoParam param);

	@WebMethod
	List<Todo> findAll();

	@WebMethod
	Result remove(String id);

	@Local
	public interface ILocal extends TodoServiceHome {
	}

	@Remote
	public interface IRemote extends TodoServiceHome {
	}
}
