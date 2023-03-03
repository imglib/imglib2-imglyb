/*-
 * #%L
 * Utility and helper methods to facilitate python imglib2 interaction with shared memory.
 * %%
 * Copyright (C) 2017 - 2023 Howard Hughes Medical Institute.
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

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningBooleanUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningByteUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningDoubleUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningFloatUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningIntUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningLongUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningShortUnsafe;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.NativeBoolType;
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

	@Deprecated
	public ArrayImgWithUnsafeStoreFactory()
	{
		this( null );
	}

	public ArrayImgWithUnsafeStoreFactory( final T t )
	{
		super( t );
	}

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

		if ( type instanceof NativeBoolType )
		{
			final ArrayImg< NativeBoolType, OwningBooleanUnsafe > img = new ArrayImg<>( new OwningBooleanUnsafe( numEntities ), dim, new Fraction() );
			img.setLinkedType( new NativeBoolType( img ) );
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
		if ( type instanceof NativeType ) { return new ArrayImgWithUnsafeStoreFactory( ( NativeType< ? > ) type ); }
		throw new IncompatibleTypeException( type, "Does not implement NativeType!" );
	}

	@Override
	public Img< T > create( final long... dimensions )
	{
		return create( dimensions, type() );
	}

}
