package com.github.chanko.vpd.resource;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.logging.Logger;

import com.github.chanko.vpd.logic.EmpLogic;

@Path("emp")
public class EmpResource {
	private static final Logger log = Logger.getLogger(EmpResource.class);

	@Inject
	private EmpLogic emp;

	@GET
	@Path("app")
	public Response findAll() {
		log.info("========================= findAll by app =========================");
		emp.findAll().forEach(e -> log.info(e.toString()));

		log.info("========================= findAllNative by app =========================");
		emp.findAllNative().forEach(e -> log.info(e.toString()));

		return Response.status(Status.OK).build();
	}

	@GET
	@Path("user01")
	public Response findAllByUser01() {
		log.info("========================= findAll by user01 =========================");
		emp.findAllByUser01().forEach(e -> log.info(e.toString()));

		log.info("========================= findAllNative by user01 =========================");
		emp.findAllNativeByUser01().forEach(e -> log.info(e.toString()));

		return Response.status(Status.OK).build();
	}

	@GET
	@Path("user02")
	public Response findAllByUser02() {
		log.info("========================= findAll by user02 =========================");
		emp.findAllByUser02().forEach(e -> log.info(e.toString()));

		log.info("========================= findAllNative by user02 =========================");
		emp.findAllNativeByUser02().forEach(e -> log.info(e.toString()));

		return Response.status(Status.OK).build();
	}
}
