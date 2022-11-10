#!/bin/sh
URL="http://localhost:6110/trigger"
if test -n "$1";then
  URL='http://localhost:6110/trigger/?clean-up=false'
fi
for i in $(seq 0 10) ;do http "$URL";done
