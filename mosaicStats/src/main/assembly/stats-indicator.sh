STATS_INDICATOR_HOME=`dirname $0`

CP=
for i in `ls ${STATS_INDICATOR_HOME}/lib/*.jar`
do
  CP=${CP}:${i}
done

#---------------------------#
# run the program           #
#---------------------------#
MAIN=org.fao.unredd.statsCalculator.StatsIndicator
java -cp ".:${CP}" $MAIN "$@"