package com.github.chanko.valve;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.jboss.logging.Logger;

public class SimpleValve extends ValveBase {
	private static final Logger LOG = Logger.getLogger(SimpleValve.class);

	private final AtomicInteger requestCount = new AtomicInteger(0);

	/**
	 * 累積リクエスト数
	 * @return リクエスト数
	 */
	public int getRequestCount() {
		return requestCount.intValue();
	}

	/**
	 * 10秒に1回実行されるバックグラウンドプロセス.
	 */
	@Override
	public void backgroundProcess() {
		LOG.info("execute background process");
	}

	/**
	 * リクエストごとに処理.
	 */
	@Override
	public void invoke(Request request, Response response) throws IOException,
			ServletException {

		if (request.getRequestURI().startsWith(
				request.getContextPath() + "/goto503")) {
			response.sendError(Response.SC_SERVICE_UNAVAILABLE);
			LOG.error("request /goto503 error");
		} else {
			int count = this.requestCount.incrementAndGet();
			LOG.infof("invoke   %d", count);
			next.invoke(request, response);
		}
	}
}
