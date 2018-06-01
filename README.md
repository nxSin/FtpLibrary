# FtpLibrary
FtpLibrary for Android 

# Add Library

1. add maven repsit

```
repositories {
    maven {
        url  "https://dl.bintray.com/nsinlibrary/mylibrary"
    }
}
```

2. add dependencies

```
compile 'com.xin:Ftplibrary:1.0'
```

# Sample Usage

## Open Log

log default is close , if you want open log

```
FtpUtils ftpUtils = new FtpUtils("host", port, "userName", "userPsw");
ftpUtils.setDebug(true);
```

## Upload File

```
        FtpUtils ftpUtils = new FtpUtils("host", port, "userName", "userPsw");
        //连接前先设置连接模式，1为主动模式，2为被动模式。（默认为主动模式）
        ftpUtils.setFtpModel(FtpUtils.ActiveMode);
        if (ftpUtils.open()) {
            String fileName = "111.txt";
            //upload(本地文件目录和文件名,上传到服务器的文件名,FTP目录如:/path1/pathb2/,如果目录不存在会自动创建目录)
            ftpUtils.upload(getDir() + "/tmpUserImage.jpg",
                    "person_001.jpg",
                    "/path1");
        }
```
