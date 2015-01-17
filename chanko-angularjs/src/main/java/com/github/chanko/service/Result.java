package com.github.chanko.service;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Result implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String SUCCESS = "success";
	public static final String FAILURE = "failure";

	private String result;
	private long count;

	public Result() {
	}

	public Result success() {
		this.result = SUCCESS;
		this.count = 1;
		return this;
	}

	public Result failure() {
		this.result = FAILURE;
		this.count = -1;
		return this;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
