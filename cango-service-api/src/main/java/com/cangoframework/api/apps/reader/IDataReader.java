package com.cangoframework.api.apps.reader;

import java.util.List;

public abstract class IDataReader {

	public void start(String lineContent, List<String> fieldList){

	}

	public abstract void readLine(int lineNumber, String lineContent, List<String> fieldList);
	
	public void end(String lineContent, List<String> fieldList){

	}


}
