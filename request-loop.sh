#!/bin/sh
URL="http://localhost:6110/trigger?dirty=true"
for i in $(seq 0 1000) ;do http "$URL";done
