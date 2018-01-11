package com.cangoframework.task.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JProgressBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SingleModeAction extends AbstractAction implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(SingleModeAction.class);
	private static final long serialVersionUID = 1L;
	protected static Thread processThread = null;
	private String actionName = "Untitiled Action";
	private JProgressBar progressBar = null;

	public SingleModeAction(String action) {
		super(action);
		this.actionName = action;
	}

	public void actionPerformed(ActionEvent e) {
		if ((processThread != null) && (processThread.isAlive())) {
			logger.error("任务[" + processThread.getName() + "]正在执行,不能启动其他任务!!!");
			return;
		}
		processThread = new Thread(this, this.actionName);
		processThread.start();
	}

	public abstract void run();

	public JProgressBar getProgressBar() {
		return this.progressBar;
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}
}