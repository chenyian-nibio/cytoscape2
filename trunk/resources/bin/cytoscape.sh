#!/bin/sh
#
# Run cytoscape from a jar file
# this is a linux-only version
#-------------------------------------------------------------------------------

#export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:./plugins

java -Dswing.aatext=true -Xss5M -Xmx512M -jar cytoscape.jar cytoscape.CyMain -p plugins "$@"

