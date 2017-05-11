from __future__ import print_function

__all__ = ( 'to_imglib', 'to_imglib_argb', 'to_numpy' )

def _init_jvm_options():

	import jnius_config
	
	import os

	PYJNIUS_JAR_STR = 'PYJNIUS_JAR'
	IMGLYB_JAR_STR = 'IMGLYB_JAR'

	if PYJNIUS_JAR_STR not in globals():
		try:
			PYJNIUS_JAR=os.environ[ PYJNIUS_JAR_STR ]
		except KeyError as e:
			print( "Path to pyjnius.jar not defined! Use environment variable {} to define it.".format( PYJNIUS_JAR_STR ) )
			raise e

	if IMGLYB_JAR_STR not in globals():
		try:
			IMGLYB_JAR=os.environ[ IMGLYB_JAR_STR ]
		except KeyError as e:
			print( "Path to imglib2-imglyb jar not defined! Use environment variable {} to define it.".format( IMGLYB_JAR_STR ) )
			raise e


	if 'classpath' in globals():
		jnius_config.add_classpath( classpath )

	CLASSPATH_STR='CLASSPATH'
	if CLASSPATH_STR in os.environ:
		for path in os.environ[ CLASSPATH_STR ].split( jnius_config.split_char ):
			jnius_config.add_classpath( path )

	jnius_config.add_classpath( PYJNIUS_JAR )
	jnius_config.add_classpath( IMGLYB_JAR )

	JVM_OPTIONS_STR = 'JVM_OPTIONS'

	if JVM_OPTIONS_STR in os.environ:
		jnius_config.add_options( *os.environ[ JVM_OPTIONS_STR ].split(' ') )

	return jnius_config

config = _init_jvm_options()


from .util import \
     to_imglib, \
     to_imglib_argb, \
     to_numpy
