package com.cangoframework.task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cangoframework.task.util.Tools;

public abstract class ExecuteUnit extends TaskObject implements Runnable {
	private boolean allowManualExecute;
	private Target target;
	private ArrayList routeTable;
	private Properties extendPropertiesBackup;
	private int executeStatus;
	private Date beginTime;
	private Date endTime;
	private SimpleDateFormat sdf;

	public ExecuteUnit() {
		this.allowManualExecute = false;
		this.target = null;
		this.routeTable = new ArrayList(3);
		this.extendPropertiesBackup = new Properties();
		this.executeStatus = -1;

		this.sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
	}

	public final Target getTarget() {
		return this.target;
	}

	protected final void setTarget(Target target) {
		this.target = target;
	}

	public final void addRoute(Route route) {
		if (!(this.routeTable.contains(route))) {
			this.routeTable.add(route);
			fireTaskEvent(10, route);
		}
	}

	public final void removeRoute(Route route) {
		if (this.routeTable.contains(route)) {
			this.routeTable.remove(route);
			fireTaskEvent(11, route);
		}
	}

	public final Route[] getRouteTable() {
		Route[] r = new Route[this.routeTable.size()];
		return ((Route[]) (Route[]) this.routeTable.toArray(r));
	}

	public String getExecuteInfo() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("单元名称[" + getName() + "]");
		sbf.append("单元执行状态[" + getExecuteStatus() + "]");
		double iTimeConsuming = (this.endTime.getTime() - this.beginTime.getTime()) / 1000.0D;
		if (iTimeConsuming > 36000.0D) {
			sbf.append("单元执行时间[WARN][" + iTimeConsuming + "]秒");
		} else
			sbf.append("单元执行时间[" + iTimeConsuming + "]秒");

		sbf.append("开始时间[" + this.sdf.format(this.beginTime) + "]");
		sbf.append("结束时间[" + this.sdf.format(this.endTime) + "]");

		return sbf.toString();
	}

	public final void run() {
		this.logger.info("正在运行的类名：" + this.getClass().getName());
		Tools.autoSetFieldValue(this);// 自动注入

		this.executeStatus = 4;
		this.beginTime = Calendar.getInstance().getTime();
		this.logger.debug("ExecuteUnit[" + getName() + "] Starting ...");
		this.executeStatus = execute();
		this.endTime = Calendar.getInstance().getTime();
		this.logger.info("ExecuteUnit[" + getName() + "]OK[" + getExecuteInfo() + "]");
	}

	public synchronized void setExecuteStatus(int executeStatus) {
		this.executeStatus = executeStatus;
	}

	public synchronized int getExecuteStatus() {
		return this.executeStatus;
	}

	public abstract int execute();

	public final ExecuteUnit nextUnit() {
		ExecuteUnit u = null;
		Route r = null;
		int lastStatus = this.target.getLastUnitExecuteStatus();

		for (int i = 0; i < this.routeTable.size(); ++i) {
			r = (Route) this.routeTable.get(i);
			if (lastStatus == r.executeStatus()) {
				u = r.nextUnit();
				break;
			}
		}
		return u;
	}

	public final int getRouteCount() {
		return this.routeTable.size();
	}

	protected final void replaceTaskVar() {
		Enumeration<?> en = this.extendProperties.keys();
		boolean trace = this.logger.isTraceEnabled();
		if (trace)
			this.logger.trace("TaskVar replace start...");
		while (en.hasMoreElements()) {
			String n = (String) en.nextElement();
			String v = this.extendProperties.getProperty(n);
			String ov = this.extendPropertiesBackup.getProperty(n);
			if (ov != null)
				v = ov;
			if ((v != null) && (v.length() > 7)) {
				String nv = replaceVar(v);
				if (!(nv.equals(v))) {
					this.extendPropertiesBackup.setProperty(n, v);
					this.extendProperties.setProperty(n, nv);
				}
			}
			if (trace) {
				this.logger.trace(n + " old value : " + v);
				this.logger.trace(n + " new value : " + this.extendProperties.getProperty(n));
			}
		}
	}

	private String replaceVar(String srcString) {
		StringBuffer sb = new StringBuffer();
		String var = "\\{\\$(?:(TASK)|(?:TARGET))\\.([^\\{\\}]*)\\}";
		Pattern p = Pattern.compile(var);
		Matcher m = p.matcher(srcString);
		while (m.find()) {
			String pv = null;
			if (m.group(1) == null)
				pv = this.target.getProperty(m.group(2), "");
			else
				pv = this.target.getTask().getProperty(m.group(2), "");

			if (pv == null)
				pv = "";
			m.appendReplacement(sb, pv);
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public final boolean isAllowManualExecute() {
		return this.allowManualExecute;
	}

	public final void setAllowManualExecute(boolean allowStandaloneRun) {
		this.allowManualExecute = allowStandaloneRun;
	}

	protected final void transferUnitProperties() {
		Enumeration pe = this.extendProperties.keys();
		while (pe.hasMoreElements()) {
			String p = (String) pe.nextElement();
			String v = this.extendProperties.getProperty(p);
			if (p.startsWith("unit."))
				Tools.setPropertyX(this, p.substring(5), v, false);
		}
	}

	protected final void transferObjectProperties(Object internalObject) {
		if (internalObject == null)
			return;
		String cn = internalObject.getClass().getName() + ".";
		Enumeration<?> pe = this.extendProperties.keys();
		while (true) {
			String p;
			while (true) {
				if (!(pe.hasMoreElements()))
					return;
				p = (String) pe.nextElement();
				if (p.startsWith(cn))
					break;
			}
			String v = this.extendProperties.getProperty(p);
			String n = p.substring(cn.length());
			Tools.setPropertyX(internalObject, n, v, false);
		}
	}

	private void fireTaskEvent(int eventType, Route relativeRoute) {
		if ((this.target != null) && (this.target.getTask() != null))
			this.target.getTask().fireTaskEvent(eventType, this.target, this, relativeRoute);
	}
}