sudo pigpiod

cd /home/pi/Sketchy

freemem=$(( `free -m | cut -b 35-40 | sed -n 2p | tr -d ' '` * 9 / 10))
memargs='-Xms'$freemem'm -Xmx'$freemem'm'

echo 'Starting JVM with '$freemem' MBytes'
java  $memargs -cp .:./*:./lib/* com.sketchy.server.HttpServer
