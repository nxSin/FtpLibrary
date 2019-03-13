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

# License
 
The MIT License
 
Copyright (c) <year> <copyright holders>
 
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
 
The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.
 
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
