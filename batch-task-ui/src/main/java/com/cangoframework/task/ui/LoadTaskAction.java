package com.cangoframework.task.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cangoframework.task.Task;
import com.cangoframework.task.TaskBuilder;

public class LoadTaskAction extends SingleModeAction {
	private static Logger logger = LoggerFactory.getLogger(LoadTaskAction.class);
	private static final long serialVersionUID = 1L;
	private TaskWindow parentWindow = null;
	private File lastDirectory = null;
	private String taskFilePath;

	public LoadTaskAction(TaskWindow parentWin) {
		super("LoadTask");
		this.parentWindow = parentWin;
		this.lastDirectory = new File("./");
	}

	public void run() {
		JFileChooser jfc = new JFileChooser(this.lastDirectory);
		jfc.setFileFilter(getXmlFileFilter());
		if (jfc.showOpenDialog(this.parentWindow) == 0) {
			File taskfile = jfc.getSelectedFile();
			this.taskFilePath = taskfile.getAbsolutePath();// task文件路径
			this.lastDirectory = jfc.getCurrentDirectory();
			if(!TaskBuilder.checkTaskFile(taskFilePath)){
				JOptionPane.showMessageDialog(parentWindow, "选择的Task文件无效,无法加载...");
				return;
			}
			Task parentTask = parentWindow.getTask();
			Task task = TaskBuilder.buildTaskFromXML(taskfile.getAbsolutePath());
			this.parentWindow.setTask(task);
		} else {
			logger.info("用户取消了选择任务文件，装载任务中止!");
		}
	}

	private FileFilter getXmlFileFilter() {
		FileFilter fileFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "任务定义文件(*.xml)";
			}
			@Override
			public boolean accept(File f) {
				if (f.isDirectory())
					return true;
				String ext = null;
				String s = f.getName();
				int i = s.lastIndexOf(46);
				if ((i > 0) && (i < s.length() - 1)) {
					ext = s.substring(i + 1).toLowerCase();
				}
				if ("xml".equalsIgnoreCase(ext))
					return true;
				return false;
			}
		};
		return fileFilter;
	}

	public String getTaskFilePath() {
		return taskFilePath;
	}

	public void setTaskFilePath(String taskFilePath) {
		this.taskFilePath = taskFilePath;
	}

}