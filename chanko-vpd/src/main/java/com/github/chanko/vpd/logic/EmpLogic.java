package com.github.chanko.vpd.logic;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.github.chanko.vpd.entity.Emp;

@Stateless
public class EmpLogic {
	@PersistenceContext
	private EntityManager em;

	/**
	 * 全件取得。
	 * 
	 * @return 検索結果。
	 */
	public List<Emp> findAll() {
		return em.createNamedQuery("Emp.findAll", Emp.class).getResultList();
	}

	/**
	 * VPDによりdeptno=10のものだけ取得。
	 * 
	 * @return　検索結果。
	 */
	public List<Emp> findAllByUser01() {
		em.setProperty("deptno", "10");
		return em.createNamedQuery("Emp.findAll", Emp.class).getResultList();
	}

	/**
	 * VPDによりdeptno=20のものだけ取得。
	 * 
	 * @return 検索結果
	 */
	public List<Emp> findAllByUser02() {
		em.setProperty("deptno", "20");
		return em.createNamedQuery("Emp.findAll", Emp.class).getResultList();
	}

	public List<Emp> findAllNative() {
		return em.createNamedQuery("Emp.findAllNative", Emp.class)
				.getResultList();
	}

	/**
	 * VPDによりdeptno=10のものだけ取得。
	 * 
	 * @return　検索結果。
	 */
	public List<Emp> findAllNativeByUser01() {
		em.setProperty("deptno", "10");
		return em.createNamedQuery("Emp.findAllNative", Emp.class)
				.getResultList();
	}

	/**
	 * VPDによりdeptno=20のものだけ取得。
	 * 
	 * @return 検索結果
	 */
	public List<Emp> findAllNativeByUser02() {
		em.setProperty("deptno", "20");
		return em.createNamedQuery("Emp.findAllNative", Emp.class)
				.getResultList();
	}
}
