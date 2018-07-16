package net.imglib2.python;

import java.util.Arrays;

import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningFloatUnsafe;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Fraction;
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

}
