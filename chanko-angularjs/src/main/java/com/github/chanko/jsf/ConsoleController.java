package com.github.chanko.jsf;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Model;
import javax.inject.Inject;

import com.github.chanko.cdi.ElapsedTime;
import com.github.chanko.jpa.Todo;
import com.github.chanko.service.ServiceLocator;
import com.github.chanko.service.TodoServiceHome;

@Model
public class ConsoleController {
	private List<Todo> todos;

	public List<Todo> getTodos() {
		return todos;
	}

	public void setTodos(List<Todo> todos) {
		this.todos = todos;
	}

	@Inject
	private ServiceLocator locator;

	private TodoServiceHome service;

	@PostConstruct
	public void init() {
		service = locator.get(TodoServiceHome.class);
	}

	@ElapsedTime
	public void list() {
		todos = service.findAll();
	}

	@ElapsedTime
	public void remove(String id) {
		service.remove(id);
		todos = service.findAll();
	}
	
	public void hello() {
		System.out.println("hello");
	}
}
