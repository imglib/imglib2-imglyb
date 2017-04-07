package net.imglib2.python;

import org.scijava.ui.behaviour.io.InputTriggerConfig;
import org.scijava.ui.behaviour.util.Behaviours;

import bdv.img.cache.VolatileCachedCellImg;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.cache.UncheckedCache;
import net.imglib2.cache.volatiles.UncheckedVolatileCache;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.volatiles.array.VolatileFloatArray;
import net.imglib2.img.basictypelongaccess.unsafe.UnsafeUtil;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningFloatUnsafe;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.LazyCellImg;
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

	public static long getFirstArrayElementAddress( final Object o )
	{
		return UnsafeUtil.getFirstArrayElementAddress( o );
	}

	public static ArrayImg< FloatType, OwningFloatUnsafe > toArrayImg( final OwningFloatUnsafe store, final long[] dim )
	{
		final ArrayImg< FloatType, OwningFloatUnsafe > img = new ArrayImg<>( store, dim, new Fraction() );
		final FloatType linkedType = new FloatType( img );
		img.setLinkedType( linkedType );
		return img;
	}

	public static LazyCellImg.Get< Cell< VolatileFloatArray > > getFromUncheckedCache( final UncheckedCache< Long, Cell< VolatileFloatArray > > cache )
	{
		return index -> cache.get( index );
	}

	public static < A > VolatileCachedCellImg.Get< A > getFromUncheckedVolatileCache( final UncheckedVolatileCache< Long, A > cache )
	{
		return ( index, hints ) -> cache.get( index, hints );
	}

	public static < T extends Type< T > > void burnIn( final RandomAccessible< T > source, final RandomAccessibleInterval< T > target )
	{
		for ( final Pair< T, T > p : Views.interval( Views.pair( source, target ), target ) )
			p.getB().set( p.getA() );
	}

}
