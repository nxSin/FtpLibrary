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
compile 'com.xin:Ftplibrary:1.1'
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
        //set connect mode，1：active model，2：passivity mode（default）
        ftpUtils.setFtpModel(FtpUtils.ActiveMode);
        if (ftpUtils.open()) {
            String fileName = "111.txt";
            //upload(localUrl,remote fileName,FTP Path:/path1/pathb2/（will create when if not exist)
            ftpUtils.upload(getDir() + "/tmpUserImage.jpg",
                    "person_001.jpg",
                    "/path1");
        }
```
