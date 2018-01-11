package com.cangoframework.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cangoframework.task.util.StringX;

public abstract class TaskObject {
	protected final Logger logger = LoggerFactory.getLogger(TaskObject.class);
	private String name;
	private String describe;
	private boolean traceOn;
	protected final Properties extendProperties;

	public TaskObject() {

		this.name = "Unnamed";
		this.describe = "No describe";
		this.traceOn = false;

		this.extendProperties = new Properties();
	}

	public final String getDescribe() {
		return this.describe;
	}

	public final void setDescribe(String describe) {
		this.describe = describe;
	}

	public final String getName() {
		return this.name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getProperty(String property) {
		return this.extendProperties.getProperty(property);
	}

	public final void setProperty(String name, String value) {
		if (value != null)
			this.extendProperties.setProperty(name, SystemHelper.replacePropertyTags(value));
		else
			this.extendProperties.setProperty(name, value);
	}

	public final boolean isTraceOn() {
		return this.traceOn;
	}

	public final void setTraceOn(boolean traceOn) {
		this.traceOn = traceOn;
	}

	protected final void trace(TaskObject obj) {
		if (isTraceOn())
			this.logger.info(obj.toString());
		else
			this.logger.trace(obj.toString());
	}

	public final int getProperty(String property, int defaultValue) {
		int value = defaultValue;
		String v = this.extendProperties.getProperty(property);
		if (v != null)
			try {
				value = Integer.parseInt(v);
			} catch (Exception ex) {
				value = defaultValue;
			}

		return value;
	}

	public final double getProperty(String property, double defaultValue) {
		double value = defaultValue;
		String v = this.extendProperties.getProperty(property);
		if (v != null)
			try {
				value = Double.parseDouble(v);
			} catch (Exception ex) {
				value = defaultValue;
			}

		return value;
	}

	public final boolean getProperty(String property, boolean defaultValue) {
		boolean value = defaultValue;
		String v = this.extendProperties.getProperty(property);
		if (v != null)
			value = StringX.parseBoolean(v);
		return value;
	}

	public final Date getProperty(String property, Date defaultValue) {
		Date value = defaultValue;
		String v = this.extendProperties.getProperty(property);
		if (v != null)
			value = StringX.parseDate(v);
		return value;
	}

	public final Date getProperty(String property, Date defaultValue, String format) {
		Date value = defaultValue;
		String v = this.extendProperties.getProperty(property);
		if (v != null) {
			SimpleDateFormat df = new SimpleDateFormat(format);
			try {
				value = df.parse(v);
			} catch (Exception ex) {
				value = defaultValue;
			}
		}
		return value;
	}

	public final String getProperty(String property, String defaultValue) {
		return this.extendProperties.getProperty(property, defaultValue);
	}

	public final String[] getProperties() {
		return ((String[]) (String[]) this.extendProperties.keySet().toArray(new String[0]));
	}

	public String toString() {
		return "正在运行"+this.getClass().getSimpleName()+":"+getName() + "[" + getDescribe() + "]";
	}
}