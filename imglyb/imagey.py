import glob
import re
import sys
import time

from PyQt5 import QtGui, QtCore, QtWidgets

class MainWindow( QtWidgets.QWidget ):
	def __init__( self, imagej ):
		super( MainWindow, self ).__init__()
		self.layout = QtWidgets.QGridLayout()
		self.layout.addWidget( imagej, 0, 0, 6, 6 )

def get_parent_id_xlib( name ):
	from Xlib import X
	from Xlib.display import Display
	display = Display()
	root = display.screen().root
	children = root.query_tree().children
	parent_ids = []
	for c in children:
		c_name = c.get_wm_name()
		print (c_name)
		if c_name and name in c_name:
			parent_ids.append( c.id )

	return parent_ids		
		

FIJI_JARS_DIR= '/home/phil/local/Fiji.app/jars'
FIJI_PLUGIN_DIR = '/home/phil/local/Fiji.app/plugins'

match_string = r'(imglib2-([0-9]|algorithm-[0-9])|bigdataviewer)'
matcher = re.compile( match_string )

libs = [ jar for jar in sorted( glob.glob( '{}/*.jar'.format( FIJI_JARS_DIR ) ) ) if not matcher.search( jar ) ]
plugin_jars = [ jar for jar in sorted( glob.glob( '{}/*.jar'.format( FIJI_PLUGIN_DIR ) ) ) if not matcher.search( jar ) ]

jars = libs + plugin_jars


import os
if 'CLASSPATH' in os.environ:
	os.environ['CLASSPATH'] = ':'.join( jars + os.environ['CLASSPATH'].split(':') )
else:
	os.environ['CLASSPATH'] = ':'.join( jars )

os.environ['JVM_OPTIONS'] = '-Dplugins.dir={}'.format( FIJI_PLUGIN_DIR )


import imglyb
from jnius import autoclass

ImageJ = autoclass( 'ij.ImageJ' )
ij = ImageJ()
title = 'oge'
ij.setTitle( title )
time.sleep( 1 )
ids = get_parent_id_xlib( 'ij-ImageJ' )
print( 'ids:', ids, ij.getTitle() )

window = QtGui.QWindow.fromWinId( ids[0] )
# frame = QtWidgets.QWidget.createWindowContainer( window )

app = QtWidgets.QApplication([])
sys.exit( app.exec_() )



