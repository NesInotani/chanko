package com.github.chanko.mbean;

import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public class Stats implements StatsMBean {
	private static final Logger LOG = Logger.getLogger(Stats.class.getName());

	private MBeanServer server;

	private ObjectName httpConnectorObjectName;

	private static final String[] targetAttr = new String[] { "processingTime",
			"requestCount" };

	@PostConstruct
	public void init() {
		server = ManagementFactory.getPlatformMBeanServer();

		try {
			httpConnectorObjectName = new ObjectName(
					"jboss.as:subsystem=web,connector=http");
		} catch (MalformedObjectNameException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getThroughput() {
		try {
			Set<ObjectName> result = server.queryNames(httpConnectorObjectName,
					null);

			if (result.isEmpty()) {
				return 0;
			}

			int processingTime = 0;
			int requestCount = 0;

			for (ObjectName oname : result) {
				AttributeList attrList = server
						.getAttributes(oname, targetAttr);
				if (attrList.size() == 2) {
					processingTime += (int) ((Attribute) attrList.get(0))
							.getValue();

					requestCount += (int) ((Attribute) attrList.get(1))
							.getValue();

					LOG.info(targetAttr[0] + "=" + processingTime + ", "
							+ targetAttr[1] + "=" + requestCount);
				}
			}

			if (requestCount == 0) {
				return 0;
			}

			return processingTime / requestCount;

		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
