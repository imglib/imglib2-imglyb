import glob
import re
import sys
import time

from PyQt5 import QtGui, QtCore, QtWidgets
from qtconsole.rich_ipython_widget import RichJupyterWidget
from qtconsole.inprocess import QtInProcessKernelManager

global toggleShow
toggleShow = True

class HidingWidget( QtWidgets.QWidget ):
	def __init__( self, parent ):
		super( HidingWidget, self ).__init__( parent )

	def closeEvent( self, event ):
		print( "Hiding!" )
		self.hide()
		print( "Done hiding!" )
		print( self.parent() )

		# layout = QtWidgets.QVBoxLayout(parent)
    # print( 9 )
    # widget = RichJupyterWidget(parent=parent)
    # print( 10 )
    # layout.addWidget(widget)
    # print( 11 )
    # widget.kernel_manager = kernel_manager
    # print( 12 )
    # widget.kernel_client = kernel_client
    # print( 13 )
    # widget.exit_requested.connect(stop)
    # print( 14 )
    # ipython_widget = widget
    # print( 15 )
    # ipython_widget.show()
    # kernel.shell.push({'widget':widget,'kernel':kernel, 'parent':parent})
    # print( 16 )
    # return {'widget':widget,'kernel':kernel}

class IPythonWidget( HidingWidget ):
	def __init__( self, parent, kernel_manager, kernel_client, kernel ):
	# def __init__( self, parent ):
		super( IPythonWidget, self ).__init__( parent )
		# kernel_manager = QtInProcessKernelManager()
		# kernel_manager.start_kernel()
		# kernel = kernel_manager.kernel
		# print( "init", 1 )
		# kernel.gui = 'qt'
		# print( "init", 1.1 )

		# kernel_client = kernel_manager.client()
		# print( "init", 1.2 )
		# kernel_client.namespace = self
		# print( "init", 1.25 )
		# try:
		# 	kernel_client.start_channels()
		# except Exception as e:
		# 	print (e)
		# 	raise e
		# print( "init", 1.3 )

		print( "init", 2 )
		self.layout = QtWidgets.QVBoxLayout(parent)
		print( "init", 3 )
		# print( 9 )
		# print( 10 )
		self.kernel_manager = kernel_manager
		print( "init", 4 )
		self.kernel_client = kernel_client
		print( "init", 5 )
		self.kernel = kernel
		print( "init", 6 )
		# self.current_widget = QtWidgets.QLabel("WAAAAAAAAT", self)
		self.current_widget = RichJupyterWidget( parent = self )
		print( "init", 7 )
		self.current_widget.kernel_manager = kernel_manager
		print( "init", 9 )
		self.layout.addWidget( self.current_widget )
		print( "init", 10 )
		ipython_widget = self.current_widget
		print( "init", 12 )
		# ipython_widget.show()
		self.kernel.shell.push({'widget':ipython_widget,'kernel':self.kernel, 'parent':self})
		print( "init", 8 )
		print( "init", 8.1 )
		print( "init", 8.2 )
		print( "init", 8.3 )
		try:
			self.current_widget.kernel_client = kernel_client
		except Exception as e:
			print( e )
			raise e
		print( "init", 13 )
		print( "init", 14 )

	def closeEvent( self, event ):
		toggleShow = False
		print( "hide", 1 )
		self.current_widget.hide()
		print( "hide", 2 )
		self.hide()
		print( "hide", 3 )
		# event.accept()

	def showEvent( self, event ):
		print( "show", 1 )
		self.setVisible( True )
		print( "show", 2 )
		self.current_widget.show()
		print( "show", 3 )


		

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
from imglyb import util
from jnius import autoclass, cast, PythonJavaClass, java_method

class ActionListener( PythonJavaClass ):

	__javainterfaces__ = [ 'java/awt/event/ActionListener' ]
	
	def __init__( self, func ):
		super( ActionListener, self ).__init__()
		self.func = func
		print( 'cn', util.Helpers.className( self ) )

	@java_method('(Ljava/awt/event/ActionEvent;)V')
	def actionPerformed( self, e ):
		# try:
		# 	print( "Running action" )
		# except Exception as e:
		# 	print (e)
		# 	raise e
		try:
			self.func( e )
		except Exception as e:
			print (e)
			raise e
		try:
			print( "Ran action" )
		except Exception as e:
			print (e)
			raise e


ImageJ = autoclass( 'ij.ImageJ' )
Menu = autoclass( 'java.awt.Menu' )
MenuItem = autoclass( 'java.awt.MenuItem' )
ij = ImageJ()
title = 'imagej with qtconsole'
ij.setTitle( title )

menu_bar = ij.getMenuBar()
menu_count = menu_bar.getMenuCount()
menu_name = 'Plugins'
scripting_name = 'Scripting'
menus = [ menu_bar.getMenu( i ) for i in range( menu_count ) if menu_name == menu_bar.getMenu( i ).getLabel() ]
print( menus[ 0 ].getLabel() )
menu = menus[ 0 ] if len( menus ) > 0 else menu_bar.add( Menu( menu_name ) )
items = [ menu.getItem( i ) for i in range( menu.getItemCount() ) if scripting_name == menu.getItem( i ).getLabel() ]
print( items )
created = Menu( scripting_name )
print( created )
print( util.Helpers.className( created ), util.Helpers.className( menu ) )

sub_menu = cast( 'java.awt.Menu', items[0] if len(items) > 0 else menu.add( cast( 'java.awt.MenuItem', Menu( scripting_name ) ) ) )
cpython_entry = sub_menu.add( MenuItem( "CPython" ) )
print( cpython_entry )
def change_toggle( toggle ):
	global toggleShow
	toggleShow = toggle
# listener = ActionListener( lambda e : print("WOT WOT" ) )
listener = ActionListener( lambda e : change_toggle( True ) )
print( 'listener', util.Helpers.className (listener ) )
cpython_entry.addActionListener( listener )




kernel_manager = QtInProcessKernelManager()
kernel_manager.start_kernel()
kernel = kernel_manager.kernel
kernel.gui = 'qt'

kernel_client = kernel_manager.client()
kernel_client.start_channels()

app = QtWidgets.QApplication([])
app.setQuitOnLastWindowClosed( False )
widget = IPythonWidget( None, kernel_manager, kernel_client, kernel )
# widget.show()

import threading
def thread_target():
	while( True ):
		if toggleShow:
			widget.show()
	time.sleep( 0.01 )
		
t = threading.Thread( target = thread_target )
t.start()
		
def send_signal():
	print( "MIZZGE " )
	# try:
	# 	t.showWidget()
	# except Exception as e:
	# 	print (e)
	# 	raise e

# cpython_entry.addActionListener( ActionListener( lambda e : send_signal() ) )



# app.exec_()
	
	

# dummy = QtWidgets.QWidget()

# app.aboutToQuit.connect( lambda : widget.hide() )



# def run_qtconsole( kernel_manager, kernel_client, kernel ):
# 	app = QtWidgets.QApplication([])
# 	widget = IPythonWidget( None, kernel_manager, kernel_client, kernel )
# 	widget.show()
# 	app.exec_()
	

# cpython_entry.addActionListener( ActionListener( lambda e : run_qtconsole( kernel_manager, kernel_client, kernel ) ) )

# def run_qtconsole():
# 	app = QtWidgets.QApplication([])
# 	widget = IPythonWidget( None )
# 	widget.show()
# 	app.exec_()
	

# cpython_entry.addActionListener( ActionListener( lambda e : run_qtconsole() ) )

while( True ):
	time.sleep( 0.5 )



