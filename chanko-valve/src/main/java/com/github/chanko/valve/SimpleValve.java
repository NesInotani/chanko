package com.github.chanko.valve;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.jboss.logging.Logger;

/**
 * Valveクラスの動作確認. 実行中リクエストを保持する.
 * 
 * @author nobrooklyn
 */
public class SimpleValve extends ValveBase {
	private static final Logger LOG = Logger.getLogger(SimpleValve.class);

	private final ConcurrentMap<Long, String> requesURLs = new ConcurrentHashMap<>();

	private final String[] resType = new String[0];

	/**
	 * 処理中リクエストURL(String配列)
	 */
	public String[] getRequestURLs() {
		return requesURLs.values().toArray(resType);
	}

	/**
	 * 処理中リクエストURL(Map)
	 */
	public Map<Long, String> getRequestURLsMap() {
		return requesURLs;
	}

	/**
	 * 処理中リクエスト数
	 */
	public int getRequestCountCurrent() {
		return requesURLs.size();
	}

	private final AtomicInteger requestCount = new AtomicInteger(0);

	/**
	 * 累積リクエスト数
	 * 
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

		} else {
			this.requestCount.incrementAndGet();

			long threadId = Thread.currentThread().getId();
			requesURLs.put(threadId, request.getRequestURI());

			next.invoke(request, response);

			requesURLs.remove(threadId);
		}
	}
}
