import ctypes

from collections import defaultdict

from jnius import autoclass

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
	return source.ctypes.data_as( ctypes.c_void_p ).value	

# how to use type hints for python < 3.5?
def to_imglib( source ):
	if source.flags[ 'CARRAY' ]:
		address = _get_address( source )
		if not source.dtype in numpy_dtype_to_conversion_method:
			raise NotImplementedError( "Cannot convert dtype to ImgLib2 type yet: {}".format( source.dtype ) )
		return numpy_dtype_to_conversion_method[ source.dtype ]( address, *source.shape[::-1] )
	else:
		raise NotImplementedError( "Cannot convert ndarrays yet that are not aligned or not c-style contiguous" )


def to_imglib_argb( source ):
	if source.flags[ 'CARRAY' ]:
		address = _get_address( source )
		if not ( source.dtype == np.dtype( 'int32' ) or source.dtype == np.dtype( 'uint32' ) ):
			raise NotImplementedError( "source.dtype must be int32 or uint32" )
		return NumpyToImgLibConversions.toARGB( address, *source.shape[::-1] )
	else:
		raise NotImplementedError( "Cannot convert ndarrays yet that are not aligned or not c-style contiguous" )

def options2D():
	return BdvOptions.options().is2D()
