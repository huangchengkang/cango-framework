package com.cangoframework.task;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Task extends TaskObject {
	private boolean parallelRun = false;
	private boolean waitAllTargetsComplete = true;
	private Map targets = new LinkedHashMap();
	private ArrayList listeners = null;

	public Task() {
		this.listeners = new ArrayList();
	}

	public final void start() {
		start(true);
	}

	public final void start(boolean waitAllComplete) {
		fireTaskEvent(new TaskEvent(this, 0));
		this.waitAllTargetsComplete = waitAllComplete;
		trace(this);
		if (isParallelRun())
			runParallel();
		else
			runSerial();
		fireTaskEvent(new TaskEvent(this, 1));
	}

	public final void runTarget(String targetName, boolean newThread) {
		Target target = getTarget(targetName);
		if (target != null) {
			trace(target);
			if (newThread)
				new Thread(target).start();
			else
				target.run();
		}
	}

	public final int executeUnit(String targetName, String unitName) {
		Target target = getTarget(targetName);
		if (target != null) {
			trace(target);
			return target.executeUnit(unitName);
		}
		this.logger.error("Target <" + getName() + "." + targetName + "> not exists!");
		return 2;
	}

	private void runParallel() {
		Target target = null;
		Target[] allTarget = getTargets();
		ThreadGroup threadGroup = new ThreadGroup(getName());
		for (int i = 0; i < allTarget.length; ++i) {
			target = allTarget[i];
			if (!(target.isEnabled())) {
				break;
			}
			trace(target);
			new Thread(threadGroup, target, target.getName()).start();
		}
		if (this.waitAllTargetsComplete) {
			if (threadGroup.activeCount() == 0)
				return;
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
			}
		}
	}

	private void runSerial() {
		Target target = null;
		Target[] allTarget = getTargets();
		for (int i = 0; i < allTarget.length; ++i) {
			target = allTarget[i];
			if (!(target.isEnabled())) {
				break;
			}
			trace(target);
			target.run();
		}
	}

	public final boolean isParallelRun() {
		return this.parallelRun;
	}

	public final void setParallelRun(boolean parallelRun) {
		this.parallelRun = parallelRun;
	}

	public final void addTarget(Target target) {
		target.setTask(this);
		this.targets.put(target.getName(), target);
		fireTaskEvent(6, target, null, null);
	}

	public final void removeTarget(String name) {
		if (this.targets.containsKey(name)) {
			Target t = (Target) this.targets.get(name);
			this.targets.remove(name);
			fireTaskEvent(7, t, null, null);
		}
	}
	
	public final void reserveTargets(String ... targetNames) {
		Map newTargets = new LinkedHashMap();
		for (String targetName : targetNames) {
			if(targets.containsKey(targetName)){
				newTargets.put(targetName, targets.get(targetName));
			}
		}
		this.targets.clear();
		this.targets.putAll(newTargets);
	}

	public final void removeTarget(Target target) {
		if ((target != null) && (this.targets.containsKey(target.getName()))) {
			this.targets.remove(target.getName());
			fireTaskEvent(7, target, null, null);
		}
	}

	public final Target getTarget(String name) {
		return ((Target) this.targets.get(name));
	}

	public final Target[] getTargets() {
		Target[] t = new Target[this.targets.size()];
		return ((Target[]) (Target[]) this.targets.values().toArray(t));
	}

	public final int getTargetCount() {
		return this.targets.size();
	}

	protected void fireTaskEvent(int eventType, Target relativeTarget, ExecuteUnit relativeUnit, Route relativeRoute) {
		TaskEvent e = new TaskEvent(this, eventType, relativeTarget, relativeUnit, relativeRoute);
		fireTaskEvent(e);
	}

	protected void fireTaskEvent(TaskEvent e) {
		for (int i = 0; i < this.listeners.size(); ++i) {
			TaskEventListener l = (TaskEventListener) this.listeners.get(i);
			switch (e.getType()) {
			case 0:
				l.taskStart(e);
				break;
			case 1:
				l.taskExit(e);
				break;
			case 2:
				l.targetStart(e);
				break;
			case 3:
				l.targetExit(e);
				break;
			case 4:
				l.unitStart(e);
				break;
			case 5:
				l.unitExit(e);
				break;
			case 6:
				l.targetAdded(e);
				break;
			case 7:
				l.targetRemoved(e);
				break;
			case 8:
				l.unitAdded(e);
				break;
			case 9:
				l.unitRemoved(e);
				break;
			case 10:
				l.routeAdded(e);
				break;
			case 11:
				l.routeRemoved(e);
			}
		}
	}

	public void addTaskEventListener(TaskEventListener l) {
		if (!(this.listeners.contains(l)))
			this.listeners.add(l);
	}

	public void removeTaskEventListener(TaskEventListener l) {
		if (this.listeners.contains(l))
			this.listeners.remove(l);
	}

}