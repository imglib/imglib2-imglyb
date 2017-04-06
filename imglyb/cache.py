import imglyb
from imglyb import util, types
from jnius import autoclass, cast

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



if __name__ == "__main__":
	cellDimensions = ( 64, 64, 64 )
	dimensions = ( 640, 640, 640 )
	ttype = types.FloatType()
	vtype = types.VolatileFloatType()

	grid = CellGrid( dimensions, cellDimensions );
	blockcache = DiskCellCache.createTempDirectory( "CellImg-", True )
	diskcache = DirtyDiskCellCache(
		blockcache,
		grid,
		CheckerboardLoader( grid ),
		AccessIo.get( PrimitiveType.FLOAT, AccessFlags.DIRTY ),
		ttype.getEntitiesPerPixel() );
	iosync = IoSync( diskcache );
	cache = GuardedStrongRefLoaderRemoverCache( 1000 ) \
	  .withRemover( iosync ) \
	  .withLoader( iosync ) \
	  .unchecked()
	img = LazyCellImg( grid, cast( 'net.imglib2.type.NativeType', types.FloatType() ), util.Helpers.getFromUncheckedCache( cache ) )

	bdv = util.BdvFunctions.show( img, "Cached" );
	bdv.getBdvHandle().getViewerPanel().setDisplayMode( DisplayMode.SINGLE );

	maxNumLevels = 1;
	numFetcherThreads = 7;
	queue = BlockingFetchQueues( maxNumLevels );
	FetcherThreads( queue, numFetcherThreads );

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
