package com.github.chanko.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Listener {
	public static void main(String[] args) {
		Listener listner = new Listener();
		listner.listen();
	}

	private ExecutorService acceptorPool;
	private Configuration conf;

	public Listener() {
		conf = new Configuration();
		acceptorPool = Executors.newFixedThreadPool(conf.getAcceptorCount());
	}

	public void listen() {
		try {
			int acceptorCount = conf.getAcceptorCount();

			ServerSocket serverSocket = new ServerSocket(conf.getPort(),
					conf.getBacklog());

			System.out.println("listening on " + conf.getPort());

			for (int i = 0; i < acceptorCount; i++) {
				acceptorPool.submit(new Acceptor(serverSocket, conf));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
