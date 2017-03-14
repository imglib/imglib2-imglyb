package net.imglib.python;

import java.util.concurrent.ExecutionException;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.morphology.distance.DistanceTransform;
import net.imglib2.algorithm.morphology.distance.DistanceTransform.DISTANCE_TYPE;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypelongaccess.IntLongAccess;
import net.imglib2.img.basictypelongaccess.unsafe.FloatUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.IntUnsafe;
import net.imglib2.img.unsafe.UnsafeImg;
import net.imglib2.img.unsafe.UnsafeImgs;
import net.imglib2.type.numeric.ARGBLongAccessType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.real.FloatLongAccessType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;

public class PythonFunctions
{

	public static RandomAccessibleInterval< ARGBType > toARGB( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final IntUnsafe access = new IntUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< ARGBType, IntUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final ARGBType linkedType = new ARGBType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< ARGBLongAccessType, IntLongAccess > img = UnsafeImgs.argbs( access, dim );
			final Converter< ARGBLongAccessType, ARGBType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( (RandomAccessibleInterval< ARGBLongAccessType >) img, converter, new ARGBType() );
		}
	}

	public static RandomAccessibleInterval< FloatType > toFloat( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final FloatUnsafe access = new FloatUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< FloatType, FloatUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final FloatType linkedType = new FloatType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< FloatLongAccessType, FloatUnsafe > img = UnsafeImgs.floats( access, dim );
			final Converter< FloatLongAccessType, FloatType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( ( RandomAccessibleInterval< FloatLongAccessType > ) img, converter, new FloatType() );
		}
	}

	public static void distanceTransform( final RandomAccessibleInterval< FloatType > input, final double... sigma )
	{
		try
		{
			DistanceTransform.transform( input, DISTANCE_TYPE.EUCLIDIAN, Runtime.getRuntime().availableProcessors(), sigma );
		}
		catch ( InterruptedException | ExecutionException e )
		{
			e.printStackTrace();
		}
	}

}
