package com.github.chanko.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.ws.soap.SOAPFaultException;

@Provider
public class SOAPFaultExceptionMapper implements
		ExceptionMapper<SOAPFaultException> {

	@Override
	public Response toResponse(SOAPFaultException e) {
		Result r = new Result();
		r.setResult("failure");
		r.setCount(-1);

		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.type(MediaType.APPLICATION_JSON).entity(r).build();
	}

}
