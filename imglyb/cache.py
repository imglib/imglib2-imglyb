import imglyb
from imglyb import util, types
from jnius import autoclass, cast, PythonJavaClass, java_method

import ctypes
import numpy as np
import time

ArrayImgs = autoclass( 'net.imglib2.img.array.ArrayImgs' )
Intervals = autoclass( 'net.imglib2.util.Intervals' )
UnsafeUtil = autoclass( 'net.imglib2.img.basictypelongaccess.unsafe.UnsafeUtil' )
Arrays = autoclass( 'java.util.Arrays' )
OwningFloatUnsafe = autoclass( 'net.imglib2.img.basictypelongaccess.unsafe.owning.OwningFloatUnsafe' )
Fraction = autoclass( 'net.imglib2.util.Fraction' )
LongStream = autoclass('java.util.stream.LongStream')
ReshapeView = autoclass( 'net.imglib2.python.view.reshape.ReshapeView' )
FinalInterval = autoclass( 'net.imglib2.FinalInterval' )

CellGrid = autoclass( 'net.imglib2.img.cell.CellGrid' )
DirtyDiskCellCache = autoclass('net.imglib2.cache.img.DirtyDiskCellCache')
DiskCellCache = autoclass('net.imglib2.cache.img.DiskCellCache')
CheckerboardLoader = autoclass('net.imglib2.cache.CheckerboardLoader')
IoSync = autoclass('net.imglib2.cache.IoSync')
GuardedStrongRefLoaderRemoverCache = autoclass('net.imglib2.cache.ref.GuardedStrongRefLoaderRemoverCache')
LazyCellImg = autoclass('net.imglib2.img.cell.LazyCellImg')
DisplayMode = autoclass('bdv.viewer.DisplayMode')
BlockingFetchQueues = autoclass('net.imglib2.cache.queue.BlockingFetchQueues')
FetcherThreads = autoclass('net.imglib2.cache.queue.FetcherThreads')
AccessIo = autoclass('net.imglib2.cache.img.AccessIo')
PrimitiveType = autoclass('net.imglib2.cache.img.PrimitiveType')
AccessFlags = autoclass('net.imglib2.cache.img.AccessFlags')
ProcessingLoader = autoclass('net.imglib2.cache.ProcessingLoader')
VolatileOwningFloatUnsafe = autoclass('net.imglib2.cache.VolatileOwningFloatUnsafe')
ReferenceGuardingFloatUnsafe = autoclass('net.imglib2.cache.ReferenceGuardingFloatUnsafe')
CreateInvalidVolatileCell = autoclass('bdv.img.cache.CreateInvalidVolatileCell')
WeakRefVolatileCache = autoclass('net.imglib2.cache.ref.WeakRefVolatileCache')
CacheHints = autoclass('net.imglib2.cache.volatiles.CacheHints')
LoadingStrategy = autoclass('net.imglib2.cache.volatiles.LoadingStrategy')
VolatileCachedCellImg = autoclass('bdv.img.cache.VolatileCachedCellImg')


class LambdaProcessor( PythonJavaClass ):
	__javainterfaces__ = ['net.imglib2.cache.ProcessingLoader$Processor']

	def __init__( self, functor ):
		super( LambdaProcessor, self ).__init__()
		self.functor = functor
	
	@java_method( '(Lnet/imglib2/Interval;)Ljava/lang/Object;' )# Lnet/imglib2/cache/ReferenceGuardingFloatUnsafe;' )
	def process( self, interval ):
		# print( "interval?" )
		return self.functor( interval )

class FloatUnsafeReference( PythonJavaClass ):
	__javainterfaces__ = ['net.imglib2.cache.ReferenceGuardingFloatUnsafe$ReferenceHolder']

	def __init__( self, ref ):
		super( FloatUnsafeReference, sself ).__init__()
		self.ref = ref

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

		get_address = getattr( access, 'getAddress', None )
		if callable( get_address ):
			return get_address()
		
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
	

	
def fill_with_random( interval ):#, make_store, make_img ):
	n = Intervals.numElements( interval )
	store = VolatileOwningFloatUnsafe( n, True )# make_store( n, True )
	dims = Intervals.dimensionsAsLongArray( interval )
	store_cast = store # cast( 'net.imglib2.img.basictypeaccess.FloatAccess', store )
	img = ArrayImgs.floats( store_cast, *dims ) #make_img( store, )
	np_img = ImgLibReferenceGuard( img )
	np_img[...] = np.random.randint( 2**16, size=np_img.shape )
	print( "Correct?", np_img.shape, np_img.dtype, np_img.min(), np_img.max(), store_cast.getValue( 0 ), store_cast.getValue( 1 ), store_cast.getValue( 2 ) )
	return store_cast

