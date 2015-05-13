sudo pigpiod

cd /home/pi/Sketchy
java -Xms384m -Xmx384m -cp .:./*:./lib/* com.sketchy.server.HttpServer
