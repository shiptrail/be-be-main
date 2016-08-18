#!/bin/bash
echo "Validating install"

#HOSTNAME=shiptrail.lenucksi.eu
HOSTNAME=$1

SERVES_PLAY=`curl -k http://$HOSTNAME/ 2>/dev/null | grep "Welcome to Play" -c`
if [ "$SERVES_PLAY" -ge "1" ]; then
        echo "/ Serves Play BE OK"
    true
else
        echo "/ Serves Play BE FAIL"
    false
fi

SERVES_FE=`curl -L -v -k http://$HOSTNAME/fe/ 2>/dev/null | grep "ng-app=gulpAngular" -c`
if [ "$SERVES_FE" -ge "1" ]; then
        echo "/fe/ Serves FE OK"
    true
else
        echo "/fe/ Serves FE FAIL"
    false
fi

cd /tmp
curl -O -L -v -k http://$HOSTNAME/swpdvtracker.apk 2>/dev/null
ls swpdvtracker.apk && echo "Serves Android APK OK"
rm -v swpdvtracker.apk || true
