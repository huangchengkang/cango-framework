package com.cangoframework.task;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cangoframework.task.util.StringX;
import com.cangoframework.task.util.Tools;

public class TaskBuilder {
	private static Logger logger = LoggerFactory.getLogger(TaskBuilder.class);
	
	/**
	 * 检查Task配置文件的正确性
	 * @param taskFile
	 * @return
	 */
	public static boolean checkTaskFile(String taskFile){
		if(Tools.isEmpty(taskFile))
			return false;
		File file = new File(taskFile);
		if(!(file.exists()&&file.isFile()))
			return false;
		if(!(file.getName().endsWith(".xml")||
				file.getName().endsWith(".XML")))
			return false;
		
		Document doc = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(Tools.getInputStream(taskFile));
			Element rootElement = doc.getRootElement();
			if(rootElement==null||!"task".equalsIgnoreCase(rootElement.getName()))
				return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static Task buildTaskFromXML(String taskFile) {
		Task task = null;
		if (StringX.isEmpty(taskFile))
			return task;
		try {
			task = buildTaskFromXML(Tools.getInputStream(taskFile));
		} catch (Exception e) {
			e.printStackTrace();
		} return task;
	}

	public static Task buildTaskFromXML(InputStream inputStream) {
		Task task = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			task = buildTaskFromXML(builder.build(inputStream));
		} catch (Exception e) {
			logger.error("Build document from " + inputStream + " failed", e);
		}
		return task;
	}

	public static Task buildTaskFromXML(Document taskDocument) {
		Task task = new Task();
		Element xTask = taskDocument.getRootElement();
		
		//1.加载systemProperties属性
		Element xSystemProperties = xTask.getChild("systemProperties");
		buildSystemPropertiesFromXML(xSystemProperties, task);
		
		//2.初始化Task对象
		buildTaskObject(xTask, task);
		task.setParallelRun(Boolean.valueOf(xTask.getAttributeValue("parallelRun")).booleanValue());
		task.setTraceOn(Boolean.valueOf(xTask.getAttributeValue("traceOn")).booleanValue());

		//3.初始化任务监听器
		Element xListeners = xTask.getChild("listeners");
		if (xListeners != null) {
			List listenerList = xListeners.getChildren("listener");
			if (!(listenerList.isEmpty())) {
				boolean enabled = true;
				for (int i = 0; i < listenerList.size(); ++i) {
					Element xListener = (Element) listenerList.get(i);
					String enabledFlag = xListener.getAttributeValue("enabled");
					try {
						if (StringX.isEmpty(enabledFlag)){
							enabled = StringX.parseBoolean(enabledFlag);
						}
						if (enabled){
							buildListener(xListener, task);
						}
					} catch (Exception e) {
						logger.error("Add Listener error.", e);
					}
				}
			}
		}
		
		
		//4.初始化任务目标对象
		Element xTargets = xTask.getChild("targets");
		List targetList = xTargets.getChildren("target");
		if (!(targetList.isEmpty())) {
			boolean enabled = true;
			for (int i = 0; i < targetList.size(); ++i) {
				try {
					Element xTarget = (Element) targetList.get(i);
					String enabledFlag = xTarget.getAttributeValue("enabled");
					if (enabledFlag != null){
						enabled = StringX.parseBoolean(enabledFlag);
					}
					if (enabled){
						buildTarget(xTarget, task);
					}
				} catch (Exception e) {
					logger.error("Load Target error.", e);
					task = null;
				}
			}
		}
		
		return task;
	}

	private static void buildSystemPropertiesFromXML(Element xSystemProperties, Task task) {
		if(xSystemProperties==null) return;
		List<?> l = xSystemProperties.getChildren();
		if (!l.isEmpty()) {
			for (int i = 0; i < l.size(); ++i) {
				Element element = (Element) l.get(i);
				String name = element.getAttribute("name").getValue();
				String value = element.getAttribute("value").getValue();
				SystemHelper.setProperty(name, value);
			}
		}
	}
	
