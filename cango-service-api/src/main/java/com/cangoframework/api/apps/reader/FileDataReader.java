package com.cangoframework.api.apps.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;


public class FileDataReader {
	public static void readData(IDataReader reader,String fileName) 
			throws Exception{
		readData(reader, fileName, ",");
	}
	public static void readData(IDataReader reader,String fileName,String splitChar) 
			throws Exception{
		readData(reader, fileName, splitChar,-1);
	}
	public static void readData(IDataReader reader,String fileName,
			String splitChar,int limit) throws Exception{
		readData(reader, fileName,"UTF-8",splitChar,limit);
	}
	public static void readData(IDataReader reader,String fileName,String encoding,
			String splitChar) throws Exception{
		readData(reader, fileName,encoding,splitChar,-1);
	}
	public static void readData(IDataReader reader,String fileName,String encoding,
			String splitChar,int limit) throws Exception{
		readData(reader, fileName,encoding,splitChar,limit,null);
	}
	public static void readData(IDataReader reader,String fileName,String encoding,
			String splitChar,int limit,String headFlag) throws Exception{
		readData(reader, fileName,encoding,splitChar,limit,headFlag,false);
	}
	

	/**
	 * @param reader 数据临时存储器
	 * @param fileName 文件路径
	 * @param encoding 读取编码
	 * @param splitChar 分隔符号
	 * @param limit 分隔最大限
	 * @param headFlag 头部开始标记
	 * @param doStartEndFlag 数据行是否读取开始和结束行
	 * @throws Exception
	 */
	public static void readData(IDataReader reader,String fileName,String encoding,
			String splitChar,Integer limit,String headFlag,boolean doStartEndFlag) throws Exception{
		
		BufferedReader br = null;
		try {
			boolean hasHeader = false;
			String startFlag = null;
			String dataFlag = null;
			String endFlag = null;
			if(headFlag!=null){
				String[] headerFlag = headFlag.split(",",3);
				if(headerFlag.length == 3){
					startFlag = headerFlag[0];
					dataFlag = headerFlag[1];
					endFlag = headerFlag[2];
					hasHeader = true;
				}else{
					hasHeader = false;
					throw new Exception("请指定正确格式的行头内容...");
				}
			}
			
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
			String lineContent = br.readLine();
			int lineNumber = 0;
			List<String> fieldList = null;
			while(lineContent!=null){
				lineNumber ++;
				String[] fields = null;
				if(limit ==null ||limit < 0){
					fields = lineContent.split(splitChar);
				}else{
					fields = lineContent.split(splitChar,limit);
				}
				fieldList = Arrays.asList(fields);
				
				if(hasHeader&&lineContent.startsWith(startFlag)){
					reader.start(lineContent,fieldList);
					lineContent = br.readLine();
					continue;
				}
				if(hasHeader&&lineContent.startsWith(dataFlag)){
					reader.readLine(lineNumber, lineContent, fieldList);
					lineContent = br.readLine();
					continue;
				}
				if(hasHeader&&lineContent.startsWith(endFlag)){
					reader.end(lineContent,fieldList);
					lineContent = br.readLine();
					continue;
				}
				
				if(lineNumber==1){
					reader.start(lineContent,fieldList);
					if(doStartEndFlag)
						reader.readLine(lineNumber, lineContent, fieldList);
					lineContent = br.readLine();
					continue;
				}
				String currentLineContent = lineContent;
				lineContent = br.readLine();
				if(lineContent==null){
					if(doStartEndFlag)
						reader.readLine(lineNumber, currentLineContent, fieldList);
					reader.end(currentLineContent, fieldList);
				}else{
					reader.readLine(lineNumber, currentLineContent, fieldList);
				}
			}
			
		} catch (Exception e) {
			throw e;
		}finally{
			if(br!=null) 
				br.close();
		}
	}
	
}
