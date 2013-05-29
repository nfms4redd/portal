# <shp> Contains a classification layer, typically with admnistrative polygons
# <classification_field> Specifies the field in <shp> that is unique identifier of the classes
# <tif> Forest mask with values 1/0 for presence/absence of forest
# <result> Name of the file to store the results to

# Produces tabular data with the provinces in the input shapefile as fields 
# and a column with the amount of fixels in the forest mask that are set to one.

# Validate the number of parameters
if [ $# -ne 4 ]
then
	echo "Usage: `basename $0` <shp> <classification_field> <tif> <result>"
	exit -1
fi

# Give well known names to the parameters
shp=$1
fieldId=$2
tif=$3
res=$4
tempraster=/tmp/rast$$.tif
layername=$(basename $shp)
layername=${layername%.*}

# Check field exists
ogrinfo -so -fields=YES $shp $layername | grep "${fieldId}:" 
if [ $? -ne 0 ]
then
	echo "cannot find the field: $fieldId"
	exit -1
fi

printf "\n--Generating temporal results in: $tempraster\n" &&

# Rasterize the provinces
size=`gdalinfo $tif | grep 'Size is'` &&
width=`echo $size | awk -F', | ' '{print $3}'` &&
height=`echo $size | awk -F', | ' '{print $4}'` &&
printf "\n--Rasterizing at ${width}, ${height}\n" &&
gdal_rasterize -a $fieldId -ot Byte -ts $width $height -l $layername $shp $tempraster &&

# Obtain the statistics
printf "\n--Generating stats in $res\n" &&
oft-stat -i $tif -um $tempraster -o /tmp/stats$$.txt &&

# Obtain the sum
tempstats=/tmp/stats$$.txt &&
awk < $tempstats 'BEGIN{print "prov number avg sum"} {print $1,$2,$3,$2*$3}' > ${res} &&

printf "\n--clean up\n"
rm $tempraster
rm $tempstats
