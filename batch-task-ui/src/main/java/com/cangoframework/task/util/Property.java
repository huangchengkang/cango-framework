package com.cangoframework.task.util;

import java.io.Serializable;

public class Property implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte displayFormat = 0;
	public Object key;
	public Object value;

	public Property() {
		this.key = null;
		this.value = null;
	}

	public Property(Object paramObject1, Object paramObject2) {
		this.key = paramObject1;
		this.value = paramObject2;
	}

	public Property(Object paramObject1, Object paramObject2, byte paramByte) {
		this.key = paramObject1;
		this.value = paramObject2;
		this.displayFormat = paramByte;
	}

	public Object getKey() {
		return this.key;
	}

	public Property setKey(Object paramObject) {
		this.key = paramObject;
		return this;
	}

	public Object getValue() {
		return this.value;
	}

	public Property setValue(Object paramObject) {
		this.value = paramObject;
		return this;
	}

	public Property setNameAndValue(Object paramObject1, Object paramObject2) {
		this.key = paramObject1;
		this.value = paramObject2;
		return this;
	}

	public Property setDisplayFormat(byte paramByte) {
		this.displayFormat = paramByte;
		return this;
	}

	public String toString() {
		switch (this.displayFormat) {
		case 0:
			return this.key.toString();
		case 1:
			return this.value.toString();
		case 2:
			return this.key.toString() + this.value.toString();
		}
		return this.key.toString();
	}
}