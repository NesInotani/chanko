package com.github.chanko.web.servlet;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

@WebServlet(urlPatterns = "/sleep")
public class SleepServlet extends HttpServlet {

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
		String sec = req.getParameter("sec");
		String dat = req.getParameter("dat");

		if (sec == null || dat == null) {
			throw new ServletException("parameter sec or dat is null.");
		}

		int sleepSec = Integer.parseInt(sec);

		try {
			LOG.infof("data size %d : sleep %d", dat.length(), sleepSec);
			TimeUnit.SECONDS.sleep(sleepSec);
			LOG.infof("wake up");
		} catch (InterruptedException e) {
			LOG.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
}
