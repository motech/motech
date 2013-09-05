FILE=${PWD}/target/tomcat-processes-list

if [ -f $FILE ]
then
    rm -f $FILE
fi

ps ux | grep 'target/tomcat' | grep -v grep > $FILE

TOMCAT_PROCESSES_COUNT=$(wc -l < $FILE)

echo "There are $TOMCAT_PROCESSES_COUNT running Tomcat processes"
echo "You can find Tomcat processes list in $FILE"

exit $TOMCAT_PROCESSES_COUNT
