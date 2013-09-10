package com.github.chanko.socket.server;

class Configuration {
	private int port = 8999;
	private int backlog = 10;
	private int acceptorCount = 3;
	private int workerCount = 10;
	private int workerMaxBytes = 10;

	int getPort() {
		return port;
	}

	void setPort(int port) {
		this.port = port;
	}

	int getBacklog() {
		return backlog;
	}

	void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	int getAcceptorCount() {
		return acceptorCount;
	}

	void setAcceptorCount(int acceptorCount) {
		this.acceptorCount = acceptorCount;
	}

	int getWorkerCount() {
		return workerCount;
	}

	void setWorkerCount(int workerCount) {
		this.workerCount = workerCount;
	}

	int getWorkerMaxBytes() {
		return workerMaxBytes;
	}

	void setWorkerMaxBytes(int workerMaxBytes) {
		this.workerMaxBytes = workerMaxBytes;
	}

}
