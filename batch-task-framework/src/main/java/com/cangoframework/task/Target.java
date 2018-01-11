package com.cangoframework.task;

import java.util.LinkedHashMap;
import java.util.Map;

import com.cangoframework.task.util.Tools;

public final class Target extends TaskObject implements Runnable {
	private boolean parallelRun;
	private int parallelNumber;
	private int lastUnitExecuteStatus;
	private ExecuteUnit rootUnit;
	private ExecuteUnit lastUnit;
	private ExecuteUnit currentUnit;
	private ParallelTable parallelTable;
	private Map units;
	private Task task;
	private boolean enabled;
	
	private String startTime;
	private String endTime;
	private String type;

	public Target() {
		this.parallelRun = false;
		this.parallelNumber = 10;
		this.lastUnitExecuteStatus = 0;
		this.rootUnit = null;
		this.lastUnit = null;
		this.currentUnit = null;
		this.parallelTable = null;

		this.units = new LinkedHashMap();
		this.task = null;
		this.enabled = true;
	}

	public final void addUnit(ExecuteUnit unit) {
		unit.setTarget(this);
		this.units.put(unit.getName(), unit);
		fireTaskEvent(8, unit);
	}

	public final void removeUnit(ExecuteUnit unit) {
		if ((unit != null) && (this.units.containsKey(unit.getName()))) {
			this.units.remove(unit.getName());
			fireTaskEvent(9, unit);
		}
	}

	public final void removeUnit(String name) {
		if (this.units.containsKey(name)) {
			ExecuteUnit u = (ExecuteUnit) this.units.get(name);
			this.units.remove(name);
			fireTaskEvent(9, u);
		}
	}

	public final ExecuteUnit getUnit(String name) {
		return ((ExecuteUnit) this.units.get(name));
	}

	public final ExecuteUnit[] getUnits() {
		ExecuteUnit[] u = new ExecuteUnit[this.units.size()];
		return ((ExecuteUnit[]) (ExecuteUnit[]) this.units.values().toArray(u));
	}

	public final void run() {
		if (isParallelRun())
			runParallel();
		else
			runSerial();
	}

	public final void runParallel() {
		ThreadGroup threadGroup = new ThreadGroup("runTarget" + getName());
		this.logger.info("target[Parallel][ParallelNumber="
				+ getParallelNumber() + "] start");
		while (true) {
			ExecuteUnit unit = this.parallelTable.getExecuteUnit();

			if ((unit != null)
					&& (threadGroup.activeCount() < getParallelNumber())) {
				unit.setExecuteStatus(0);
				new Thread(threadGroup, unit).start();
			} else {
				if (this.parallelTable.isAllUnitRuning()) {
					break;
				}

				if (threadGroup.activeCount() == 0) {
					this.logger.warn(this.parallelTable.getNotRunUnit());
					break;
				}
				try {
					Thread.sleep(20L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}

		if (threadGroup.activeCount() == 0)
			return;
		try {
			Thread.sleep(100L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public final void runSerial() {
		this.currentUnit = getRootUnit();
		if (this.currentUnit == null) {
			this.logger.error("目标" + getName() + "的根单元没有在路由表中定义！");
			return;
		}
		fireTaskEvent(2, this.currentUnit);

		while (this.currentUnit != null) {
			trace(this.currentUnit);
			fireTaskEvent(4, this.currentUnit);
			this.currentUnit.replaceTaskVar();

			this.currentUnit.run();
			this.lastUnitExecuteStatus = this.currentUnit.getExecuteStatus();
			this.lastUnit = this.currentUnit;
			fireTaskEvent(5, this.currentUnit);
			this.currentUnit = this.currentUnit.nextUnit();
		}
		fireTaskEvent(3, getLastUnit());
	}

	public final boolean isParallelRun() {
		return this.parallelRun;
	}

	public final void setParallelRun(boolean parallelRun) {
		this.parallelRun = parallelRun;
	}

	public final int getParallelNumber() {
		return this.parallelNumber;
	}

	public final void setParallelNumber(String parallelNumber) {
		if (parallelNumber.matches("[0-9]*"))
			setParallelNumber(Integer.parseInt(parallelNumber));
	}

	public final void setParallelNumber(int parallelNumber) {
		this.parallelNumber = parallelNumber;
	}

	public final ParallelTable getParalleTable() {
		if (this.parallelTable == null)
			this.parallelTable = new ParallelTable(this);

		return this.parallelTable;
	}

	public final int executeUnit(String unitName) {
		ExecuteUnit unit = getUnit(unitName);
		if (unit == null) {
			this.logger.error("Unit <" + getTask().getName() + "." + getName()
					+ "." + unitName + "> not exists!");
			return 2;
		}
		trace(unit);
		fireTaskEvent(4, unit);
		unit.replaceTaskVar();// 打印新旧属性值

		// 自动获取字段值
		Tools.autoSetFieldValue(unit);
		// 调用实现类方法执行
		this.lastUnitExecuteStatus = unit.execute();
		fireTaskEvent(5, unit);
		return this.lastUnitExecuteStatus;
	}

	public final Task getTask() {
		return this.task;
	}

	protected final void setTask(Task task) {
		this.task = task;
		setTraceOn(task.isTraceOn());
	}

	public final ExecuteUnit getCurrentUnit() {
		return this.currentUnit;
	}

	private ExecuteUnit getRootUnit() {
		if (this.rootUnit == null) {
			ExecuteUnit u = null;
			if (!(this.units.isEmpty())) {
				ExecuteUnit[] allUnits = getUnits();
				for (int i = 0; i < allUnits.length; ++i)
					if (allUnits[i].nextUnit() != null) {
						u = allUnits[i];
						break;
					}
			}

			this.rootUnit = u;
		}
		return this.rootUnit;
	}

	public final int getLastUnitExecuteStatus() {
		return this.lastUnitExecuteStatus;
	}

	public final boolean isEnabled() {
		return this.enabled;
	}

	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public final int getUnitCount() {
		return this.units.size();
	}

	private void fireTaskEvent(int eventType, ExecuteUnit relativeUnit) {
		if (this.task != null)
			this.task.fireTaskEvent(eventType, this, relativeUnit, null);
	}

	public final ExecuteUnit getLastUnit() {
		return this.lastUnit;
	}

	public final String getStartTime() {
		return startTime;
	}

	public final void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public final String getEndTime() {
		return endTime;
	}

	public final void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public final String getType() {
		return type;
	}

	public final void setType(String type) {
		this.type = type;
	}
	
}