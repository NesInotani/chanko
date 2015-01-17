package com.github.chanko.cdi;

import java.io.Serializable;

import javax.enterprise.context.Dependent;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.logging.Logger;

@Interceptor
@Dependent
@ElapsedTime
public class ElapsedTimeInterceptor implements Serializable {
	private static final long serialVersionUID = 1L;

	@AroundInvoke
	public Object invoke(InvocationContext ic) throws Exception {
		Logger log = Logger
				.getLogger(ic.getTarget().getClass().getSuperclass());
		String methodName = ic.getMethod().getName();

		long begin = System.currentTimeMillis();
		Object result = ic.proceed();
		long end = System.currentTimeMillis();

		log.infof("METHOD=%s ELAPSEDTIME=%d msec", methodName, (end - begin));

		return result;
	}
}
