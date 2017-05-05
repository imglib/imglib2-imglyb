package net.imglib2.python;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningByteUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningDoubleUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningFloatUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningIntUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningLongUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningShortUnsafe;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.complex.ComplexDoubleType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedLongType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Intervals;

public class ArrayImgWithUnsafeStoreFactory< T extends NativeType< T > > extends ImgFactory< T >
{

	@SuppressWarnings( "unchecked" )
	@Override
	public Img< T > create( final long[] dim, final T type )
	{
		final long numEntities = Intervals.numElements( dim );

		if ( type instanceof ARGBType )
		{
			final ArrayImg< ARGBType, OwningIntUnsafe > img = new ArrayImg<>( new OwningIntUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new ARGBType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof ComplexDoubleType )
		{
			final ArrayImg< ComplexDoubleType, OwningDoubleUnsafe > img = new ArrayImg<>( new OwningDoubleUnsafe( numEntities ), dim, new Fraction( 2, 1 ) );
			img.setLinkedType( new ComplexDoubleType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof ComplexFloatType )
		{
			final ArrayImg< ComplexFloatType, OwningFloatUnsafe > img = new ArrayImg<>( new OwningFloatUnsafe( numEntities ), dim, new Fraction( 2, 1 ) );
			img.setLinkedType( new ComplexFloatType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof DoubleType )
		{
			final ArrayImg< DoubleType, OwningDoubleUnsafe > img = new ArrayImg<>( new OwningDoubleUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new DoubleType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof FloatType )
		{
			final ArrayImg< FloatType, OwningFloatUnsafe > img = new ArrayImg<>( new OwningFloatUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new FloatType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof ByteType )
		{
			final ArrayImg< ByteType, OwningByteUnsafe > img = new ArrayImg<>( new OwningByteUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new ByteType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof UnsignedByteType )
		{
			final ArrayImg< UnsignedByteType, OwningByteUnsafe > img = new ArrayImg<>( new OwningByteUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new UnsignedByteType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof IntType )
		{
			final ArrayImg< IntType, OwningIntUnsafe > img = new ArrayImg<>( new OwningIntUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new IntType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof UnsignedIntType )
		{
			final ArrayImg< UnsignedIntType, OwningIntUnsafe > img = new ArrayImg<>( new OwningIntUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new UnsignedIntType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof LongType )
		{
			final ArrayImg< LongType, OwningLongUnsafe > img = new ArrayImg<>( new OwningLongUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new LongType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof UnsignedLongType )
		{
			final ArrayImg< UnsignedLongType, OwningLongUnsafe > img = new ArrayImg<>( new OwningLongUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new UnsignedLongType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof ShortType )
		{
			final ArrayImg< ShortType, OwningShortUnsafe > img = new ArrayImg<>( new OwningShortUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new ShortType( img ) );
			return ( Img< T > ) img;
		}

		if ( type instanceof UnsignedShortType )
		{
			final ArrayImg< UnsignedShortType, OwningShortUnsafe > img = new ArrayImg<>( new OwningShortUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new UnsignedShortType( img ) );
			return ( Img< T > ) img;
		}

		return null;

	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( type instanceof NativeType )
			return new ArrayImgWithUnsafeStoreFactory();
		throw new IncompatibleTypeException( type, "Does not implement NativeType!" );
	}

}
