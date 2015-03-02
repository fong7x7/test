#! /bin/sh

### BEGIN INIT INFO
# Provides:          usernotebuilder
# Required-Start:    $remote_fs $network $syslog
# Required-Stop:     $remote_fs $network $syslog
# Default-Start:     3 5
# Default-Stop:      0 1 6
# Short-Description: Start Workfront Notification Builder
# Description:       Control the Workfront User-Notification Builder Service
### END INIT INFO
# chkconfig: 345 84 16

[ -f "/etc/rc.d/init.d/functions" ] && . /etc/rc.d/init.d/functions
[ -z "$JAVA_HOME" -a -x /etc/profile.d/java.sh ] && . /etc/profile.d/java.sh

CLASS_NAME=com.workfront.usernotebuilder.ServiceApplication
APP_NAME=usernotebuilder
WORKFRONT_HOME=${WORKFRONT_HOME:-"/opt/attask"}
DIR="$WORKFRONT_HOME/usernotebuilder"
LIB_DIR="$DIR/lib"
PIDFILE="$DIR/$APP_NAME.pid"
JAVA_HOME=${JAVA_HOME:-"/usr/java/default"}
#"jms" user is taken from search indexer, as this service will run in proximity
RUN_AS_USER=${RUN_AS_USER:-"jms"}

# -Djava.util.logging.config.file=logging.properties
OPTIONS="-Xms1G -Xmx1G -XX:MaxPermSize=512m"

# Need to migrate to a $WORKFRONT_HOME...
CONF_FILE="$DIR/conf/$APP_NAME.env"

if [ -e "$CONF_FILE" ]; then
    echo "INFO: reading configuration file $CONF_FILE"
    source "$CONF_FILE"
fi

if [ -z "$JAVACMD" ]; then
    if [ -n "$JAVA_HOME"  ] ; then
        JAVACMD="$JAVA_HOME/bin/java"
    fi
fi

if [ ! -x "$JAVACMD" ] ; then
    JAVACMD=`which java 2> /dev/null `
    if [ -z "$JAVACMD" ] ; then
        JAVACMD=java
    fi
fi

if [ ! -x "$JAVACMD" ]; then
    echo "ERROR: cannot find java. Set JAVA_HOME or JAVACMD."
    exit 1
fi

# TODO consider Spring Boot Actuator application.pid generation instead
echo "INFO: pid is at $PIDFILE"
pid=""

# where is pgrep on our preferred linux distro?
PGREP="/usr/bin/pgrep"
if [ ! -x $PGREP ]; then
    PGREP="/usr/local/bin/pgrep"
    if [ ! -x $PGREP ]; then
        PGREP=`which pgrep 2> /dev/null`
        if [ ! -x $PGREP ]; then
            echo "ERROR: Unable to locate 'pgrep' - expected at $PGREP"
            echo "Please report this message and the location of the command for your system."
            exit 1
        fi
    fi
fi

checkRunning() {
    getpid
    if [ "X$pid" = "X" ]; then
        return 1;
    fi
    return 0;
}

getpid() {
    if [ -f $PIDFILE ]; then
        if [ -r $PIDFILE ]; then
            pid=`cat $PIDFILE`
            if [ "X$pid" != "X" ]; then
                matchpid
            fi
        else
            echo "ERROR: Cannot read $PIDFILE."
            exit 1
        fi
    fi
}

matchpid() {
    pid=`$PGREP -o java -F $PIDFILE`
    if [ "X$pid" = "X" ]; then
        echo "INFO: Removed stale pid file: $PIDFILE"
        rm -f $PIDFILE
        pid=""
    fi
}

start() {
    if (checkRunning); then
        PID=`cat $PIDFILE`
        echo "INFO: Process with pid '$PID' is already running"
        return 0
    fi

    #echo "dirname: $DIR"

    # first check if there is a lib directory
    # if so, include all jar files in this lib directory on the classpath
    if [ ! -d "$LIB_DIR" ]; then
        echo "ERROR: lib directory does not exist: $LIB_DIR"
    fi

    echo "using libraries in $LIB_DIR"

    #Why clear the classpath? CLASSPATH=""
    for jar in `ls $LIB_DIR`; do
        CLASSPATH="$LIB_DIR/$jar:$CLASSPATH"
    done

    export CLASSPATH

    su -m $RUN_AS_USER -c \
        "$JAVACMD $OPTIONS $CLASS_NAME >> $DIR/logs/startup.log 2>&1 &
        echo \$! > $PIDFILE" $RUN_AS_USER

    RETVAL=$?

    # make sure java was started successfully
    if ! (checkRunning); then
        echo "ERROR: error starting java process"
        return 1
    fi

    echo "INFO: pid $(cat $PIDFILE) stored in $PIDFILE"

    return 0
}

stop(){
    if (checkRunning); then
        PID=`cat $PIDFILE`
        echo "INFO: stopping process $PID..."
        kill $PID
        rm $PIDFILE
        return 0
    else
        echo "not running"
        return 0
    fi
}

restart() {
    stop
    start
    status
    return "$?"
}

status(){
    echo "INFO: checking status..."
    if (checkRunning); then
        echo "INFO: $APP_NAME is running at $pid"
        return 0;
    else
        echo "INFO: $APP_NAME is NOT running"
        return 1;
    fi
}

help() {
    echo "Usage: $0 {start|stop|restart|status}"
}

case "$1" in
    start)
        start
        exit "$?"
        ;;
    stop)
        stop
        exit "$?"
        ;;
    restart)
        restart
        exit "$?"
        ;;
    status)
        status
        exit "$?"
        ;;
    *)
        help
        exit 1
        ;;
esac

exit 0;
