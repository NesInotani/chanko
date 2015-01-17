package com.github.chanko.service;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TodoParam implements Serializable {
	private static final long serialVersionUID = 1L;
	private String title;
	private boolean done;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	@Override
	public String toString() {
		return "title=" + title + ", done=" + done;
	}
}