	/**
	 * 任务监听器
	 * @param xListener
	 * @param task
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	private static void buildListener(Element xListener, Task task)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = xListener.getAttributeValue("listenerClass");
		if (StringX.isEmpty(className)){
			throw new ClassNotFoundException("listenerClass is null!");
		}
		TaskEventListener listener = (TaskEventListener) Class.forName(className).newInstance();

		Element xProps = xListener.getChild("extendProperties");
		if (xProps != null) {
			Iterator it = xProps.getChildren("property").iterator();
			while (it.hasNext()) {
				String propertyName = null;
				Element xProp = (Element) it.next();
				propertyName = xProp.getAttributeValue("name");
				if(StringX.isEmpty(propertyName)){
					propertyName = propertyName.trim();
					Tools.setPropertyX(listener, propertyName, SystemHelper.replacePropertyTags(xProp.getAttributeValue("value")), false);
				}
			}
		}
		task.addTaskEventListener(listener);
	}
	private static void buildTarget(Element xTarget, Task task)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, Exception {
		Target target = new Target();
		
		//1.初始化Target对象
		buildTaskObject(xTarget, target);
		
		//开始区间
		String regex = "^[0-23]{2}:[0-59]{2}:[0-59]{2}$";
		String startTime = xTarget.getAttributeValue("startTime",xTarget.getChildText("startTime"));
		if(StringX.isSpace(startTime)){
			startTime = "00:00:00";
		}
		startTime = startTime.trim();
		if(startTime.matches(regex)){
			String[] split = startTime.split("[:]", 3);
			if(Integer.parseInt(split[0])<=23&&
					Integer.parseInt(split[1])<=59&&
					Integer.parseInt(split[2])<=59){
				target.setStartTime(startTime);
			}else{
				throw new Exception("target("+target.getName()+") startTime config is error.");
			}
		}else{
			throw new Exception("target("+target.getName()+") startTime config is error.");
		}
		//结束区间
		String endTime = xTarget.getAttributeValue("endTime",xTarget.getChildText("endTime"));
		if(StringX.isSpace(endTime)){
			endTime = "23:59:59";
		}
		endTime = endTime.trim();
		if(endTime.matches(regex)){
			String[] split = endTime.split("[:]", 3);
			if(Integer.parseInt(split[0])<=23&&
					Integer.parseInt(split[1])<=59&&
					Integer.parseInt(split[2])<=59){
				target.setEndTime(endTime);
			}else{
				throw new Exception("target("+target.getName()+") endTime config is error.");
			}
		}else{
			throw new Exception("target("+target.getName()+") endTime config is error.");
		}
		//目标任务类型
		String type = xTarget.getAttributeValue("type",xTarget.getChildText("type"));
		if(StringX.isSpace(type))
			type = "0";
		target.setType(type);
		
		//1.1设置并发能力与并发数量
		target.setParallelRun(Boolean.valueOf(xTarget.getAttributeValue("parallelRun")).booleanValue());
		String parallelNumber = xTarget.getAttributeValue("parallelNumber");
		if (parallelNumber != null){
			target.setParallelNumber(parallelNumber);
		}

		//2.初始化Target的执行单元
		Element xUnits = xTarget.getChild("executeUnits");
		if (xUnits == null) {
			throw new Exception("Target: " + target.getName() + " miss executeUnits node!");
		}

		//3.初始化执行单元 executeUnit
		Iterator it = xUnits.getChildren("executeUnit").iterator();
		while (it.hasNext()) {
			Element xUnit = (Element) it.next();
			buildUnit(xUnit, target);
		}

		//4.初始化路由表
		
		//4.1创建串行路由
		Element xRouteTable = xTarget.getChild("routeTable");
		if ((xRouteTable == null) && (!(target.isParallelRun()))) {
			throw new Exception("Target: " + target.getName() + " miss routeTable node!");
		}
		if (!target.isParallelRun()){
			buildRoute(target, xRouteTable);
		}

		//4.2创建并行路由
		Element xParallelTable = xTarget.getChild("parallelTable");
		if (xParallelTable == null && (target.isParallelRun())) {
			throw new Exception("Target: " + target.getName() + " miss parallelTable node!");
		}
		if (target.isParallelRun()){
			buildParallelTable(target, xParallelTable);
		}
		
		task.addTarget(target);
	}

	private static void buildRoute(Target target, Element xRouteTable) throws IllegalAccessException {
		Iterator it = xRouteTable.getChildren("route").iterator();
		while (it.hasNext()) {
			Element xRoute = (Element) it.next();
			
			//1.unit
			ExecuteUnit unit = target.getUnit(xRoute.getAttributeValue("unit"));
			if (unit == null) {
				throw new IllegalAccessException(
						"Illegal route,unit \"" + xRoute.getAttributeValue("unit") + "\" not access!");
			}
			
			//2.nextUnit
			ExecuteUnit nextUnit = target.getUnit(xRoute.getAttributeValue("nextUnit"));
			if (nextUnit == null) {
				throw new IllegalAccessException(
						"Illegal route,nextUnit \"" + xRoute.getAttributeValue("nextUnit") + "\" not access!");
			}

			//2.executeStatus
			String attrExecuteStatus = xRoute.getAttributeValue("executeStatus");
			if ((attrExecuteStatus == null) || (attrExecuteStatus.equalsIgnoreCase(""))) {
				throw new IllegalAccessException(
						"Illegal route,lost executeStatus,unit=\"" + xRoute.getAttributeValue("unit") + "\",nextUnit=\""
								+ xRoute.getAttributeValue("nextUnit") + "\"");
			}

			unit.addRoute(new Route(attrExecuteStatus, nextUnit));
		}
	}

	private static void buildParallelTable(Target target, Element xRouteTable) throws IllegalAccessException {
		Iterator it = xRouteTable.getChildren("unit").iterator();
		while (it.hasNext()) {
			Element xUnit = (Element) it.next();
			ExecuteUnit unit = target.getUnit(xUnit.getAttributeValue("name"));
			if (unit == null)
				throw new IllegalAccessException(
						"Illegal route,unit \"" + xUnit.getAttributeValue("name") + "\" not access!");
			target.getParalleTable().addUnit(unit);

			Iterator itp = xUnit.getChildren("preUnit").iterator();
			while (itp.hasNext()) {
				Element xPreUnit = (Element) itp.next();
				ExecuteUnit preUnit = target.getUnit(xPreUnit.getAttributeValue("unit"));
				if (preUnit == null)
					throw new IllegalAccessException(
							"Illegal route,unit \"" + xPreUnit.getAttributeValue("unit") + "\" not access!");
				String attrExecuteStatus = xPreUnit.getAttributeValue("executeStatus");
				if ((attrExecuteStatus == null) || (attrExecuteStatus.equalsIgnoreCase("")))
					throw new IllegalAccessException(
							"Illegal preUnit,lost executeStatus,unit=\"" + xPreUnit.getAttributeValue("unit") + "\"");
				target.getParalleTable().addPreviousUnit(unit, preUnit, attrExecuteStatus);
			}
		}
		logger.debug("\n" + target.getParalleTable());
	}

	private static void buildUnit(Element xUnit, Target target)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String className = xUnit.getAttributeValue("executeClass");
		if (className == null){
			className = xUnit.getChildTextTrim("executeClass");
		}
		//初始化ExecuteUnit对象
		ExecuteUnit unit = (ExecuteUnit) Class.forName(className).newInstance();
		buildTaskObject(xUnit, unit);
		String bAllowManualExecute = xUnit.getAttributeValue("allowManualExecute");
		if (bAllowManualExecute != null){
			unit.setAllowManualExecute(StringX.parseBoolean(bAllowManualExecute));
		}
		target.addUnit(unit);
	}

	private static void buildTaskObject(Element xTaskObject, TaskObject taskObject) {
		
		String tempStr = null;
		
		//1.任务名称
		tempStr = xTaskObject.getAttributeValue("name");
		if (tempStr == null){
			tempStr = xTaskObject.getChildTextTrim("name");
		}
		taskObject.setName(tempStr);

		//2.任务描述
		tempStr = xTaskObject.getAttributeValue("describe");
		if (tempStr == null){
			tempStr = xTaskObject.getChildTextTrim("describe");
		}
		taskObject.setDescribe(tempStr);

		//3.任务全局属性
		Element xProps = xTaskObject.getChild("extendProperties");
		Element xProp = null;
		Iterator<Element> it = null;
		if (xProps != null) {
			it = xProps.getChildren("property").iterator();
			while (it.hasNext()) {
				xProp = (Element) it.next();
				taskObject.setProperty(xProp.getAttributeValue("name"), SystemHelper.replacePropertyTags(xProp.getAttributeValue("value")));
			}
		}
		
		//4.接受无父标签extendProperties的属性
		it = xTaskObject.getChildren("property").iterator();
		while (it.hasNext()) {
			xProp = (Element) it.next();
			taskObject.setProperty(xProp.getAttributeValue("name"), SystemHelper.replacePropertyTags(xProp.getAttributeValue("value")));
		}
		
	}

	public static Task buildTaskFromDB(Connection connection, String taskTable, String targetTable,
			String procedureTable) {
		return null;
	}

	public static Task buildTaskFromFile(String filename) {
		return null;
	}
}