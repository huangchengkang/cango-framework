package com.cangoframework.api.service;

import com.cangoframework.api.apps.sftp.SftpConfig;
import com.cangoframework.api.apps.sftp.SftpFileService;
import com.cangoframework.api.common.ServiceApiProperties;

/**
 * Created by cango on 2018/1/10.
 */
public class SftpServiceApi {
    public static boolean downloadFile(String localPath, String remotePath ) {
        SftpConfig sftpConfig = getDefaultSftpConfig();
        return downloadFile(localPath,remotePath,sftpConfig);
    }

    public static boolean downloadFile(String localPath, String remotePath , SftpConfig sftpConfig) {
        return SftpFileService.downloadFileByPassword(localPath,remotePath,sftpConfig);
    }
    public static boolean downloadFile(String localPath, String remotePath ,
           String host, String port, String userName, String password) {
        return SftpFileService.downloadFileByPassword(localPath, remotePath, host, port, userName, password);
    }

    public static boolean uploadFile(String localPath, String remotePath ) {
        SftpConfig sftpConfig = getDefaultSftpConfig();
        return uploadFile(localPath, remotePath, sftpConfig);
    }
    public static boolean uploadFile(String localPath, String remotePath, SftpConfig sftpConfig ) {
        return SftpFileService.uploadFileByPassword(localPath, remotePath, sftpConfig);
    }
    public static boolean uploadFile(String localPath, String remotePath,
          String host, String port, String userName, String password) {
        return SftpFileService.uploadFileByPassword(localPath, remotePath, host, port, userName, password);
    }

    private static SftpConfig getDefaultSftpConfig() {
        String host = ServiceApiProperties.getProperty("sftp.host");
        String port = ServiceApiProperties.getProperty("sftp.port","22");
        String username = ServiceApiProperties.getProperty("sftp.username");
        String password = ServiceApiProperties.getProperty("sftp.password");
        return new SftpConfig(host,port,username,password);
    }
}
