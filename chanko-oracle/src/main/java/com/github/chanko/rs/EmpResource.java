package com.github.chanko.rs;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.chanko.jpa.Emp;

@Path("emp")
@Stateless
public class EmpResource {
	@PersistenceContext(unitName = "primary")
	private EntityManager em;

	public void setEm(EntityManager em) {
		this.em = em;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Emp> findAll() {
		return em.createNamedQuery("Emp.all", Emp.class).getResultList();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{name}")
	public List<Emp> matchName(@PathParam("name") String name) {
		return em.createNamedQuery("Emp.matchName", Emp.class)
				.setParameter("name", name).getResultList();
	}
}
