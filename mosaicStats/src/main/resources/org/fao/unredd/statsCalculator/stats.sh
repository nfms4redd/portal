gdal_rasterize -a $field -ot Int32 -ts $width $height -l $layerName $rasterizeInput $rasterizeOutput &&
gdal_merge.py -of GTiff -o $maskedAreaBands -separate $forestMask $areaRaster &&
echo -e "1\n#1 #2 *" | oft-calc -of GTiff -ot Int32 $maskedAreaBands $maskedArea &&
oft-stat -i $maskedArea -um $rasterizeOutput -o $tempStats
