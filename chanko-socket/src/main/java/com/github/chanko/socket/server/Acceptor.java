package com.github.chanko.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Acceptor implements Callable<Void> {
	private ServerSocket serverSocket;
	private Configuration conf;
	private static ExecutorService workerPool;

	Acceptor(ServerSocket serverSocket, Configuration conf) {
		this.serverSocket = serverSocket;
		this.conf = conf;

		if (workerPool == null) {
			workerPool = Executors.newFixedThreadPool(conf.getWorkerCount());
		}
	}

	static synchronized void shutdown() {
		if (workerPool != null) {
			workerPool.shutdownNow();
			workerPool = null;
		} else {
			System.out.println("Worker Pool already shutdown.");
		}
	}

	@Override
	public Void call() {
		String threadName = Thread.currentThread().getName();

		if (serverSocket == null || serverSocket.isClosed()) {
			System.err.println("ServerSocket is null or already closed.");
			return null;
		}

		Socket socket = null;
		try {
			System.out.println("accepted by " + threadName);
			while ((socket = serverSocket.accept()) != null) {

				Worker worker = new Worker(socket, conf);
				workerPool.submit(worker);

			}

		} catch (IOException e) {
			e.printStackTrace();
			try {
				if (socket != null && !socket.isClosed()) {
					socket.close();
					System.out.println("socket closed by " + threadName);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}
}
