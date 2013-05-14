# <classification_layer> Contains a classification layer, typically with administrative polygons
# <classification_field> Specifies the field in <shp> that is unique identifier of the classes
# tif0 tif1 ... tifN Specifies the ordered list of forest masks to calculate the statistics
#
# Uses the stats.sh script to calculate the zonal statistics for each of the forest mask received as parameters

classificationLayer=`java -jar nfms-utils.jar -l`

while getopts ":l:f:" opt; do
  case $opt in
    l)
      classificationLayer=$OPTARG
      ;;
    f)
      classificationField=$OPTARG
      ;;
    \?)
      echo "Invalid option: -$OPTARG" 
      echo "Usage: `basename $0` -l <classification_layer> -f <classification_field> tif0 tif1 ... tifN"
      exit 1
      ;;
    :)
      echo "Option -$OPTARG requires an argument." 
      exit 1
      ;;
  esac
done

if [ $OPTIND -gt $# ]
then
	echo "At least a tif file must be specified"
	exit 1
elif [ -z "$classificationLayer" ]
then
	echo "A classification layer must be specified"
	exit 1
elif [ -z "$classificationField" ]
then
	echo "A classification field must be specified"
	exit 1
fi

firstTiff=${@:$OPTIND:1}
numTiffs=$(($# - $OPTIND + 1))

#echo "Will process ${@:$OPTIND}, a total of $numTiffs"

#echo "Processing... $firstTiff"
./stats.sh $classificationLayer $classificationField $firstTiff /tmp/result$$_i_1.txt > /dev/null

for ((i=1; i< numTiffs; i++))
do
	index=$OPTIND+$i
	tiff=${@:$index:1}
#	echo "Processing... $tiff"
	./stats.sh $classificationLayer $classificationField $tiff /tmp/result$$_i.txt > /dev/null
	join /tmp/result$$_i_1.txt /tmp/result$$_i.txt > /tmp/join$$.txt 
	mv /tmp/join$$.txt /tmp/result$$_i_1.txt
done
cat /tmp/result$$_i_1.txt