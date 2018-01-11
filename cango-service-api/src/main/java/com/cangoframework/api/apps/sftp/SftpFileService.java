package com.cangoframework.api.apps.sftp;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class SftpFileService {
	
	private static Logger logger = LoggerFactory.getLogger(SftpFileService.class);
	public static boolean downloadFileByPassword(String sourcePath, String remotePath , SftpConfig sftpConfig) {
		SftpChannel sftpUtil = null;
		InputStream instream = null;
		OutputStream out = null;
		try {
			sftpUtil = new SftpChannel();
			ChannelSftp sftpChannel = sftpUtil.getChannel(sftpConfig, 5 * 60 * 1000);
			//创建本地文件
			createLocalFile(sourcePath);
			// 以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
			instream = sftpChannel.get(remotePath);
			out = new FileOutputStream(sourcePath);
			exchangeStream(instream,out);
			return true;
		} catch(Exception e) {
			logger.error("sftp文件获取失败", e);
			return false;
		} finally {
			closeResource(sftpUtil,instream,out);
		}
	}

	public static boolean downloadFileByPassword(String sourcePath, String remotePath, String host, String port, String userName, String password) {
		SftpConfig sftpConfig = new SftpConfig();
		sftpConfig.setHost(host);
		sftpConfig.setPort(port);
		sftpConfig.setUsername(userName);
		sftpConfig.setPassword(password);
		sftpConfig.setType(SftpConfig.SFTP_TYPE_PASSWORD);
		return downloadFileByPassword(sourcePath,remotePath,sftpConfig);
	}
	public static boolean uploadFileByPassword(String sourcePath, String remotePath, SftpConfig sftpConfig) {
		SftpChannel sftpUtil = null;
		InputStream instream = null;
		OutputStream out = null;
		try {
			sftpUtil = new SftpChannel();
			ChannelSftp sftpChannel = sftpUtil.getChannel(sftpConfig, 5 * 60 * 1000);
			//创建远程文件夹
			createRemoteFile(sftpChannel,remotePath);
			out = sftpChannel.put(remotePath);
			instream = new FileInputStream(sourcePath);

			exchangeStream(instream,out);
			return true;
		} catch(Exception e) {
			logger.error("sftp文件获取失败", e);
			return false;
		} finally {
			closeResource(sftpUtil,instream,out);
		}
	}
	public static boolean uploadFileByPassword(String sourcePath, String remotePath, String host, String port, String userName, String password) {
		SftpConfig sftpConfig = new SftpConfig();
		sftpConfig.setHost(host);
		sftpConfig.setPort(port);
		sftpConfig.setUsername(userName);
		sftpConfig.setPassword(password);
		sftpConfig.setType(SftpConfig.SFTP_TYPE_PASSWORD);
		return uploadFileByPassword(sourcePath,remotePath,sftpConfig);
	}
	
	public static boolean getFileByPrivateKey(String sourcePath, String remotePath, String host, String port, String userName) {
		SftpConfig sftpConfig = new SftpConfig();
		sftpConfig.setHost(host);
		sftpConfig.setPort(port);
		sftpConfig.setUsername(userName);
		sftpConfig.setType(SftpConfig.SFTP_TYPE_PRIVATEKEY);
		
		SftpChannel sftpUtil = null;
		InputStream instream = null;
		OutputStream out = null;
		try {
			sftpUtil = new SftpChannel();
			ChannelSftp sftpChannel = sftpUtil.getChannel(sftpConfig, 5 * 60 * 1000);
			//创建本地文件
			createLocalFile(sourcePath);
			// 以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
			out = new FileOutputStream(sourcePath);
			instream = sftpChannel.get(remotePath);
			exchangeStream(instream,out);
			return true;
		} catch(Exception e) {
			logger.error("sftp文件获取失败", e);
			return false;
		} finally {
			closeResource(sftpUtil,instream,out);
		}

	}
	
	public static boolean putFileByPrivateKey(String sourcePath, String remotePath, String host, String port, String userName) {
		SftpConfig sftpConfig = new SftpConfig();
		sftpConfig.setHost(host);
		sftpConfig.setPort(port);
		sftpConfig.setUsername(userName);
		sftpConfig.setType(SftpConfig.SFTP_TYPE_PRIVATEKEY);
		
		SftpChannel sftpUtil = null;
		InputStream instream = null;
		OutputStream out = null;
		try {
			sftpUtil = new SftpChannel();
			ChannelSftp sftpChannel = sftpUtil.getChannel(sftpConfig, 5 * 60 * 1000);
			//创建远程文件夹
			createRemoteFile(sftpChannel,remotePath);
			// 以下代码实现从本地上传一个文件到服务器，如果要实现下载，对换以下流就可以了
			instream = new FileInputStream(sourcePath);
			out = sftpChannel.put(remotePath);
			exchangeStream(instream,out);
			return true;
		} catch(Exception e) {
			logger.error("sftp文件获取失败", e);
			return false;
		} finally {
			closeResource(sftpUtil,instream,out);
		}
		        
	}
	/**
	 * 确保文件存在
	 * @param filePath
	 */
	private static void createLocalFile(String filePath){
		filePath = filePath.replace("\\","/");
		//1.确保本地路径存在
		String tempDirPath = filePath.substring(0,filePath.lastIndexOf("/"));
		File dir = new File(tempDirPath);
		if(!(dir.exists()&&dir.isDirectory())){
			dir.mkdirs();
			logger.info("sftp本地路径["+tempDirPath+"]创建成功...");
		}

		//2.确保文件存在，保证流交换时正常
		File file = new File(filePath);
		if(!(file.exists()&&file.isFile())){
			try {
				file.createNewFile();
				logger.info("sftp本地文件["+filePath+"]创建成功...");
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("创建sftp本地文件["+filePath+"]失败...");
			}
		}
	}
	/**
	 * 创建远程目录
	 * @param sftpChannel
	 * @param filePath
	 */
	private static void createRemoteFile(ChannelSftp sftpChannel, String filePath){
		//1.创建文件路径
		String tempDirPath = filePath.substring(0,filePath.lastIndexOf("/"));
		try {
			sftpChannel.cd(tempDirPath);
		} catch (SftpException e) {
			logger.warn("sftp服务器不存在文件夹:"+tempDirPath+" , 程序自动创建中...");
			//文件夹不存在
			try {
				sftpChannel.cd( "/" );
				String[] folders = tempDirPath.split("/");
				for ( String folder : folders ) {
					if (folder.length() > 0 ) {
						try {
							sftpChannel.cd( folder );
						}catch ( SftpException ex ) {
							sftpChannel.mkdir( folder );
							sftpChannel.cd( folder );
						}
					}
				}
			} catch (SftpException x) {
				x.printStackTrace();
				logger.error("文件夹:"+tempDirPath+" , 不是一个有效的目录路径或者没有权限，目录创建失败...");
			}
		}

		//2.确保文件存在
		//sftpChannel 自动创建覆盖，无须处理
	}

	private static void closeResource(SftpChannel sftpUtil, InputStream instream, OutputStream out) {
		try {
			if (out != null) {
				out.close();
			}
			if (instream != null) {
				instream.close();
			}
			if (sftpUtil != null) {
				sftpUtil.closeChannel();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 交换流内容
	 * @param instream
	 * @param out
	 * @throws IOException
	 */
	private static void exchangeStream(InputStream instream, OutputStream out) throws IOException {
		byte[] buff = new byte[1024 * 2];
		int read;
		if (instream != null) {
			logger.info("Start to read input stream");
			do {
				read = instream.read(buff, 0, buff.length);
				if (read > 0) {
					out.write(buff, 0, read);
				}
				out.flush();
			} while (read >= 0);
			logger.info("input stream read done.");
		}
	}
	
	
}
