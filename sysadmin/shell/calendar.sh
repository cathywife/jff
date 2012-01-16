#!/bin/sh

set -e -x

SCRIPT_DIR=$(readlink -f $(dirname $0))
. $SCRIPT_DIR/lib.sh

cmp_file $SCRIPT_DIR/etc/default/calendarserver /etc/default/calendarserver || {
    overwrite_file $SCRIPT_DIR/etc/default/calendarserver /etc/default/calendarserver
    CONF_CHANGED=1
}

cmp_dir $SCRIPT_DIR/etc/caldavd /etc/caldavd || {
    overwrite_file $SCRIPT_DIR/etc/caldavd /etc/caldavd
    CONF_CHANGED=1
}

[ -z "$CONF_CHANGED" ] || service calendarserver restart

[ "`pidof memcached`" ] || service memcached start
[ "`pidof caldavd`" ] || service calendarserver start

