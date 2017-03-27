import ctypes

from collections import defaultdict

from jnius import autoclass, PythonJavaClass, java_method

import numpy as np

__all__ = (
	'to_imglib',
	'to_imglib_argb',
	'options2D'
)

# java
Random = autoclass( 'java.util.Random' )

# imglib
NumpyToImgLibConversions = autoclass('net.imglib2.python.NumpyToImgLibConversions')
Views = autoclass('net.imglib2.view.Views')

# bigdataviewer
BdvFunctions = autoclass('bdv.util.BdvFunctions')
BdvOptions = autoclass('bdv.util.BdvOptions')

# Guard
ReferenceGuardingRandomAccessibleInterval = autoclass( 'net/imglib2/python/ReferenceGuardingRandomAccessibleInterval' )

numpy_dtype_to_conversion_method = {
	np.dtype( 'complex64' )  : NumpyToImgLibConversions.toComplexFloat,
	np.dtype( 'complex128' ) : NumpyToImgLibConversions.toComplexDouble,
	np.dtype( 'float32' )    : NumpyToImgLibConversions.toFloat,
	np.dtype( 'float64' )    : NumpyToImgLibConversions.toDouble,
	np.dtype( 'int8' )       : NumpyToImgLibConversions.toByte,
	np.dtype( 'int16' )      : NumpyToImgLibConversions.toShort,
	np.dtype( 'int32' )      : NumpyToImgLibConversions.toInt,
	np.dtype( 'int64' )      : NumpyToImgLibConversions.toLong,
	np.dtype( 'uint8' )      : NumpyToImgLibConversions.toUnsignedByte,
	np.dtype( 'uint16' )     : NumpyToImgLibConversions.toUnsignedShort,
	np.dtype( 'uint32' )     : NumpyToImgLibConversions.toUnsignedInt,
	np.dtype( 'uint64' )     : NumpyToImgLibConversions.toUnsignedLong
	}

def _get_address( source ):
	return source.ctypes.data

class ReferenceGuard( PythonJavaClass ):
	__javainterfaces__ = [ 'net/imglib2/python/ReferenceGuardingRandomAccessibleInterval$ReferenceHolder' ]
	def __init__( self, *args, **kwargs ):
		super( ReferenceGuard, self ).__init__()
		self.args = args

# how to use type hints for python < 3.5?
def to_imglib( source ):
	return ReferenceGuardingRandomAccessibleInterval( _to_imglib( source ), ReferenceGuard( source ) )

# how to use type hints for python < 3.5?
def _to_imglib( source ):
	if source.flags[ 'CARRAY' ]:
		address = _get_address( source )
		if not source.dtype in numpy_dtype_to_conversion_method:
			raise NotImplementedError( "Cannot convert dtype to ImgLib2 type yet: {}".format( source.dtype ) )
		return numpy_dtype_to_conversion_method[ source.dtype ]( address, *source.shape[::-1] )
	else:
		raise NotImplementedError( "Cannot convert ndarrays yet that are not aligned or not c-style contiguous" )

def to_imglib_argb( source ):
	return ReferenceGuardingRandomAccessibleInterval( _to_imglib_argb( source ), ReferenceGuard( source ) )

def _to_imglib_argb( source ):
	if source.flags[ 'CARRAY' ]:
		address = _get_address( source )
		if not ( source.dtype == np.dtype( 'int32' ) or source.dtype == np.dtype( 'uint32' ) ):
			raise NotImplementedError( "source.dtype must be int32 or uint32" )
		return NumpyToImgLibConversions.toARGB( address, *source.shape[::-1] )
	else:
		raise NotImplementedError( "Cannot convert ndarrays yet that are not aligned or not c-style contiguous" )

def options2D():
	return BdvOptions.options().is2D()


class GenericOverlayRenderer( PythonJavaClass ):
	__javainterfaces__ = ['net/imglib2/ui/OverlayRenderer']

	def __init__( self, draw_overlays=lambda g : None,  set_canvas_size=lambda w, h : None ):
		super(GenericOverlayRenderer, self).__init__()
		self.draw_overlays = draw_overlays
		self.set_canvas_size = set_canvas_size

	@java_method('(Ljava/awt/Graphics;)V')
	def drawOverlays( self, g ):
		self.draw_overlays( g )

	@java_method('(II)V')
	def setCanvasSize( self, width, height ):
		self.set_canvas_size( width, height )


class GenericMouseMotionListener( PythonJavaClass ):
	__javainterfaces__ = ['java/awt/event/MouseMotionListener']

	def __init__( self, mouse_dragged = lambda e : None, mouse_moved = lambda e : None ):
		super(GenericMouseMotionListener, self).__init__()
		self.mouse_dragged = mouse_dragged
		self.mouse_moved = mouse_moved

	@java_method('(Ljava/awt/event/MouseEvent;)V')
	def mouseDragged( self, e ):
		self.mouse_dragged( e )

	@java_method('(Ljava/awt/event/MouseEvent;)V')
	def mouseMoved( self, e ):
		self.mouse_moved( e )
