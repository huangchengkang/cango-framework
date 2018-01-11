package com.cangoframework.api.apps.sftp;

import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SftpChannel {
	private Channel channel;
	private Session session;

	public ChannelSftp getChannel(SftpConfig sftpDetails, int timeout)
			throws JSchException {

		String ftpHost = sftpDetails.getHost();
		String port = sftpDetails.getPort();
		String ftpUserName = sftpDetails.getUsername();
		String ftpPassword = sftpDetails.getPassword();
		String ftpType = sftpDetails.getType();

		int ftpPort = SftpConfig.SFTP_DEFAULT_PORT;
		if (port != null && !port.equals("")) {
			ftpPort = Integer.valueOf(port);
		}
		
		JSch jsch = new JSch(); // 创建JSch对象
		if(SftpConfig.SFTP_TYPE_PRIVATEKEY.equals(ftpType)) {
			jsch.addIdentity(sftpDetails.getPrivateKey());
		}

		session = jsch.getSession(ftpUserName, ftpHost, ftpPort); // 根据用户名，主机ip，端口获取一个Session对象
		System.out.println("Session created.");
		if(SftpConfig.SFTP_TYPE_PASSWORD.equals(ftpType) && ftpPassword != null) {
			session.setPassword(ftpPassword); // 设置密码
		}
		
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config); // 为Session对象设置properties
		session.setTimeout(timeout); // 设置timeout时间
		session.connect(); // 通过Session建立链接
		System.out.println("Session connected.");

		System.out.println("Opening Channel.");
		channel = session.openChannel("sftp"); // 打开SFTP通道
		channel.connect(); // 建立SFTP通道的连接
		System.out.println("Connected successfully to ftpHost = " + ftpHost
				+ ",as ftpUserName = " + ftpUserName + ", returning: "
				+ channel);
		return (ChannelSftp) channel;
	}

	public void closeChannel() throws Exception {
		if (channel != null) {
			channel.disconnect();
		}
		if (session != null) {
			session.disconnect();
		}
	}
}
