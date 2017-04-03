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
import net.imglib2.img.basictypelongaccess.DoubleLongAccess;
import net.imglib2.img.basictypelongaccess.FloatLongAccess;
import net.imglib2.img.basictypelongaccess.IntLongAccess;
import net.imglib2.img.basictypelongaccess.unsafe.ByteUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.DoubleUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.FloatUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.IntUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.LongUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.ShortUnsafe;
import net.imglib2.python.img.strided.StridedImg;
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

public class NumpyToImgLibConversionsWithStride
{

	public static RandomAccessibleInterval< ARGBType > toARGB( final long address, final long[] stride, final long[] dim )
	{
		final IntUnsafe access = new IntUnsafe( address );
		final StridedImg< ARGBLongAccessType, IntLongAccess > img = new StridedImg<>( access, dim, stride, new Fraction() );
		final ARGBLongAccessType type = new ARGBLongAccessType( img );
		img.setLinkedType( type );
		return Converters.convertRandomAccessibleIterableInterval( img, new ARGBLongAccessTypeARGBTypeConverter() );
	}

	public static RandomAccessibleInterval< ComplexFloatType > toComplexFloat( final long address, final long[] stride, final long[] dim )
	{
		final FloatUnsafe access = new FloatUnsafe( address );
		final StridedImg< ComplexFloatLongAccessType, FloatLongAccess > img = new StridedImg<>( access, dim, stride, new Fraction( 2, 1 ) );
		final ComplexFloatLongAccessType type = new ComplexFloatLongAccessType( img );
		img.setLinkedType( type );
		return Converters.convertRandomAccessibleIterableInterval( img, new ComplexFloatLongAccessTypeComplexFloatTypeConverter() );
	}

	public static RandomAccessibleInterval< ComplexDoubleType > toComplexDouble( final long address, final long[] stride, final long[] dim )
	{
		final DoubleUnsafe access = new DoubleUnsafe( address );
		final StridedImg< ComplexDoubleLongAccessType, DoubleLongAccess > img = new StridedImg<>( access, dim, stride, new Fraction( 2, 1 ) );
		final ComplexDoubleLongAccessType type = new ComplexDoubleLongAccessType( img );
		img.setLinkedType( type );
		return Converters.convertRandomAccessibleIterableInterval( img, new ComplexDoubleLongAccessTypeComplexDoubleTypeConverter() );
	}

	public static RandomAccessibleInterval< FloatType > toFloat( final long address, final long[] stride, final long[] dim )
	{
		final FloatUnsafe access = new FloatUnsafe( address );
		final StridedImg< FloatLongAccessType, FloatUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new FloatLongAccessType( img ) );
		return Converters.convertRandomAccessibleIterableInterval( img, new FloatLongAccessTypeFloatTypeConverter() );
	}

	public static RandomAccessibleInterval< DoubleType > toDouble( final long address, final long[] stride, final long[] dim )
	{
		final DoubleUnsafe access = new DoubleUnsafe( address );
		final StridedImg< DoubleLongAccessType, DoubleUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new DoubleLongAccessType( img ) );
		return Converters.convertRandomAccessibleIterableInterval( img, new DoubleLongAccessTypeDoubleTypeConverter() );
	}

	public static RandomAccessibleInterval< ByteType > toByte( final long address, final long[] stride, final long[] dim )
	{
		final ByteUnsafe access = new ByteUnsafe( address );
		final StridedImg< ByteLongAccessType, ByteUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new ByteLongAccessType( img ) );
		return Converters.convertRandomAccessibleIterableInterval( img, new ByteLongAccessTypeByteTypeConverter() );
	}

	public static RandomAccessibleInterval< ShortType > toShort( final long address, final long[] stride, final long[] dim )
	{
		final ShortUnsafe access = new ShortUnsafe( address );
		final StridedImg< ShortLongAccessType, ShortUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new ShortLongAccessType( img ) );
		return Converters.convertRandomAccessibleIterableInterval( img, new ShortLongAccessTypeShortTypeConverter() );
	}

	public static RandomAccessibleInterval< IntType > toInt( final long address, final long[] stride, final long[] dim )
	{
		final IntUnsafe access = new IntUnsafe( address );
		final StridedImg< IntLongAccessType, IntUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new IntLongAccessType( img ) );
		return Converters.convertRandomAccessibleIterableInterval( img, new IntLongAccessTypeIntTypeConverter() );
	}

	public static RandomAccessibleInterval< LongType > toLong( final long address, final long[] stride, final long[] dim )
	{
		final LongUnsafe access = new LongUnsafe( address );
		final StridedImg< LongLongAccessType, LongUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new LongLongAccessType( img ) );
		return Converters.convertRandomAccessibleIterableInterval( img, new LongLongAccessTypeLongTypeConverter() );
	}

	public static RandomAccessibleInterval< UnsignedByteType > toUnsignedByte( final long address, final long[] stride, final long[] dim )
	{
		final ByteUnsafe access = new ByteUnsafe( address );
		final StridedImg< UnsignedByteLongAccessType, ByteUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new UnsignedByteLongAccessType( img ) );
		return Converters.convertRandomAccessibleIterableInterval( img, new UnsignedByteLongAccessTypeUnsignedByteTypeConverter() );
	}

	public static RandomAccessibleInterval< UnsignedShortType > toUnsignedShort( final long address, final long[] stride, final long[] dim )
	{
		final ShortUnsafe access = new ShortUnsafe( address );
		final StridedImg< UnsignedShortLongAccessType, ShortUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new UnsignedShortLongAccessType( img ) );
		return Converters.convertRandomAccessibleIterableInterval( img, new UnsignedShortLongAccessTypeUnsignedShortTypeConverter() );
	}

	public static RandomAccessibleInterval< UnsignedIntType > toUnsignedInt( final long address, final long[] stride, final long[] dim )
	{
		final IntUnsafe access = new IntUnsafe( address );
		final StridedImg< UnsignedIntLongAccessType, IntUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new UnsignedIntLongAccessType( img ) );
		return Converters.convertRandomAccessibleIterableInterval( img, new UnsignedIntLongAccessTypeUnsignedIntTypeConverter() );
	}

	public static RandomAccessibleInterval< UnsignedLongType > toUnsignedLong( final long address, final long[] stride, final long[] dim )
	{
		final LongUnsafe access = new LongUnsafe( address );
		final StridedImg< UnsignedLongLongAccessType, LongUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new UnsignedLongLongAccessType( img ) );
		return Converters.convertRandomAccessibleIterableInterval( img, new UnsignedLongLongAccessTypeUnsignedLongTypeConverter() );
	}

}
