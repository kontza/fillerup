#!/bin/sh
URL="http://localhost:6110/trigger"
for i in $(seq 0 10) ;do http "$URL";done
