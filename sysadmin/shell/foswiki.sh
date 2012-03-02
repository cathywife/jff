#!/bin/sh

set -e -x

SCRIPT_DIR=$(readlink -f $(dirname $0))
. $SCRIPT_DIR/lib.sh


[ -z "$CONF_CHANGED" ] || service apache2 restart

ensure_service_started apache2 apache2
