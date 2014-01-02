package com.github.chanko.web.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

@WebServlet(urlPatterns = "/resourcedump")
public class ResourcePathDumperServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private Logger LOG;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doPost(req, res);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		Enumeration<URL> resourceURLs = Thread.currentThread()
				.getContextClassLoader().getResources(".");

		res.getWriter().write("<html><body><h1>Resource Paths</h1><ul>");

		for (URL url : Collections.list(resourceURLs)) {
			String path = url.toString();
			LOG.info(path);

			res.getWriter().write("<li>" + path + "</li>");
		}

		res.getWriter().write("</ul></body></html>");

	}
}
