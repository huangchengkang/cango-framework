package com.cangoframework.task;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import com.cangoframework.task.SystemHelper;
import com.cangoframework.task.Task;
import com.cangoframework.task.TaskRunner;
import com.cangoframework.task.ui.TaskWindow;
import com.cangoframework.task.util.CommandLineArgument;

public class TaskUIRunner extends TaskRunner {

	public static int runGui(Task task) {
		try {
			// 设置外观
			JFrame.setDefaultLookAndFeelDecorated(SystemHelper.getProperty("frameDecorated",true));
			JDialog.setDefaultLookAndFeelDecorated(SystemHelper.getProperty("dialogDecorated",true));
			UIManager.setLookAndFeel(SystemHelper.getProperty("lookAndFeel", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel"));
			setUIFont();
			TaskWindow tw = new TaskWindow();
			tw.setTask(task);
			tw.setLocationRelativeTo(null);
			tw.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	private static void setUIFont() {
		Font ft = new Font(SystemHelper.getProperty("fontStyle","宋体"),
				0, SystemHelper.getProperty("fontSize",12));
		FontUIResource f = new FontUIResource(ft);
		Enumeration<?> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource)
				UIManager.put(key, f);
		}
	}
	
	public static void main(String[] args) {
		CommandLineArgument arg = new CommandLineArgument(args);
		setGuiMode(arg.getArgument("gui", false));
		int exitCode = runTask(arg.getArgument("task", "classpath:task.xml"));
		if (!(isGuiMode()))
			System.exit(exitCode);
	}
}
