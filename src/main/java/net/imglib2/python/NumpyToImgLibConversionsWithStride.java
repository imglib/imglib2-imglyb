package net.imglib2.python;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.converter.Converter;
import net.imglib2.converter.Converters;
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
		final Converter< ARGBLongAccessType, ARGBType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< ARGBLongAccessType > ) img, converter, new ARGBType() );
	}

	public static RandomAccessibleInterval< ComplexFloatType > toComplexFloat( final long address, final long[] stride, final long[] dim )
	{
		final FloatUnsafe access = new FloatUnsafe( address );
		final StridedImg< ComplexFloatLongAccessType, FloatLongAccess > img = new StridedImg<>( access, dim, stride, new Fraction( 2, 1 ) );
		final ComplexFloatLongAccessType type = new ComplexFloatLongAccessType( img );
		img.setLinkedType( type );
		final Converter< ComplexFloatLongAccessType, ComplexFloatType > converter = ( s, t ) -> {
			t.set( s.getRealFloat(), s.getImaginaryFloat() );
		};
		return Converters.convert( ( RandomAccessibleInterval< ComplexFloatLongAccessType > ) img, converter, new ComplexFloatType() );
	}

	public static RandomAccessibleInterval< ComplexDoubleType > toComplexDouble( final long address, final long[] stride, final long[] dim )
	{
		final DoubleUnsafe access = new DoubleUnsafe( address );
		final StridedImg< ComplexDoubleLongAccessType, DoubleLongAccess > img = new StridedImg<>( access, dim, stride, new Fraction( 2, 1 ) );
		final ComplexDoubleLongAccessType type = new ComplexDoubleLongAccessType( img );
		img.setLinkedType( type );
		final Converter< ComplexDoubleLongAccessType, ComplexDoubleType > converter = ( s, t ) -> {
			t.set( s.getRealDouble(), s.getImaginaryDouble() );
		};
		return Converters.convert( ( RandomAccessibleInterval< ComplexDoubleLongAccessType > ) img, converter, new ComplexDoubleType() );
	}

	public static RandomAccessibleInterval< FloatType > toFloat( final long address, final long[] stride, final long[] dim )
	{
		final FloatUnsafe access = new FloatUnsafe( address );
		final StridedImg< FloatLongAccessType, FloatUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new FloatLongAccessType( img ) );
		final Converter< FloatLongAccessType, FloatType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< FloatLongAccessType > ) img, converter, new FloatType() );
	}

	public static RandomAccessibleInterval< DoubleType > toDouble( final long address, final long[] stride, final long[] dim )
	{
		final DoubleUnsafe access = new DoubleUnsafe( address );
		final StridedImg< DoubleLongAccessType, DoubleUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new DoubleLongAccessType( img ) );
		final Converter< DoubleLongAccessType, DoubleType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< DoubleLongAccessType > ) img, converter, new DoubleType() );
	}

	public static RandomAccessibleInterval< ByteType > toByte( final long address, final long[] stride, final long[] dim )
	{
		final ByteUnsafe access = new ByteUnsafe( address );
		final StridedImg< ByteLongAccessType, ByteUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new ByteLongAccessType( img ) );
		final Converter< ByteLongAccessType, ByteType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< ByteLongAccessType > ) img, converter, new ByteType() );
	}

	public static RandomAccessibleInterval< ShortType > toShort( final long address, final long[] stride, final long[] dim )
	{
		final ShortUnsafe access = new ShortUnsafe( address );
		final StridedImg< ShortLongAccessType, ShortUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new ShortLongAccessType( img ) );
		final Converter< ShortLongAccessType, ShortType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< ShortLongAccessType > ) img, converter, new ShortType() );
	}

	public static RandomAccessibleInterval< IntType > toInt( final long address, final long[] stride, final long[] dim )
	{
		final IntUnsafe access = new IntUnsafe( address );
		final StridedImg< IntLongAccessType, IntUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new IntLongAccessType( img ) );
		final Converter< IntLongAccessType, IntType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< IntLongAccessType > ) img, converter, new IntType() );
	}

	public static RandomAccessibleInterval< LongType > toLong( final long address, final long[] stride, final long[] dim )
	{
		final LongUnsafe access = new LongUnsafe( address );
		final StridedImg< LongLongAccessType, LongUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new LongLongAccessType( img ) );
		final Converter< LongLongAccessType, LongType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< LongLongAccessType > ) img, converter, new LongType() );
	}

	public static RandomAccessibleInterval< UnsignedByteType > toUnsignedByte( final long address, final long[] stride, final long[] dim )
	{
		final ByteUnsafe access = new ByteUnsafe( address );
		final StridedImg< UnsignedByteLongAccessType, ByteUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new UnsignedByteLongAccessType( img ) );
		final Converter< UnsignedByteLongAccessType, UnsignedByteType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< UnsignedByteLongAccessType > ) img, converter, new UnsignedByteType() );
	}

	public static RandomAccessibleInterval< UnsignedShortType > toUnsignedShort( final long address, final long[] stride, final long[] dim )
	{
		final ShortUnsafe access = new ShortUnsafe( address );
		final StridedImg< UnsignedShortLongAccessType, ShortUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new UnsignedShortLongAccessType( img ) );
		final Converter< UnsignedShortLongAccessType, UnsignedShortType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< UnsignedShortLongAccessType > ) img, converter, new UnsignedShortType() );
	}

	public static RandomAccessibleInterval< UnsignedIntType > toUnsignedInt( final long address, final long[] stride, final long[] dim )
	{
		final IntUnsafe access = new IntUnsafe( address );
		final StridedImg< UnsignedIntLongAccessType, IntUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new UnsignedIntLongAccessType( img ) );
		final Converter< UnsignedIntLongAccessType, UnsignedIntType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< UnsignedIntLongAccessType > ) img, converter, new UnsignedIntType() );
	}

	public static RandomAccessibleInterval< UnsignedLongType > toUnsignedLong( final long address, final long[] stride, final long[] dim )
	{
		final LongUnsafe access = new LongUnsafe( address );
		final StridedImg< UnsignedLongLongAccessType, LongUnsafe > img = new StridedImg<>( access, dim, stride, new Fraction() );
		img.setLinkedType( new UnsignedLongLongAccessType( img ) );
		final Converter< UnsignedLongLongAccessType, UnsignedLongType > converter = ( s, t ) -> {
			t.set( s.get() );
		};
		return Converters.convert( ( RandomAccessibleInterval< UnsignedLongLongAccessType > ) img, converter, new UnsignedLongType() );
	}

}
