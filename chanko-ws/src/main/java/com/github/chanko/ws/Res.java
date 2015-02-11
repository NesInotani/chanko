package com.github.chanko.ws;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Res {
	private String message;
	private boolean success = false;
	private BigDecimal nums;
	@XmlJavaTypeAdapter(TimestampAdapter.class)
	private Timestamp ts;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public BigDecimal getNums() {
		return nums;
	}

	public void setNums(BigDecimal nums) {
		this.nums = nums;
	}

	public Timestamp getTs() {
		return ts;
	}

	public void setTs(Timestamp ts) {
		this.ts = ts;
	}

	@Override
	public String toString() {
		return "Res [message=" + message + ", success=" + success + ", nums="
				+ nums + ", ts=" + ts + "]";
	}

}
