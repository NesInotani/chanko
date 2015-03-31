package com.github.chanko.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import javax.xml.ws.Service;

import org.jboss.logging.Logger;

import com.github.chanko.ws.HelloPort;
import com.github.chanko.ws.Res;

@WebServlet("hello")
public class HelloClient extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(HelloClient.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Service service = Service.create(new URL(
				"http://localhost:8080/chanko-ws/Hello?wsdl"), new QName(
				"http://ws.chanko.github.com/", "HelloService"));
		HelloPort port = service.getPort(new QName(
				"http://ws.chanko.github.com/", "HelloPort"), HelloPort.class);

		log.info("------------------- oneway --------------------");
		port.sayOneway();

		log.info("------------------- sync --------------------");
		Res r = port.say();
		log.info(r.toString());

		log.info("------------------- callback --------------------");
		Future<Res> fr = port.sayAsync(new AsyncHandler<Res>() {
			@Override
			public void handleResponse(Response<Res> res) {
				try {
					log.info(res.get().toString());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		});

		log.info("------------------- polling --------------------");
		Response<Res> rr = port.sayAsync();
		while (rr.isDone()) {
			try {
				TimeUnit.SECONDS.sleep(4);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		try {
			log.info(rr.get().toString());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
	}

}
