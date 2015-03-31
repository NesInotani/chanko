package com.github.chanko.ws;

import java.util.concurrent.Future;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

@WebService(name = "Hello")
@SOAPBinding(style = Style.RPC)
public interface HelloPort {
	@WebResult(name = "result")
	public Res say();

	@WebMethod(operationName = "say")
	public Response<Res> sayAsync();

	@WebMethod(operationName = "say")
	public Future<Res> sayAsync(
			@WebParam(name = "asyncHandler") AsyncHandler<Res> callback);

	@WebMethod
	@Oneway
	public void sayOneway();
}
