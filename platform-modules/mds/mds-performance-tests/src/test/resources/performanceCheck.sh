#!/bin/bash

function printHelp {
    echo "PerformanceCheck script"
    echo "[DESCRIPTION]"
    echo "  Script takes 2 files as arguments, from which first represents performance tests resaults and second one specifies maximum values for test. Both files must contain CSV in form of simpleClassName,methodName,value."
    echo "[USAGE]"
    echo "  performanceCheck [-h] [-d performanceHistoryDir] <fileWithValues> <fileWithMaximumValues>"
    echo "  h       -       Prints this help messsage."
    echo "  d       -       Allow specifing performance history location."
    echo "[RETURN VALUES]"
    echo "  0       -       Executed with success."
    echo "  1       -       Some method exceeded resource limit."
    echo "  2       -       Maximum value for some method wasn't specified in file."
    echo "  4       -       Missing argument(s)."
    echo "  8       -       Invalid option."

}

SUCCESS=0
RESOURCE_LIMIT_EXCEEDED=1
MAXIMUM_VALUE_MISSING=2
ARGUMENTS_MISSING=4
INVALID_OPT=8
CURRENT_DATE=`date`

PRINT_HELP=false
SPECIFY_LOCATION=false

while getopts hd: OPT; do
    case "$OPT" in
        h)  PRINT_HELP=true;;
        d)  SPECIFY_LOCATION=true
            PERFORMANCE_HISTORY_DIR=$OPTARG;;
        *) exit $INVALID_OPT;;
    esac
done

if [ "$PRINT_HELP" == "true" ]
then
    printHelp
    exit $SUCCESS
fi


if [ "$OPTIND" -gt 1 ]
then
    shift $((OPTIND-1))
fi

if [ "$1" == "" ] || [ "$2" == "" ]
then
    printHelp
    exit $ARGUMENTS_MISSING
fi


if [ ! -d $PERFORMANCE_HISTORY_DIR ];
then
    mkdir -p $PERFORMANCE_HISTORY_DIR
fi

EXIT_STATUS=$SUCCESS

while read p; do
    ID=`echo $p | cut -d',' -f1,2`
    CLASS_NAME=`echo $p | cut -d',' -f1`
    METHOD_NAME=`echo $p | cut -d',' -f2`
    RES_USED=`echo $p | cut -d',' -f3`
    MAX_RES_USED=`grep $ID $2 | cut -d',' -f3`
    STATUS="passed"
    if [ "$MAX_RES_USED" == "" ]
    then
        echo "[ERROR]: Maximum value for "$CLASS_NAME"."$METHOD_NAME" is not specified in file."
        if [ "$EXIT_STATUS" -ne "1" ]
        then
            EXIT_STATUS=$MAXIMUM_VALUE_MISSING
        fi
    else
        if [ "$RES_USED" -gt "$MAX_RES_USED" ]
        then
            echo "[ERROR]: Exceeded! "$CLASS_NAME"."$METHOD_NAME" result - "$RES_USED", but threshold is "$MAX_RES_USED"."
            EXIT_STATUS=$RESOURCE_LIMIT_EXCEEDED
            STATUS="failed"
        else
            echo "[INFO]: "$CLASS_NAME"."$METHOD_NAME" result - "$RES_USED"."
        fi
    fi
    if [ "$SPECIFY_LOCATION" == "true" ]
    then
        echo $CURRENT_DATE","$RES_USED","$STATUS >> $PERFORMANCE_HISTORY_DIR"/"$CLASS_NAME"#"$METHOD_NAME".log"
    fi
done < $1

exit $EXIT_STATUS

