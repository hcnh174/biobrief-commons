echo "loading biobrief-commons functions"

export ERROR_FILE=/mnt/out/errors.txt
export LOG_FILE=/mnt/out/log.txt
export TRACE_FILE=/mnt/out/trace.txt

#source /home/nelson/workspace/biobrief-commons/scripts/env.sh

###############################################

# clears the screen and the scrollback buffer
# https://apple.stackexchange.com/questions/31872/how-do-i-reset-the-scrollback-in-the-terminal-via-a-shell-command
function cls {
 clear && printf '\e[3J'
}

function echoerr {
 cat <<< "Error: $@" 1>&2;
}

function logerr {
 cat <<< "Error: $@" 1>&2
 echo "$(date +%FT%T) $(hostname -s): $@" >> $ERROR_FILE
}

function log {
 cat <<< "Log: $@";
 echo "$(date +%FT%T) $(hostname -s): $@" >> $LOG_FILE
}

function trace {
 cat <<< "Trace: $@";
 echo "$(date +%FT%T) $(hostname -s): $@" >> $TRACE_FILE
}

function checkFileExists {
 local filename=$1
 [[ ! -f "$filename" ]] && { logerr "$filename does not exist"; return 1; }
 [[ ! -s "$filename" ]] && { logerr "$filename is empty"; return 1; }
 return 0
}

function checkFileNotExists {
 local filename=$1
 [[ -f "$filename" ]] && { logerr "Error: $filename already exists"; return 1; }
 return 0
}

function checkDirExists {
 local dir=$1
 [[ ! -d "$dir" ]] && { logerr "$dir does not exist"; return 1; }
 return 0
}

function checkReturnCode {
 local code=$?
 local command=$1 
 echo "code=$code"
 [[ $code -ne "0" ]] && { logerr "command $command failed"; return $code; }
 return 0
}
#dfsf; checkReturnCode "dfsf"

function logUndefined {
 local varname=$1
 logerr "$varname is undefined"
}

function checkDefined {
 local varname=$1
 [[ -z "$varname" ]] && logUndefined $varname
}

