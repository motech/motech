#!/bin/bash

# get current working directory
CWD=$(pwd)
NAME=$(basename ${CWD})

if [ ${NAME} = '.tx' ]; then
    # go to main repository directory
    TARGET=${CWD}
    cd ..
else
    TARGET="${CWD}/.tx"
fi

# get list of path to messages.properties files
FILES=$(find | grep -v target | grep messages.properties | sort)

# remove old config file
if [ -f ${TARGET}/config ]; then
    rm -rf ${TARGET}/config
fi

echo "[main]" >> config
echo "host = https://www.transifex.com" >> config
echo >> config

# generate new config file
for f in ${FILES}; do

# remove two first characters > ./
FILE="${f:2}"

# get module name
NAME=$(echo ${FILE} | awk -F 'src' '{print $1}' | awk -F '/' '{print $(NF-1)}')
EXT=$(echo ${FILE} | awk -F 'resources/' '{print $2}' | awk -F '/' '{print $1}')

# for some modules we have to change name
if [ ${NAME} == 'tasks' ]; then
    NAME="tasks-bundle"
elif [ ${NAME} == 'http-bundle-archetype' ]; then
    NAME="minimal-bundle-archetype"
elif [ ${NAME} == 'web-security' ] && [ ${EXT} == 'webapp' ]; then
    NAME="web-security-bundle"
fi

# get path to directory where messages.properties exists
MDIR=$(dirname ${FILE})

echo "[MOTECH.$NAME]" >> config
echo "file_filter = $MDIR/messages_<lang>.properties" >> config
echo "source_file = $FILE" >> config
echo "source_lang = en_US" >> config
echo "type = PROPERTIES" >> config
echo >> config

done

mv config ${TARGET}/
cd ${CWD}
