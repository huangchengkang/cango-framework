package com.cangoframework.task.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cangoframework.task.ExecuteUnit;
import com.cangoframework.task.Target;
import com.cangoframework.task.Task;
import com.cangoframework.task.TaskObject;

public class RunTaskObjectAction extends SingleModeAction {
	private static Logger logger = LoggerFactory.getLogger(RunTaskObjectAction.class);
	private static final long serialVersionUID = 1L;
	private TaskObject taskObject = null;

	public RunTaskObjectAction() {
		super("RunTaskObject");
	}

	public RunTaskObjectAction(TaskObject to) {
		super("RunTaskObject");
		this.taskObject = to;
	}

	public void run() {
		if (this.taskObject == null) {
			logger.warn("没有选中的任务模块可以运行！");
			return;
		}

		logger.info("开始运行任务对象......");
		getProgressBar().setIndeterminate(true);
		if (this.taskObject instanceof Task) {
			Task task = (Task) this.taskObject;
			task.start();
		} else if (this.taskObject instanceof Target) {
			Target target = (Target) this.taskObject;
			target.getTask().runTarget(target.getName(), false);
		} else if (this.taskObject instanceof ExecuteUnit) {
			ExecuteUnit unit = (ExecuteUnit) this.taskObject;
			Target target = unit.getTarget();
			target.getTask().executeUnit(target.getName(), unit.getName());
		} else {
			logger.error("无效的任务对象类型！");
		}
		logger.info("任务对象运行结束！");
		getProgressBar().setIndeterminate(false);
	}

	public final TaskObject getTaskObject() {
		return this.taskObject;
	}

	public final void setTaskObject(TaskObject taskObject) {
		this.taskObject = taskObject;
	}
}