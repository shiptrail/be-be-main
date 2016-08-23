#!/bin/bash
echo "Validating install"

#HOSTNAME=shiptrail.lenucksi.eu
HOSTNAME=$1

SERVES_PLAY_RESULT=false
SERVES_PLAY=`curl -k http://$HOSTNAME/ 2>/dev/null | grep "np-app=gulpAngular" -c`
if [ "$SERVES_PLAY" -ge "1" ]; then
    echo "/ Serves Play BE OK and redirects to FE OK"
    SERVES_PLAY_RESULT=true
    true
else
    echo "/ Serves Play BE and redirects to FE FAIL"
    SERVES_PLAY_RESULT=false
    false
fi

SERVES_FE=`curl -L -v -k http://$HOSTNAME/fe/ 2>/dev/null | grep "ng-app=gulpAngular" -c`
if [ "$SERVES_FE" -ge "1" ]; then
    echo "/fe/ Serves FE OK"
    SERVES_FE_RESULT=true
    true
else
    echo "/fe/ Serves FE FAIL"
    SERVES_FE_RESULT=false
    false
fi

cd /tmp
curl -O -L -v -k http://$HOSTNAME/swpdvtracker.apk 2>/dev/null
test -f swpdvtracker.apk && echo "Serves Android APK OK"
rm -v swpdvtracker.apk || true

$SERVES_PLAY_RESULT && $SERVES_FE_RESULT && `test -f swpdvtracker.apk`
