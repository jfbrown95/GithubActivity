#/bin/bash
# This shell is to swap from web1 to web2
cd /etc/nginx
sed -e s?activity:8080/activity/?activity2:8080/activity2/? <nginx.conf > /tmp/xxx
cp /tmp/xxx nginx.conf
service nginx reload 
