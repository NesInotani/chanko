package com.github.chanko.socket.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;

class Worker implements Callable<Void> {
	private byte[] readBytes;
	private Socket socket;

	Worker(Socket socket, Configuration conf) {
		this.socket = socket;
		readBytes = new byte[conf.getWorkerMaxBytes()];
	}

	@Override
	public Void call() throws Exception {
		String threadName = Thread.currentThread().getName();

		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		int readBytesCount = 0;

		System.out.println("work on " + threadName);

		while ((readBytesCount = in.read(readBytes)) != -1) {
			for (int i = 0; i < readBytesCount; i++) {
				System.out.printf("%c", readBytes[i]);
				out.write(readBytes);
			}
		}

		socket.close();

		System.out.println("socket closed by " + threadName);
		return null;
	}

}