def create_caches( ttype, vtype, name, grid, loader, primitive_type, access_flag ):
	blockcache = DiskCellCache.createTempDirectory( name, True )
	diskcache = DirtyDiskCellCache(
		blockcache,
		grid,
		loader,
		AccessIo.get( primitive_type, access_flag ),
		ttype.getEntitiesPerPixel() );
	iosync = IoSync( diskcache );
	cache = GuardedStrongRefLoaderRemoverCache( 1000 ) \
	  .withRemover( iosync ) \
	  .withLoader( iosync )

	return cache

def create_img_and_volatile_image( ttype, vtype, grid, cache, queue ):
	createInvalid = CreateInvalidVolatileCell.get( grid, ttype )
	volatileCache = WeakRefVolatileCache( cache, queue, createInvalid )
	hints = CacheHints( LoadingStrategy.VOLATILE, 0, False )
	img = LazyCellImg( grid, cast( 'net.imglib2.type.NativeType', ttype.copy() ), util.Helpers.getFromUncheckedCache( cache.unchecked() ) )
	get_get = util.Helpers.getFromUncheckedVolatileCache( volatileCache.unchecked() )
	print( "get_get", get_get )
	v_img = VolatileCachedCellImg( grid, vtype, hints, get_get )
	return img, v_img


	


if __name__ == "__main__":
	cellDimensions = ( 64, 64, 64 )
	dimensions = ( 640, 640, 640 )
	grid = CellGrid( dimensions, cellDimensions )
	ttype = types.FloatType()
	vtype = types.VolatileFloatType()

	cboard_loader = CheckerboardLoader( grid )
	cboard_cache = create_caches( ttype.copy(), vtype.copy(), "Checkerboard-", grid, cboard_loader, PrimitiveType.FLOAT, AccessFlags.DIRTY )

	img = LazyCellImg( grid, cast( 'net.imglib2.type.NativeType', ttype.copy() ), util.Helpers.getFromUncheckedCache( cboard_cache.unchecked() ) )

	proc = LambdaProcessor( lambda interval : fill_with_random( interval ) )
	# interval = FinalInterval.createMinMax( 0, 0 )
	# res = proc.process( interval )
	# print( res.getValue( 0 ) )
	processing_loader = ProcessingLoader( proc, grid )

	maxNumLevels = 1;
	numFetcherThreads = 7;
	queue = BlockingFetchQueues( maxNumLevels );
	FetcherThreads( queue, numFetcherThreads );
	
	proc_cache = create_caches( ttype.copy(), vtype.copy(), "processed-", grid, processing_loader, PrimitiveType.FLOAT, AccessFlags.VOLATILE )
	proc_img, proc_v_img = create_img_and_volatile_image( ttype.copy(), vtype.copy(), grid, proc_cache, queue )
	
	

	bdv = util.BdvFunctions.show( img, "Cached" );
	# bdv.getBdvHandle().getViewerPanel().setDisplayMode( DisplayMode.SINGLE );
	util.BdvFunctions.show( proc_img, "processed", util.BdvOptions.options().addTo( bdv ) )

	
	
	

	# Keep Python running until user closes Bdv window
	vp = bdv.getBdvHandle().getViewerPanel()
	check = autoclass( 'net.imglib2.python.BdvWindowClosedCheck' )()
	frame = cast( 'javax.swing.JFrame', autoclass( 'javax.swing.SwingUtilities' ).getWindowAncestor( vp ) )
	frame.addWindowListener( check )
	while check.isOpen():
		time.sleep( 0.1 )


	# final Pair< Img< UnsignedShortType >, Img< VolatileUnsignedShortType > > gauss1 = createGauss( Views.extendBorder( img ), 5, grid, queue );
	# final Pair< Img< UnsignedShortType >, Img< VolatileUnsignedShortType > > gauss2 = createGauss( Views.extendBorder( img ), 4, grid, queue );

	# BdvFunctions.show( gauss1.getB(), "Gauss 1", BdvOptions.options().addTo( bdv ) );
	# BdvFunctions.show( gauss2.getB(), "Gauss 2", BdvOptions.options().addTo( bdv ) );

	# final Pair< Img< ShortType >, Img< VolatileShortType > > diff = createDifference(
	# 	Views.extendBorder( gauss1.getA() ),
	# 	Views.extendBorder( gauss2.getA() ),
	# 	grid,
	# 	queue );
	# BdvFunctions.show( diff.getB(), "Diff", BdvOptions.options().addTo( bdv ) );
