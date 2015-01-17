package com.github.chanko.service;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import com.github.chanko.cdi.ElapsedTime;
import com.github.chanko.ejb.TodoBean;
import com.github.chanko.jpa.Todo;

// for JAX-RS
@Path("todo")
// for JAX-WS
@WebService
// for EJB
@Stateless
@LocalBean
@Local(TodoServiceHome.ILocal.class)
@Remote(TodoServiceHome.IRemote.class)
// for CDI
@Named
public class TodoService implements TodoServiceHome {
	@Inject
	private TodoBean todoBean;

	// for JAX-RS
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// for JAX-WS
	@WebMethod
	public Result register(TodoParam param) {
		try {
			todoBean.register(param.getTitle(), param.isDone());

			return new Result().success();

		} catch (Throwable t) {
			try {
				SOAPFault fault = MessageFactory.newInstance().createMessage()
						.getSOAPBody().addFault();
				fault.setFaultCode(SOAPConstants.SOAP_RECEIVER_FAULT);
				fault.setFaultString("Exception. Detail:" + t.getMessage());
				throw new SOAPFaultException(fault);
			} catch (SOAPException e) {
				throw new SOAPFaultException(null);
			}
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@WebMethod
	@ElapsedTime
	public List<Todo> findAll() {
		return todoBean.findAll();
	}

	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@WebMethod
	public Result remove(@PathParam("id") String id) {
		todoBean.remove(id);

		return new Result().success();
	}

}
