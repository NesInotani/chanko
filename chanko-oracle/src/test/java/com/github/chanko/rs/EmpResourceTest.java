package com.github.chanko.rs;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

import com.github.chanko.jpa.Emp;

public class EmpResourceTest {
	@Test
	public void test_local() {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("testpu");
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();

		try {
			et.begin();
			EmpResource resource = new EmpResource();
			resource.setEm(em);
			List<Emp> result = resource.findAll();
			
			assertThat(result.size(), is(23));
		} finally {
			et.rollback();
		}
	}
}
