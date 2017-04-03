package net.imglib2.python;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypelongaccess.ByteLongAccess;
import net.imglib2.img.basictypelongaccess.DoubleLongAccess;
import net.imglib2.img.basictypelongaccess.FloatLongAccess;
import net.imglib2.img.basictypelongaccess.IntLongAccess;
import net.imglib2.img.basictypelongaccess.LongLongAccess;
import net.imglib2.img.basictypelongaccess.ShortLongAccess;
import net.imglib2.img.basictypelongaccess.unsafe.ByteUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.DoubleUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.FloatUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.IntUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.LongUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.ShortUnsafe;
import net.imglib2.img.unsafe.UnsafeImg;
import net.imglib2.img.unsafe.UnsafeImgs;
import net.imglib2.type.numeric.ARGBLongAccessType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.complex.ComplexDoubleLongAccessType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatLongAccessType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.ByteLongAccessType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntLongAccessType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongLongAccessType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortLongAccessType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteLongAccessType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntLongAccessType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedLongLongAccessType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.integer.UnsignedShortLongAccessType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleLongAccessType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatLongAccessType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;

public class NumpyToImgLibConversions
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

	public static RandomAccessibleInterval< ComplexFloatType > toComplexFloat( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final FloatUnsafe access = new FloatUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< ComplexFloatType, FloatUnsafe > img = new ArrayImg<>( access, dim, new Fraction( 2, 1 ) );
			final ComplexFloatType linkedType = new ComplexFloatType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< ComplexFloatLongAccessType, FloatLongAccess > img = UnsafeImgs.complexFloats( access, dim );
			final Converter< ComplexFloatLongAccessType, ComplexFloatType > converter = ( s, t ) -> {
				t.set( s.getRealFloat(), s.getImaginaryFloat() );
			};
			return Converters.convert( ( RandomAccessibleInterval< ComplexFloatLongAccessType > ) img, converter, new ComplexFloatType() );
		}
	}

	public static RandomAccessibleInterval< ComplexDoubleType > toComplexDouble( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final DoubleUnsafe access = new DoubleUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< ComplexDoubleType, DoubleUnsafe > img = new ArrayImg<>( access, dim, new Fraction( 2, 1 ) );
			final ComplexDoubleType linkedType = new ComplexDoubleType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< ComplexDoubleLongAccessType, DoubleLongAccess > img = UnsafeImgs.complexDoubles( access, dim );
			final Converter< ComplexDoubleLongAccessType, ComplexDoubleType > converter = ( s, t ) -> {
				t.set( s.getRealDouble(), s.getImaginaryDouble() );
			};
			return Converters.convert( ( RandomAccessibleInterval< ComplexDoubleLongAccessType > ) img, converter, new ComplexDoubleType() );
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

	public static RandomAccessibleInterval< DoubleType > toDouble( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final DoubleUnsafe access = new DoubleUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< DoubleType, DoubleUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final DoubleType linkedType = new DoubleType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< DoubleLongAccessType, DoubleUnsafe > img = UnsafeImgs.doubles( access, dim );
			final Converter< DoubleLongAccessType, DoubleType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( ( RandomAccessibleInterval< DoubleLongAccessType > ) img, converter, new DoubleType() );
		}
	}

	public static RandomAccessibleInterval< ByteType > toByte( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final ByteUnsafe access = new ByteUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< ByteType, ByteUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final ByteType linkedType = new ByteType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< ByteLongAccessType, ByteLongAccess > img = UnsafeImgs.bytes( access, dim );
			final Converter< ByteLongAccessType, ByteType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( ( RandomAccessibleInterval< ByteLongAccessType > ) img, converter, new ByteType() );
		}
	}

	public static RandomAccessibleInterval< ShortType > toShort( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final ShortUnsafe access = new ShortUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< ShortType, ShortUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final ShortType linkedType = new ShortType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< ShortLongAccessType, ShortLongAccess > img = UnsafeImgs.shorts( access, dim );
			final Converter< ShortLongAccessType, ShortType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( ( RandomAccessibleInterval< ShortLongAccessType > ) img, converter, new ShortType() );
		}
	}

	public static RandomAccessibleInterval< IntType > toInt( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final IntUnsafe access = new IntUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< IntType, IntUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final IntType linkedType = new IntType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< IntLongAccessType, IntLongAccess > img = UnsafeImgs.ints( access, dim );
			final Converter< IntLongAccessType, IntType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( ( RandomAccessibleInterval< IntLongAccessType > ) img, converter, new IntType() );
		}
	}

	public static RandomAccessibleInterval< LongType > toLong( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final LongUnsafe access = new LongUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< LongType, LongUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final LongType linkedType = new LongType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< LongLongAccessType, LongLongAccess > img = UnsafeImgs.longs( access, dim );
			final Converter< LongLongAccessType, LongType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( ( RandomAccessibleInterval< LongLongAccessType > ) img, converter, new LongType() );
		}
	}

	public static RandomAccessibleInterval< UnsignedByteType > toUnsignedByte( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final ByteUnsafe access = new ByteUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< UnsignedByteType, ByteUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final UnsignedByteType linkedType = new UnsignedByteType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< UnsignedByteLongAccessType, ByteLongAccess > img = UnsafeImgs.unsignedBytes( access, dim );
			final Converter< UnsignedByteLongAccessType, UnsignedByteType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( ( RandomAccessibleInterval< UnsignedByteLongAccessType > ) img, converter, new UnsignedByteType() );
		}
	}

	public static RandomAccessibleInterval< UnsignedShortType > toUnsignedShort( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final ShortUnsafe access = new ShortUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< UnsignedShortType, ShortUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final UnsignedShortType linkedType = new UnsignedShortType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< UnsignedShortLongAccessType, ShortLongAccess > img = UnsafeImgs.unsignedShorts( access, dim );
			final Converter< UnsignedShortLongAccessType, UnsignedShortType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( ( RandomAccessibleInterval< UnsignedShortLongAccessType > ) img, converter, new UnsignedShortType() );
		}
	}

	public static RandomAccessibleInterval< UnsignedIntType > toUnsignedInt( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final IntUnsafe access = new IntUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< UnsignedIntType, IntUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final UnsignedIntType linkedType = new UnsignedIntType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< UnsignedIntLongAccessType, IntLongAccess > img = UnsafeImgs.unsignedInts( access, dim );
			final Converter< UnsignedIntLongAccessType, UnsignedIntType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( ( RandomAccessibleInterval< UnsignedIntLongAccessType > ) img, converter, new UnsignedIntType() );
		}
	}

	public static RandomAccessibleInterval< UnsignedLongType > toUnsignedLong( final long address, final long... dim )
	{
		final long numElements = Intervals.numElements( dim );
		final LongUnsafe access = new LongUnsafe( address );

		if ( numElements < Integer.MAX_VALUE )
		{
			final ArrayImg< UnsignedLongType, LongUnsafe > img = new ArrayImg<>( access, dim, new Fraction() );
			final UnsignedLongType linkedType = new UnsignedLongType( img );
			img.setLinkedType( linkedType );
			return img;
		}
		else
		{
			final UnsafeImg< UnsignedLongLongAccessType, LongLongAccess > img = UnsafeImgs.unsignedLongs( access, dim );
			final Converter< UnsignedLongLongAccessType, UnsignedLongType > converter = ( s, t ) -> {
				t.set( s.get() );
			};
			return Converters.convert( ( RandomAccessibleInterval< UnsignedLongLongAccessType > ) img, converter, new UnsignedLongType() );
		}
	}

}
