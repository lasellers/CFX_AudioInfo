rem For ColdFusion10. Requires elevated privileges command line.

rem path = path;C:\Program Files\Java\jdk1.7.0_03\bin
rem classpath =
rem C:\ColdFusion10\cfusion\wwwroot\WEB-INF\lib;C:\ColdFusion10\cfusion\wwwroot\WEB-INF\lib\cfx.jar;
cd src

javac -classpath C:\ColdFusion10\cfusion\wwwroot\WEB-INF\lib\cfx.jar -verbose  -Xlint:deprecation log.java EOFException.java SingleFilenameFilter.java READ.java ITEM.java CFX_AudioInfo.java

jar cvf CFX_AudioInfo.jar CFX_AudioInfo.class
jar uvf CFX_AudioInfo.jar CFX_AudioInfo$1CHUNK_HDR.class
jar uvf CFX_AudioInfo.jar CFX_AudioInfo$1FMT.class
jar uvf CFX_AudioInfo.jar CFX_AudioInfo$1RIFF_HDR.class
jar uvf CFX_AudioInfo.jar ITEM.class
jar uvf CFX_AudioInfo.jar READ.class
jar uvf CFX_AudioInfo.jar SingleFilenameFilter.class
jar uvf CFX_AudioInfo.jar EOFException.class
jar uvf CFX_AudioInfo.jar log.class

rem del *.class

cd ..
