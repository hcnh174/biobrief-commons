#!/bin/bash

source /home/nelson/workspace/biobrief-commons/scripts/functions.sh

# run_phantomjs.sh --htmlfile /mnt/out/temp/J400604915052_F1/J400604915052_F1_trial_report.html --pdffile /mnt/out/temp/J400604915052_F1/J400604915052_F1_trial_report.pdf
# run_phantomjs.sh --htmlfile /mnt/out/phantomjs/test.html --pdffile /mnt/out/phantomjs/test.pdf

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

echo "starting phantomjs" 

export OPENSSL_CONF=/etc/ssl/

configfile=/mnt/tools/phantomjs-2.1.1-linux-x86_64/bin/config.js
checkFileExists $configfile || exit 1
checkFileExists $htmlfile || exit 1

[[ -f "$pdffile" ]] && { rm $pdffile; }

phantomjs $configfile $htmlfile $pdffile
checkReturnCode "phantomjs $htmlfile" || exit 1
checkFileExists $pdffile || exit 1 
 
echo "finished phantomjs"


# https://hub.docker.com/r/pickapp/phantomjs/tags
# docker pull pickapp/phantomjs:2.2.0-dev
# docker run pickapp/phantomjs:2.2.0-dev $configfile $htmlfile $pdffile

#docker run pickapp/phantomjs:2.2.0-dev -v /mnt/tools/phantomjs-2.1.1-linux-x86_64/bin:/ -v /mnt/out/temp/J400604915052_F1:/mnt /config.js /mnt/J400604915052_F1_trial_report.html /mnt/J400604915052_F1_trial_report.pdf

# https://hub.docker.com/r/wernight/phantomjs
#docker run -it --rm wernight/phantomjs -v /mnt/tools/phantomjs-2.1.1-linux-x86_64/bin:/home/phantomjs -v /mnt/out/temp/J400604915052_F1:/mnt
#docker run wernight/phantomjs -v /mnt/tools/phantomjs-2.1.1-linux-x86_64/bin:/home/phantomjs -v /mnt/out/temp/J400604915052_F1:/mnt /home/phantomjs/config.js /mnt/J400604915052_F1_trial_report.html /mnt/J400604915052_F1_trial_report.pdf