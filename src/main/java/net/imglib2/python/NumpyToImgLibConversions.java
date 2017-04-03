package net.imglib2.python;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converters;
import net.imglib2.converter.readwrite.longaccess.ARGBLongAccessTypeARGBTypeConverter;
import net.imglib2.converter.readwrite.longaccess.ByteLongAccessTypeByteTypeConverter;
import net.imglib2.converter.readwrite.longaccess.ComplexDoubleLongAccessTypeComplexDoubleTypeConverter;
import net.imglib2.converter.readwrite.longaccess.ComplexFloatLongAccessTypeComplexFloatTypeConverter;
import net.imglib2.converter.readwrite.longaccess.DoubleLongAccessTypeDoubleTypeConverter;
import net.imglib2.converter.readwrite.longaccess.FloatLongAccessTypeFloatTypeConverter;
import net.imglib2.converter.readwrite.longaccess.IntLongAccessTypeIntTypeConverter;
import net.imglib2.converter.readwrite.longaccess.LongLongAccessTypeLongTypeConverter;
import net.imglib2.converter.readwrite.longaccess.ShortLongAccessTypeShortTypeConverter;
import net.imglib2.converter.readwrite.longaccess.UnsignedByteLongAccessTypeUnsignedByteTypeConverter;
import net.imglib2.converter.readwrite.longaccess.UnsignedIntLongAccessTypeUnsignedIntTypeConverter;
import net.imglib2.converter.readwrite.longaccess.UnsignedLongLongAccessTypeUnsignedLongTypeConverter;
import net.imglib2.converter.readwrite.longaccess.UnsignedShortLongAccessTypeUnsignedShortTypeConverter;
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
			return Converters.convertRandomAccessibleIterableInterval( img, new ARGBLongAccessTypeARGBTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new ComplexFloatLongAccessTypeComplexFloatTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new ComplexDoubleLongAccessTypeComplexDoubleTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new FloatLongAccessTypeFloatTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new DoubleLongAccessTypeDoubleTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new ByteLongAccessTypeByteTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new ShortLongAccessTypeShortTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new IntLongAccessTypeIntTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new LongLongAccessTypeLongTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new UnsignedByteLongAccessTypeUnsignedByteTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new UnsignedShortLongAccessTypeUnsignedShortTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new UnsignedIntLongAccessTypeUnsignedIntTypeConverter() );
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
			return Converters.convertRandomAccessibleIterableInterval( img, new UnsignedLongLongAccessTypeUnsignedLongTypeConverter() );
		}
	}

}
