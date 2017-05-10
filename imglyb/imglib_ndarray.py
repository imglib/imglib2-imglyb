import imglyb
from imglyb import util
from jnius import autoclass, cast

import ctypes
import numpy as np

ArrayImgs = autoclass( 'net.imglib2.img.array.ArrayImgs' )
Intervals = autoclass( 'net.imglib2.util.Intervals' )
UnsafeUtil = autoclass( 'net.imglib2.img.basictypelongaccess.unsafe.UnsafeUtil' )
Arrays = autoclass( 'java.util.Arrays' )
OwningFloatUnsafe = autoclass( 'net.imglib2.img.basictypelongaccess.unsafe.owning.OwningFloatUnsafe' )
Fraction = autoclass( 'net.imglib2.util.Fraction' )
LongStream = autoclass('java.util.stream.LongStream')
ReshapeView = autoclass( 'net.imglib2.python.view.reshape.ReshapeView' )


class Bunch( object ):
	def __init__( self, **kwds ):
		self.__dict__.update( kwds )

dtype_selector = {
	'FloatType' : np.dtype( 'float32' ),
	'DoubleType' : np.dtype( 'float64' ),
	'ByteType' : np.dtype( 'int8' ),
	'UnsignedByteType' : np.dtype( 'uint8' ),
	'ShortType' : np.dtype( 'int16' ),
	'UnsignedShortType' : np.dtype( 'uint16' ),
	'IntType' : np.dtype( 'int32' ),
	'UnsignedIntType' : np.dtype( 'uint32' ),
	'LongType' : np.dtype( 'int64' ),
	'UnsignedLongType' : np.dtype( 'uint64' ),
}

getters = {
	'FloatType' : util.Getters.getFloat,
}

ctype_conversions_imglib = {
	'FloatType' : ctypes.c_float,
	'DoubleType' : ctypes.c_double,
	'ByteType' : ctypes.c_int8,
	'UnsignedByteType' : ctypes.c_uint8,
	'ShortType' : ctypes.c_int16,
	'UnsignedShortType' : ctypes.c_uint16,
	'IntType' : ctypes.c_int32,
	'UnsignedIntType' : ctypes.c_uint32,
	'LongType' : ctypes.c_int64,
	'UnsignedLongType' : ctypes.c_uint64
}


def get_address( rai ):
	class_name = util.Helpers.classNameSimple( rai )
	class_name_full = util.Helpers.className( rai )
	if  class_name in ( 'ArrayImg', 'UnsafeImg' ):
		access = cast( class_name_full, rai ).update( None )
		access_type = util.Helpers.className( access )

		if 'basictypeaccess.array' in access_type:
			return util.Helpers.getFirstArrayElementAddress( cast( access_type, access ).getCurrentStorageArray() )
		elif 'basictypelongaccess.unsafe' in access_type:
			return cast( access_type, access ).getAddress()

	return -1

class ImgLibReferenceGuard( np.ndarray ):

	def __new__( cls, rai ):

		access = rai.randomAccess()
		rai.min( access )
		imglib_type = util.Helpers.classNameSimple( access.get() )
		address = get_address( rai )

		shape = tuple( Intervals.dimensionsAsLongArray( rai ) )[::-1]
		dtype = dtype_selector[ imglib_type ]
		pointer = ctypes.cast( address, ctypes.POINTER( ctype_conversions_imglib[ imglib_type ] ) )
		order = 'C'

		obj = np.ndarray.__new__( cls, buffer=np.ctypeslib.as_array( pointer, shape=shape ), shape=shape, dtype=dtype )
		obj.setflags( write = True )
		obj.rai = rai
		return obj

	def __array_finalize__( self, obj ):
		if obj is None:
			return
		self.rai = obj.rai


