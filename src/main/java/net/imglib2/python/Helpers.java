/*-
 * #%L
 * Utility and helper methods to facilitate python imglib2 interaction with shared memory.
 * %%
 * Copyright (C) 2017 - 2020 Howard Hughes Medical Institute.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.python;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.function.LongFunction;

import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

import bdv.img.cache.CreateInvalidVolatileCell;
import bdv.img.cache.VolatileCachedCellImg;
import bdv.img.cache.VolatileCachedCellImg.Get;
import bdv.util.volatiles.SharedQueue;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.cache.Cache;
import net.imglib2.cache.LoaderCache;
import net.imglib2.cache.img.CachedCellImg;
import net.imglib2.cache.img.CellLoader;
import net.imglib2.cache.img.LoadedCellCacheLoader;
import net.imglib2.cache.img.SingleCellArrayImg;
import net.imglib2.cache.ref.SoftRefLoaderCache;
import net.imglib2.cache.ref.WeakRefVolatileCache;
import net.imglib2.cache.volatiles.CacheHints;
import net.imglib2.cache.volatiles.CreateInvalid;
import net.imglib2.cache.volatiles.LoadingStrategy;
import net.imglib2.cache.volatiles.UncheckedVolatileCache;
import net.imglib2.cache.volatiles.VolatileCache;
import net.imglib2.img.NativeImg;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.AccessFlags;
import net.imglib2.img.basictypeaccess.Accesses;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.volatiles.VolatileArrayDataAccess;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileByteArray;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningFloatUnsafe;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.type.NativeType;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Fraction;
import net.imglib2.util.IntervalIndexer;
import net.imglib2.util.Intervals;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;

public class Helpers
{

	public static Behaviours behaviours()
	{
		return behaviours( new InputTriggerConfig() );
	}

	public static Behaviours behaviours( final InputTriggerConfig conf, final String... args )
	{
		return new Behaviours( conf, args );
	}

	public static String className( final Object o )
	{
		return o.getClass().getName().toString();
	}

	public static String classNameSimple( final Object o )
	{
		return o.getClass().getSimpleName().toString();
	}

	public static ArrayImg< FloatType, OwningFloatUnsafe > toArrayImg( final OwningFloatUnsafe store, final long[] dim )
	{
		final ArrayImg< FloatType, OwningFloatUnsafe > img = new ArrayImg<>( store, dim, new Fraction() );
		final FloatType linkedType = new FloatType( img );
		img.setLinkedType( linkedType );
		return img;
	}

	public static < T extends Type< T > > void burnIn( final RandomAccessible< T > source, final RandomAccessibleInterval< T > target )
	{
		for ( final Pair< T, T > p : Views.interval( Views.pair( source, target ), target ) )
		{
			p.getB().set( p.getA() );
		}
	}

	public static < T extends Type< T > & Comparable< T > > T min( final RandomAccessibleInterval< T > source, final T min )
	{
		for ( final T s : Views.flatIterable( source ) )
		{
			if ( s.compareTo( min ) < 0 )
			{
				min.set( s );
			}
		}
		return min;
	}

	public static < T extends Type< T > & Comparable< T > > T max( final RandomAccessibleInterval< T > source, final T max )
	{
		for ( final T s : Views.flatIterable( source ) )
		{
			if ( s.compareTo( max ) > 0 )
			{
				max.set( s );
			}
		}
		return max;
	}

	public static CellGrid makeGrid( final long[] dims, final int[] cellSize )
	{
		final CellGrid grid = new CellGrid( dims, cellSize );
		return grid;
	}

	public static long[] cellMin( final CellGrid grid, final long index )
	{
		final long[] min = new long[ grid.numDimensions() ];
		grid.getCellGridPositionFlat( index, min );
		Arrays.setAll( min, d -> min[ d ] * grid.cellDimension( d ) );
		return min;
	}

	private static class GetFromUncheckedCache< T > implements Get< T >
	{

		private final UncheckedVolatileCache< Long, T > delegate;

		public GetFromUncheckedCache( final UncheckedVolatileCache< Long, T > delegate )
		{
			super();
			this.delegate = delegate;
		}

		@Override
		public T get( final long index, final CacheHints cacheHints )
		{
			return delegate.get( index, cacheHints );
		}

	}

	public static < T extends NativeType< T >, A extends VolatileArrayDataAccess< A > > Object createVolatileCachedCellImg(
			final CachedCellImg< T, A > img,
			final boolean dirty )
	{
		final CellGrid grid = img.getCellGrid();
		final T type = img.createLinkedType().createVariable();
		final Cache< Long, Cell< A > > cache = img.getCache();
		final SharedQueue queue = new SharedQueue( 1, 1 );
		final CacheHints hints = new CacheHints( LoadingStrategy.DONTLOAD, 0, false );
		final CreateInvalid< Long, Cell< A > > createInvalid = CreateInvalidVolatileCell.get( grid, type, dirty );
		final VolatileCache< Long, Cell< A > > volatileCache = new WeakRefVolatileCache<>( cache, queue, createInvalid );
		final UncheckedVolatileCache< Long, Cell< A > > unchecked = volatileCache.unchecked();
		final Get< Cell< A > > get = new GetFromUncheckedCache<>( unchecked );
		final VolatileCachedCellImg< T, A > volatileImg = new VolatileCachedCellImg<>( grid, type, hints, get );
//		img.randomAccess();
		return volatileImg;
	}

	public static VolatileByteArray randomByteArray( final int numEntities, final boolean isValid, final Random rng )
	{
		final VolatileByteArray vba = new VolatileByteArray( numEntities, isValid );
		rng.nextBytes( vba.getCurrentStorageArray() );
		return vba;
	}

	public static < A > A getFromCache( final Cache< Long, A > cache, final long index ) throws ExecutionException
	{
		return cache.get( index );
	}

	public static < T extends NativeType< T >, A > CachedCellImg< T, A > imgFromFunc(
			final long[] dims,
			final int[] blockSize,
			final LongFunction< A > makeAccess,
			final T t,
			final A a,
			final LoaderCache< Long, Cell< A > > cache )
	{
		final CellGrid cellGrid = new CellGrid( dims, blockSize );
		final CacheLoaderFromFunction< Long, Cell< A > > loader = new CacheLoaderFromFunction<>( key -> {
			final long index = key;
			final long[] min = new long[ cellGrid.numDimensions() ];
			final long[] cellGridPosition = new long[ min.length ];
			final int[] size = new int[ min.length ];
			cellGrid.getCellGridPositionFlat( index, cellGridPosition );
			Arrays.setAll( min, d -> cellGrid.getCellMin( d, cellGridPosition[ d ] ) );
			cellGrid.getCellDimensions( index, min, size );
			final A access = makeAccess.apply( key );
			return new Cell<>( size, min, access );
		} );

		final CachedCellImg< T, A > img = new CachedCellImg<>(
				cellGrid,
				t.getEntitiesPerPixel(),
				cache.withLoader( loader ),
				a );
		img.setLinkedType( ( T ) t.getNativeTypeFactory().createLinkedType( ( NativeImg ) img ) );
		return img;
	}

	public static CachedCellImg< UnsignedByteType, VolatileByteArray > randomBytes( final long[] dims, final int[] blockSize )
	{
		final CellGrid cellGrid = new CellGrid( dims, blockSize );
		final CacheLoaderFromFunction< Long, Cell< VolatileByteArray > > loader = new CacheLoaderFromFunction<>( key -> {
			final long index = key;
			final long[] min = new long[ cellGrid.numDimensions() ];
			final long[] cellGridPosition = new long[ min.length ];
			final int[] size = new int[ min.length ];
			cellGrid.getCellGridPositionFlat( index, cellGridPosition );
			Arrays.setAll( min, d -> cellGrid.getCellMin( d, cellGridPosition[ d ] ) );
			cellGrid.getCellDimensions( index, min, size );

			final VolatileByteArray vba = new VolatileByteArray( ( int ) Intervals.numElements( size ), true );
			final Random rng = new Random( index );
			rng.nextBytes( vba.getCurrentStorageArray() );
			return new Cell<>( size, min, vba );
		} );

		final Cache< Long, Cell< VolatileByteArray > > cache = new SoftRefLoaderCache< Long, Cell< VolatileByteArray > >().withLoader( loader );
		final CachedCellImg< UnsignedByteType, VolatileByteArray > img = new CachedCellImg<>( cellGrid, new UnsignedByteType().getEntitiesPerPixel(), cache, new VolatileByteArray( 0, true ) );
		img.setLinkedType( new UnsignedByteType( img ) );
		return img;

	}

	private static class CellLoaderFromFunction< T extends NativeType< T >, A > implements CellLoader< T >
	{

		private final CellGrid grid;

		private final LongFunction< A > makeAccess;

		private CellLoaderFromFunction( final CellGrid grid, final LongFunction< A > makeAccess )
		{
			this.grid = grid;
			this.makeAccess = makeAccess;
		}

		@Override
		public void load( final SingleCellArrayImg< T, ? > cell ) throws Exception
		{
			final long[] min = Intervals.minAsLongArray( cell );
			final long[] pos = new long[ min.length ];
			Arrays.setAll( pos, d -> min[ d ] / grid.cellDimension( d ) );
			final long linearIndex = IntervalIndexer.positionToIndex( pos, grid.getGridDimensions() );
			final int size = ( int ) Intervals.numElements( cell );
			final A access = makeAccess.apply( linearIndex );
			final Object target = cell.update( null );
			Accesses.copyAny( access, 0, target, 0, size );
		}
	}

	public static < T extends NativeType< T >, A extends ArrayDataAccess< A > > CachedCellImg< T, A > imgWithCellLoaderFromFunc(
			final long[] dims,
			final int[] blockSize,
			final LongFunction< A > makeAccess,
			final T t,
			final A a,
			final LoaderCache< Long, Cell< A > > cache )
	{
		final CellGrid cellGrid = new CellGrid( dims, blockSize );
		final CellLoaderFromFunction< T, A > cellLoader = new CellLoaderFromFunction<>( cellGrid, makeAccess );
		final LoadedCellCacheLoader< T, A > loader = LoadedCellCacheLoader.get(
				cellGrid,
				cellLoader,
				t,
				AccessFlags.ofAccess( a ) );

		final CachedCellImg< T, A > img = new CachedCellImg<>( cellGrid, t.getEntitiesPerPixel(), cache.withLoader( loader ), a );
		img.setLinkedType( ( T ) t.getNativeTypeFactory().createLinkedType( ( NativeImg ) img ) );
		return img;
	}

}
