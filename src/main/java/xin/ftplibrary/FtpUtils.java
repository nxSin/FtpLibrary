package xin.ftplibrary;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FTP工具类
 *
 * @author dq
 */
public class FtpUtils {
    private boolean openLog;
    private FTPClient ftpClient = null;
    private String server;
    private int port;
    private String userName;
    private String userPassword;

    public static final int LocalPassiveMode = 2;
    public static final int ActiveMode = 1;
    /**
     * 主被动模式，1为主动模式 否则为被动模式
     */
    private int ftpModel = 1;

    public FtpUtils(String server, int port, String userName, String userPassword) {
        this.server = server;
        this.port = port;
        this.userName = userName;
        this.userPassword = userPassword;
    }

    /**
     * 设置debug，debug下会打印输出
     *
     * @param debug true 是 false 不是
     */
    public void setDebug(boolean debug) {
        this.openLog = debug;
    }

    /**
     * 连接服务器
     *
     * @return 连接成功与否 true:成功， false:失败
     */
    public boolean open() {
        print("open 1");
        if (ftpClient != null && ftpClient.isConnected()) {
            print("open 2");
            return true;
        }
        try {
            print("open 3");
            ftpClient = new FTPClient();
            print("open 4");
            // 连接
            ftpClient.connect(this.server, this.port);
            print("open 5");
            ftpClient.login(this.userName, this.userPassword);
            print("open 6");
            setFtpClient(ftpClient);
            print("open 7");
            // 检测连接是否成功
            int reply = ftpClient.getReplyCode();
            print("open 8");
            if (!FTPReply.isPositiveCompletion(reply)) {
                print("open 9");
                this.close();
                System.err.println("FTP server refused connection.");
                System.exit(1);
            }
            print("open FTP success:" + this.server + ";port:" + this.port + ";name:" + this.userName
                    + ";pwd:" + this.userPassword);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE); // 设置上传模式.binally  or ascii

            if (getFtpModel() == 1) {
                print("主动模式");
                //主动模式，外网使用
                ftpClient.enterLocalActiveMode();
            } else {
                print("被动模式");
                //被动模式，内网使用
                ftpClient.enterLocalPassiveMode();
            }

            print("open 10");
            return true;
        } catch (Exception ex) {
            print("open 11");
            this.close();
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 切换到父目录
     *
     * @return 切换结果 true：成功， false：失败
     */
    private boolean changeToParentDir() {
        try {
            return ftpClient.changeToParentDirectory();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 改变当前目录到指定目录
     *
     * @param dir 目的目录
     * @return 切换结果 true：成功，false：失败
     */
    private boolean cd(String dir) {
        try {
            return ftpClient.changeWorkingDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取目录下所有的文件名称
     *
     * @param filePath 指定的目录
     * @return 文件列表, 或者null
     */
    public FTPFile[] getFileList(String filePath) {
        try {
            return ftpClient.listFiles(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 层层切换工作目录
     *
     * @param ftpPath 目的目录
     * @return 切换结果
     */
    public boolean changeDir(String ftpPath) {
        if (!ftpClient.isConnected()) {
            return false;
        }
        try {
            // 将路径中的斜杠统一
            char[] chars = ftpPath.toCharArray();
            StringBuffer sbStr = new StringBuffer(256);
            for (int i = 0; i < chars.length; i++) {
                if ('\\' == chars[i]) {
                    sbStr.append('/');
                } else {
                    sbStr.append(chars[i]);
                }
            }
            ftpPath = sbStr.toString();
            if (ftpPath.indexOf('/') == -1) {
                // 只有一层目录
                ftpClient.changeWorkingDirectory(new String(ftpPath.getBytes(), "iso-8859-1"));
            } else {
                // 多层目录循环创建
                String[] paths = ftpPath.split("/");
                for (int i = 0; i < paths.length; i++) {
                    ftpClient.changeWorkingDirectory(new String(paths[i].getBytes(), "iso-8859-1"));
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 循环创建目录，并且创建完目录后，设置工作目录为当前创建的目录下
     *
     * @param ftpPath 需要创建的目录
     * @return
     */
    public boolean mkDir(String ftpPath) {
        if (!ftpClient.isConnected()) {
            return false;
        }
        try {
            // 将路径中的斜杠统一
            char[] chars = ftpPath.toCharArray();
            StringBuffer sbStr = new StringBuffer(256);
            for (int i = 0; i < chars.length; i++) {
                if ('\\' == chars[i]) {
                    sbStr.append('/');
                } else {
                    sbStr.append(chars[i]);
                }
            }
            ftpPath = sbStr.toString();
            print("ftpPath:" + ftpPath);
            if (ftpPath.indexOf('/') == -1) {
                // 只有一层目录
                ftpClient.makeDirectory(new String(ftpPath.getBytes(), "iso-8859-1"));
                ftpClient.changeWorkingDirectory(new String(ftpPath.getBytes(), "iso-8859-1"));
            } else {
                // 多层目录循环创建
                String[] paths = ftpPath.split("/");
                for (int i = 0; i < paths.length; i++) {
                    ftpClient.makeDirectory(new String(paths[i].getBytes(), "iso-8859-1"));
                    ftpClient.changeWorkingDirectory(new String(paths[i].getBytes(), "iso-8859-1"));
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 上传文件到FTP服务器
     *
     * @param localDirectoryAndFileName 本地文件目录和文件名
     * @param ftpFileName               上传到服务器的文件名
     * @param ftpDirectory              FTP目录如:/path1/pathb2/,如果目录不存在会自动创建目录
     * @return
     */
    public boolean upload(String localDirectoryAndFileName, String ftpFileName, String ftpDirectory) {
        if (!ftpClient.isConnected()) {
            print("ftp没有连接上");
            return false;
        }
        boolean flag = false;
        print("ftp 1");
        if (ftpClient != null) {
            print("ftp 2");
            File srcFile = new File(localDirectoryAndFileName);
            print("ftp 3");
            FileInputStream fis = null;
            try {
                print("ftp 4");
                fis = new FileInputStream(srcFile);
                print("ftp 5");
                // 创建目录
                this.mkDir(ftpDirectory);
                print("ftp 6");
                ftpClient.setBufferSize(100000);
                print("ftp 7");
                ftpClient.setControlEncoding("UTF-8");
                print("ftp 8");
                // 设置文件类型（二进制）
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                print("ftp 9");
                // 上传
                flag = ftpClient.storeFile(new String(ftpFileName.getBytes(), "iso-8859-1"), fis);
                print("ftp 10");
            } catch (Exception e) {
                print("ftp 11");
                this.close();
                e.printStackTrace();
                return false;
            } finally {
                print("ftp 12");
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        print("ftp 13");
        print("flag:" + flag + ",上传文件成功，本地文件名： " + localDirectoryAndFileName + "，上传到目录：" + ftpDirectory + "/" + ftpFileName);
        return flag;
    }

    /**
     * 从FTP服务器上下载文件
     *
     * @param ftpDirectoryAndFileName   ftp服务器文件路径，以/dir形式开始
     * @param localDirectoryAndFileName 保存到本地的目录
     * @return
     */
    public boolean get(String ftpDirectoryAndFileName, String localDirectoryAndFileName) {
        if (!ftpClient.isConnected()) {
            return false;
        }
        ftpClient.enterLocalPassiveMode(); // Use passive mode as default
        try {
            // 将路径中的斜杠统一
            char[] chars = ftpDirectoryAndFileName.toCharArray();
            StringBuffer sbStr = new StringBuffer(256);
            for (int i = 0; i < chars.length; i++) {
                if ('\\' == chars[i]) {
                    sbStr.append('/');
                } else {
                    sbStr.append(chars[i]);
                }
            }
            ftpDirectoryAndFileName = sbStr.toString();
            String filePath = ftpDirectoryAndFileName.substring(0, ftpDirectoryAndFileName.lastIndexOf("/"));
            String fileName = ftpDirectoryAndFileName.substring(ftpDirectoryAndFileName.lastIndexOf("/") + 1);
            this.changeDir(filePath);
            ftpClient.retrieveFile(new String(fileName.getBytes(), "iso-8859-1"),
                    new FileOutputStream(localDirectoryAndFileName)); // download
            // file
            print(ftpClient.getReplyString()); // check result
            print("从ftp服务器上下载文件：" + ftpDirectoryAndFileName + "， 保存到：" + localDirectoryAndFileName);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 返回FTP目录下的文件列表
     *
     * @param pathName
     * @return
     */
    public String[] getFileNameList(String pathName) {
        try {
            return ftpClient.listNames(pathName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 删除FTP上的文件
     *
     * @param ftpDirAndFileName 路径开头不能加/，比如应该是test/filename1
     * @return
     */
    public boolean deleteFile(String ftpDirAndFileName) {
        if (!ftpClient.isConnected()) {
            return false;
        }
        try {
            return ftpClient.deleteFile(ftpDirAndFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除FTP目录
     *
     * @param ftpDirectory
     * @return
     */
    public boolean deleteDirectory(String ftpDirectory) {
        if (!ftpClient.isConnected()) {
            return false;
        }
        try {
            return ftpClient.removeDirectory(ftpDirectory);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 关闭链接
     */
    public void close() {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
            print("成功关闭连接，服务器ip:" + this.server + ", 端口:" + this.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    /**
     * 打印输出
     *
     * @param msg
     */
    private void print(String msg) {
        if (openLog) {
            System.out.println(msg);
        }
    }

    public void setFtpModel(int ftpModel) {
        this.ftpModel = ftpModel;
    }

    public int getFtpModel() {
        return ftpModel;
    }
}