class ImgLibNumpyArray( np.ndarray ):

	def __new__( cls, rai ):

		class_name = util.Helpers.className( rai )

		if 'ReferenceGuardingRandomAccessibleInterval' in class_name:
			return cast( class_name, rai ).getReferenceHolder().args[ 0 ]

		elif 'ArrayImg' in class_name:
			return ImgLibReferenceGuard( rai )

		elif 'UnsafeImg' in class_name:
			return ImgLibReferenceGuard( rai )

		else:
			access = rai.randomAccess()
			rai.min( access )
			imglib_type = util.Helpers.classNameSimple( access.get() )
			dtype = dtype_selector[ imglib_type ]
			shape = tuple( Intervals.dimensionsAsLongArray( rai ) )[::-1]
			# obj = Bunch( rai=rai, shape=shape, dtype=dtype )
			obj = np.ndarray.__new__( cls, shape=shape, dtype=dtype, buffer=None, offset=0, strides=None, order=None )
			setattr( obj, 'rai', rai )
			setattr( obj, 'imglib_type', imglib_type )
			return obj


	def __array_finalize__( self, obj ):
		if obj is None:
			return

		if isinstance( obj, ImgLibNumpyArray ):
			self.rai = obj.rai # getattr( obj, 'rai', None )
			self.shape = obj.shape # getattr( obj, 'shape', None )
			self.dtype = obj.dtype # getattr( obj, 'dtype', None )
			self.imglib_type = obj.imglib_type
			c = util.Views.flatIterable( self.rai ).cursor()

		else:
			nd.array.__array_finalize__( self, obj )

	def __getitem__( self, obj ):
		ret_val = self._getitem_impl( obj )
		return ret_val

	def _getitem_impl( self, obj ):
		slicing = [ slice( None ) for d in range( self.rai.numDimensions() ) ]

		if obj is Ellipsis:
			pass

		elif isinstance( obj, ( tuple, list ) ):
			obj = obj[ :self.rai.numDimensions() ]
			if np.all( [ isinstance( x, ( int, slice ) ) or x is Ellipsis for x in obj ] ):
				ellipsis_position = np.where( [ x is Ellipsis for x in obj ] )[ 0 ]
				ellipsis_position = len( slicing ) if ellipsis_position.size == 0 else ellipsis_position[ 0 ]

				for idx in range( 0, ellipsis_position ):
					current = obj[ idx ]
					if isinstance( current, int ) and current < 0:
						current = self.shape[ idx ] + current
					slicing[ idx ] = current

				for idx in range( 1, len( obj ) - ellipsis_position ):
					current = obj[ len( obj ) - idx ]
					if isinstance( current, int ) and current < 0:
						current = self.shape[ len( obj ) - idx ] + current
					slicing[ len( slicing ) - idx ] = current



			else:
				raise NotImplementedError( "__getitem__ not yet implemented for advanced indexing! Use int, slice, and Ellipsis only!" )

		elif isinstance( obj, int ):
			slicing[ 0 ] = shape[0] + obj if obj < 0 else obj

		else:
			raise ValueError( "Illegal argument for slicing: {}. Use int, slice, and Ellipsis only!".format( obj ) )

		reverse_slicing = [
			x if isinstance( x, int ) else slice( x.start if x.start else 0, x.stop if x.stop else rai.dimension( d ), x.step if x.step else 1 ) for d, x in enumerate( slicing[::-1] )
			]

		if ( np.all( [ isinstance( x, int ) for x in reverse_slicing ] ) ):
			access = self.rai.randomAccess()
			access.setPosition( slicing )
			val = access.get()
			return getters[ self.imglib_type ]( val )

		else:
			hyperslicers = [ (d, pos) for d, pos in enumerate( reverse_slicing ) if isinstance( pos, int ) ]
			intervalers = [ sl for sl in reverse_slicing if not isinstance( sl, int ) ]
			result = self.rai
			for d, pos in hyperslicers[::-1]:
				result = util.Views.hyperSlice( result, d, pos )

			interval_min = [ sl.start for sl in intervalers ] # inclusive
			interval_dim = [ sl.stop for sl in intervalers ]  # exclusive
			result = util.Views.offsetInterval( result, interval_min, interval_dim )

			return ImgLibNumpyArray( result )

	def reshape( self, shape ):
		return ImgLibNumpyArray( ReshapeView( self.rai, *shape ) )


if __name__ == "__main__":
	blub = ArrayImgs.floats( 2, 3, 4 )
	shape = ( 2, 3, 4 )
	n_elements = int( np.prod( shape ) )
	data_store = OwningFloatUnsafe( n_elements )
	dim_array = LongStream.of( *shape ).toArray()
	print( Arrays.toString( dim_array ) )
	rai = util.Helpers.toArrayImg( cast( util.Helpers.className( data_store ), data_store ), dim_array )
	# rai = ArrayImgs.floats( *shape )
	c = rai.cursor()
	count = 23
	while c.hasNext():
		c.next().setReal( count )
		count += 1
	# rai.cursor().next().setReal( 3.0 )
	print( util.Helpers.className( rai.randomAccess().get() ) )
	print( util.Helpers.classNameSimple( rai.randomAccess().get() ) )
	# arr = ImgLibReferenceGuard( rai )
	arr = ImgLibNumpyArray( util.Views.interval( rai, rai ) )
	# print( "ARR TYPE ", type( arr ), arr.shape, Intervals.dimensionsAsLongArray( arr.rai ) )
	# print( "ARR SLICED TYPE ", type( arr[0] ) )
	# print ( arr[0] )
	# for v0 in range( 1 ):
	# 	for v2 in range(2):
	# 		for v1 in range(3):
	# 			print( "OGE", arr[ v0 ][ v1, v2 ])
	# print ( 'sum', np.sum( arr ) )
	# # rai.cursor().next().set( 1.0 )
	# arr[0,0,0] = 1
	# arr[ ::1, ... ] = 0
	# print ( 'sum', np.sum( arr ) )
	# c = rai.cursor()
	# while c.hasNext():
	# 	print (c.next().get())
	# print( "ADDRESS MISMATCH? ", arr.ctypes.data, np.mean( arr ) )
	# print( type( arr ), type( arr.rai ), arr.shape, arr.strides )
	# # arr = ImgLibNumpyArray( rai )
	# # # print( arr.shape, arr.dtype )
	# # b = arr[  0, 1, 2, 3 ]
	# # print( b )

