package com.github.chanko.jsf;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.servlet.http.Part;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

@Named
@SessionScoped
public class UploadPage {
	private Logger logger = Logger.getLogger(UploadPage.class
			.getCanonicalName());

	private Part file;

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	private static final String HOST = "192.168.33.12";
	private static final int PORT = 21;
	private static final String USER = "ftpusr1";
	private static final String PASS = USER;

	public String upload() {
		// logging file information
		logger.info("name: " + file.getSubmittedFileName());
		logger.info("name: " + file.getName());
		logger.info("size: " + file.getSize());
		logger.info("type: " + file.getContentType());
		for (String s : file.getHeaderNames()) {
			logger.info(s + ": " + file.getHeader(s));
		}

		FTPClient client = new FTPClient();
		try (InputStream is = file.getInputStream()) {
			// connect to server
			client.connect(HOST, PORT);
			int replyCode = client.getReplyCode();
			logger.info("connect to " + HOST + ":" + PORT + " : " + replyCode);
			if (FTPReply.isPositiveCompletion(replyCode)) {
				logger.info("connect success");
			} else {
				throw new RuntimeException("cannot connect to " + HOST + ":"
						+ PORT);
			}

			// login
			client.login(USER, PASS);
			replyCode = client.getReplyCode();
			logger.info("login " + USER + " : " + replyCode);
			if (FTPReply.isPositiveCompletion(replyCode)) {
				logger.info("loggin success");
			} else {
				throw new RuntimeException("cannot login " + USER);
			}
			logger.info("current working directory : "
					+ client.printWorkingDirectory());

			// set binary mode
			client.setFileType(FTP.BINARY_FILE_TYPE);

			// put file
			String putPath = file.getSubmittedFileName();
			client.storeFile(putPath, is);
			replyCode = client.getReplyCode();
			logger.info("put " + putPath + " : " + replyCode);
			if (FTPReply.isPositiveCompletion(replyCode)) {
				logger.info("send file complete");
			} else {
				throw new RuntimeException("cannot stored file");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (client != null) {
				try {
					client.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return "index.xhtml";
	}

}
