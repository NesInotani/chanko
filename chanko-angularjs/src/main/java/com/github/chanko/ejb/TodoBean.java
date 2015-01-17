package com.github.chanko.ejb;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import org.jboss.logging.Logger;

import com.github.chanko.jpa.Todo;

@Stateless
@Named
public class TodoBean {
	@PersistenceContext
	private EntityManager em;

	@Inject
	private Logger log;

	public String register(@NotNull String title, boolean done) {
		Todo todo = new Todo(title, done);
		em.persist(todo);

		return todo.getId();
	}

	@SuppressWarnings("unchecked")
	public List<Todo> findAll() {
		return em.createQuery("select t from Todo t").getResultList();
	}

	public void edit(@NotNull String id, @NotNull String title, boolean done) {
		Todo todo = em.find(Todo.class, id);
		todo.setTitle(title);
		todo.setDone(done);
		em.persist(todo);
	}

	public void remove(@NotNull String id) {
		log.infof("DELETE id %s", id);
		Todo todo = em.find(Todo.class, id);
		em.remove(todo);
	}
}
