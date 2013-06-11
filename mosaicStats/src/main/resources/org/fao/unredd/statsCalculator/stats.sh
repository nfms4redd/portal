gdal_rasterize -a $field -ot Byte -ts $width $height -l $layerName $rasterizeInput $rasterizeOutput
oft-stat -i $areaRaster -um $rasterizeOutput -o $tempStats
