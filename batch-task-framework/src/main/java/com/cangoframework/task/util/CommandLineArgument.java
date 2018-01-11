package com.cangoframework.task.util;

import java.util.Properties;

public class CommandLineArgument {
	private String[] args;
	private Properties p;
	public CommandLineArgument(String[] args) {
		this.args = args;
		init();
	}
	public String getArgument(String paramName){
		return p.getProperty(paramName, "");
	}
	public String getArgument(String paramName,String def){
		return p.getProperty(paramName, def);
	}
	public boolean getArgument(String paramName,boolean def){
		String booleanValue = p.getProperty(paramName,String.valueOf(def));
		return Boolean.parseBoolean(booleanValue);
	}
	public int getArgument(String paramName,int def){
		String intValue = p.getProperty(paramName,String.valueOf(def));
		return Integer.parseInt(intValue);
	}
	private void init() {
		p = new Properties();
		if(args==null||args.length==0)
			return;
		for (String param : args) {
			if(check(param)) continue;
			String[] kvs = param.split("=",2);
			p.put(kvs[0], kvs[1]);
		}
	}
	private boolean check(String param){
		if(param==null||"".equals(param))
			return true;
		if(!param.contains("=")) 
			return true;
		return false;
	}
	
}
