package com.cangoframework.task;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cangoframework.task.util.CommandLineArgument;
import com.cangoframework.task.util.StringX;

public class TaskRunner {
	private static Logger logger = LoggerFactory.getLogger(TaskRunner.class);
	private static boolean isGuiMode;
	
	public static Task runTarget(String taskFile ,String targetName) {
		return runTarget(taskFile,targetName,false);
	}
	public static Task runTarget(String taskFile ,String targetName ,boolean newThread) {
		Task task = null;
		initSystemProperties();
		if (taskFile == null) {
			logger.error("没有定义任务配置文件！");
			return task;
		}
		task = TaskBuilder.buildTaskFromXML(taskFile);
		if (task == null) {
			logger.error("创建任务失败！");
			return task;
		}
		task.runTarget(targetName, newThread);
		return task;
	}
	public static int runTask(String taskFile ,String ... targetNames) {
		initSystemProperties();
		if (taskFile == null) {
			logger.error("没有定义任务配置文件！");
			return -1;
		}
		Task task = TaskBuilder.buildTaskFromXML(taskFile);
		if (task == null) {
			logger.error("创建任务失败！");
			return -1;
		}
		task.reserveTargets(targetNames);
		return runTask(task);
	}
	public static int runTask(String taskFile) {
		initSystemProperties();
		if (taskFile == null) {
			logger.error("没有定义任务配置文件！");
			return -1;
		}
		Task task = TaskBuilder.buildTaskFromXML(taskFile);
		if (task == null) {
			logger.error("创建任务失败！");
			return -1;
		}
		if(isGuiMode){
			try {
				Class<?> taskUIRunnerClass = Class.forName(TaskRunner.class.getPackage().getName()+".TaskUIRunner");
				Method method = taskUIRunnerClass.getDeclaredMethod("runGui", Task.class);
				method.invoke(taskUIRunnerClass, task);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			return runTask(task);
		}
		return 0;
	}

	public static int runTask(Task task) {
		task.start();
		logger.info("任务运行完成！");
		return 0;
	}

	
	public static boolean isGuiMode() {
		return isGuiMode;
	}

	public static void setGuiMode(boolean isGuiMode) {
		TaskRunner.isGuiMode = isGuiMode;
	}

	private static void initSystemProperties() {
		SystemHelper.setProperty("nowDate", new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
		SystemHelper.setProperty("currentDate", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
	}

	public static void main(String[] args) {
		int exitCode = -1;
		CommandLineArgument arg = new CommandLineArgument(args);
		setGuiMode(arg.getArgument("gui", false));
		String target = arg.getArgument("target", "");
		String task = arg.getArgument("task", "classpath:task.xml");
		if(StringX.isSpace(target)){
			exitCode = runTask(task);
		}else{
			String[] targetArray = StringX.getArray(target);
			exitCode = runTask(task, targetArray);
		}
		if (!(isGuiMode())){
			System.exit(exitCode);
		}
	}
}