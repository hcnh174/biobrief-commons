#!/bin/bash

source /home/nelson/workspace/biobrief-commons/scripts/functions.sh

# run_phantomjs.sh -htmlfile /mnt/out/phantomjs/test.html --pdffile /mnt/out/phantomjs/test.pdf

#######################################

while [[ "$#" -gt 0 ]]; do case $1 in
  --htmlfile) htmlfile="$2"; shift;;
  --pdffile) pdffile="$2"; shift;;
  *) echo "Unknown parameter passed: $1"; exit 1;;
esac; shift; done

[[ -z "$htmlfile" ]] && { logerr "Error: --htmlfile not set"; exit 1; }
[[ -z "$pdffile" ]] && { logerr "Error: --pdffile not set"; exit 1; }

echo "htmlfile: $htmlfile"
echo "pdffile: $pdffile"

#########################################################

echo "starting " 

configfile=$PHANTOMJS_HOME/config.js
checkFileExists $configfile || exit 1
checkFileExists $htmlfile || exit 1

[[ -f "$pdffile" ]] && { rm $pdffile; }

$PHANTOMJS_HOME/phantomjs $configfile $htmlfile $pdffile
checkReturnCode "phantomjs $htmlfile" || exit 1
checkFileExists $pdffile || exit 1 
 
echo "finished phantomjs"
