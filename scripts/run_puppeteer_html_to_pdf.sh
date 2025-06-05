#!/bin/bash

source /home/nelson/workspace/biobrief-commons/scripts/functions.sh

# run_puppeteer_html_to_pdf.sh --htmlfile /mnt/out/temp/Z401614157127_F1/Z401614157127_F1_trial_report.html --pdffile /mnt/out/temp/Z401614157127_F1/Z401614157127_F1_trial_report.pdf

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

checkFileExists $htmlfile || exit 1

[[ -f "$pdffile" ]] && { rm $pdffile; }

echo "starting puppeteer" 

node /home/nelson/tools/puppeteer/convert_html_to_pdf.js $htmlfile $pdffile
checkReturnCode "puppeteer $htmlfile" || exit 1
#checkFileExists $pdffile || exit 1
 
echo "finished puppeteer"
