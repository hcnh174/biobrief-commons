#!/bin/bash

source /home/nelson/workspace/biobrief-commons/scripts/functions.sh

# run_pdftotext.sh --pdffile filename.pdf --password secret --textfile filename.txt
# run_pdftotext.sh --pdffile /mnt/expertpanel/A005490048398/HRU20-008.2.2/HRU20-008.2.2/HRU20-008.2.2_report.pdf --password A0054 --textfile /mnt/out/temp/HRU20-008.2.2_report_jp.txt

#######################################

while [[ "$#" -gt 0 ]]; do case $1 in
  --pdffile) pdffile="$2"; shift;;
  --password) password="$2"; shift;;
  --textfile) textfile="$2"; shift;;
  *) echo "Unknown parameter passed: $1"; exit 1;;
esac; shift; done

[[ -z "$pdffile" ]] && { logerr "Error: --pdffile not set"; exit 1; }
#[[ -z "$password" ]] && { logerr "Error: --password not set"; exit 1; }
[[ -z "$textfile" ]] && { logerr "Error: --textfile not set"; exit 1; }

echo "pdffile: $pdffile"
echo "password: $password"
echo "textfile: $textfile"

#########################################################

echo "starting pdftotext" 

checkFileExists $pdffile || exit 1
[[ -f "$textfile" ]] && { rm $textfile; }

if [[ -n $password ]]; then
	password="-upw $password"
fi 

indir=$(dirname $pdffile); log "indir: $indir"
outdir=$(dirname $textfile); log "outdir: $outdir"
infile=$(basename $pdffile); log "infile: $infile"
outfile=$(basename $textfile); log "outfile: $outfile"
mkdir -p $outdir; cd $outdir

#docker run -v $indir:/mnt/in -v $outdir:/mnt/out pdftotextpass -enc UTF-8 -nopgbrk $password -layout /mnt/in/$infile /mnt/out/$outfile
docker run -v $indir:/mnt/in -v $outdir:/mnt/out hcnh174/pdftotextjp:v1 -enc UTF-8 -nopgbrk $password -layout /mnt/in/$infile /mnt/out/$outfile

checkReturnCode "pdftotext $pdffile" || exit 1
checkFileExists $textfile || exit 1 
 
echo "finished pdftotext"

######################################



