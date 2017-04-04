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


class Bunch( object ):
	def __init__( self, **kwds ):
		self.__dict__.update( kwds )

dtype_selector = {
	'FloatType' : np.dtype( 'float32' )
}

getters = {
	'FloatType' : util.Getters.getFloat
}

ctype_conversions_imglib = {
	'FloatType' : ctypes.c_float
}
	

def get_address( rai ):
	class_name = util.Helpers.classNameSimple( rai )
	class_name_full = util.Helpers.className( rai )
	if  class_name in ( 'ArrayImg', 'UnsafeImg' ):
		access = cast( class_name_full, rai ).update( None )
		access_type = util.Helpers.className( access )
		
		if 'basictypeaccess.array' in access_type:
			print( "ARRAY? ", Arrays.toString( cast( access_type, access ).getCurrentStorageArray() ) )
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
		print( 'address:', address, type(address), np.prod(shape), type(int(np.prod(shape))))
		pointer = ctypes.cast( address, ctypes.POINTER( ctype_conversions_imglib[ imglib_type ] ) )
		print( "Contents? ", pointer.contents, UnsafeUtil.UNSAFE.getFloat( address ) )
		order = 'C'
		
		obj = np.ctypeslib.as_array( pointer, shape=shape )
		obj.setflags( write = True )
		print( "ADDRESS MISMATCH? ", address, obj.ctypes.data )
		return obj

	def __array_finalize__( self, obj ):
		if obj is None:
			return
		self.rai = obj.rai
		
		
class ImgLibNumpyArray( np.ndarray ):

	def __new__( cls, rai ):

		class_name = util.Helper.className( rai )
		
		if 'ReferenceGuardingRandomAccessibleInterval' in class_name:
			return cast( class_name, rai ).getReferenceHolder().args[ 0 ]

		elif 'ArrayImg' in class_name:
			pass

		elif 'UnsafeImg' in class_name:
			pass
			
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

		else:
			nd.array.__array_finalize__( self, obj )

	def __getitem__( self, obj ):
		slicing = [ slice( None ) for d in range( self.rai.numDimensions() ) ] 
		print( obj, slicing )

		if isinstance( obj, ( tuple, list ) ):
			obj = obj[ :self.rai.numDimensions() ]
			if np.all( [ isinstance( x, ( int, slice ) ) or x is Ellipsis for x in obj ] ):
				ellipsis_position = np.where( [ x is Ellipsis for x in obj ] )[ 0 ]
				ellipsis_position = len( slicing ) if ellipsis_position.size == 0 else ellipsis_position[ 0 ]
				print ( 'ell pos', ellipsis_position )

				for idx in range( 0, ellipsis_position ):
					slicing[ idx ] = obj[ idx ]

				for idx in range( 1, len( obj ) - ellipsis_position ):
					print( 'idx', idx)
					slicing[ len( slicing ) - idx ] = obj[ len( obj ) - idx ]

				print( "SLICING: ", slicing )
				

			else:
				raise NotImplementedError( "__getitem__ not yet implemented for advanced indexing! Use int, slice, and Ellipsis only!" )
		
		elif isinstance( obj, int ):
			slicing[ 0 ] = shape[0] - obj if obj < 0 else obj

		else:
			raise ValueError( "Illegal argument for slicing: {}. Use int, slice, and Ellipsis only!".format( obj ) )

		reverse_slicing = [
			x if isinstance( x, int ) else slice( x.start if x.start else 0, x.stop if x.stop else rai.dimension( d ), x.step if x.step else 1 ) for d, x in enumerate( slicing[::-1] )
			]

		# print( obj )
		# print( reverse_slicing )
		# print( [ isinstance( x, int ) for x in reverse_slicing ] )

		if ( np.all( [ isinstance( x, int ) for x in reverse_slicing ] ) ):
			access = rai.randomAccess()
			access.setPosition( slicing )
			val = access.get()
			return getters[ self.imglib_type ]( val )
			
		
		return None
		


if __name__ == "__main__":
	blub = ArrayImgs.floats( 1, 2, 3, 4 )
	shape = ( 1, 2, 3, 4 )
	n_elements = int( np.prod( shape ) )
	data_store = OwningFloatUnsafe( n_elements )
	dim_array = LongStream.of( *shape ).toArray()
	print( Arrays.toString( dim_array ) )
	rai = util.Helpers.toArrayImg( cast( util.Helpers.className( data_store ), data_store ), dim_array )
	c = rai.cursor()
	count = 23
	while c.hasNext():
		c.next().setReal( count )
		count += 1
	# rai.cursor().next().setReal( 3.0 )
	print( util.Helpers.className( rai.randomAccess().get() ) )
	print( util.Helpers.classNameSimple( rai.randomAccess().get() ) )
	arr = ImgLibReferenceGuard( rai )
	print ( 'sum', np.sum( arr ) )
	# rai.cursor().next().set( 1.0 )
	arr[0,0,0,0] = 1
	print ( 'sum', np.sum( arr ) )
	c = rai.cursor()
	while c.hasNext():
		print (c.next().get())
	print( "ADDRESS MISMATCH? ", arr.ctypes.data )
	# arr = ImgLibNumpyArray( rai )
	# # print( arr.shape, arr.dtype, arr.rai )
	# b = arr[  0, 1, 2, 3 ]
	# print( b )
	
