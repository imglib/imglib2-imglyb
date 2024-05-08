/*-
 * #%L
 * Utility and helper methods to facilitate python imglib2 interaction with shared memory.
 * %%
 * Copyright (C) 2017 - 2024 Howard Hughes Medical Institute.
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
package net.imglib2.python.img.strided;

import java.util.stream.LongStream;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeLongAccessType;
import net.imglib2.util.IntervalIndexer;

public class StridedImgFactory< T extends NativeLongAccessType< T >, A > extends ImgFactory< T >
{

	public static interface AccessCreator< A >
	{

		public A create( long[] dim );

	}

	private final AccessCreator< A > accessCreator;

	public StridedImgFactory( final AccessCreator< A > accessCreator, final T t )
	{
		super( t );
		this.accessCreator = accessCreator;
	}

	@Deprecated
	public StridedImgFactory( final AccessCreator< A > accessCreator )
	{
		super();
		this.accessCreator = accessCreator;
	}

	@Override
	public StridedImg< T, A > create( final long[] dim, final T type )
	{
		final long[] stride = new long[ dim.length ];
		IntervalIndexer.createAllocationSteps( dim, stride );
		return create( dim, stride, accessCreator.create( stride ), type );
	}

	@Override
	public < S > ImgFactory< S > imgFactory( final S type ) throws IncompatibleTypeException
	{
		throw new IncompatibleTypeException( type, "" );
	}

	@Deprecated
	public StridedImg< T, A > create( final long[] dim, final long[] stride, final A access, final T type )
	{
		final StridedImg< T, A > img = new StridedImg<>( access, dim, stride, type.getEntitiesPerPixel() );
		final T linkedType = type.createVariable();
		img.setLinkedType( linkedType );
		return img;
	}

	public StridedImg< T, A > create( final long[] dim, final long[] stride, final A access )
	{
		return create( dim, stride, access, type() );
	}

	@Override
	public Img< T > create( final long... dim )
	{
		return create( dim, LongStream.generate( () -> 1 ).limit( dim.length ).toArray(), accessCreator.create( dim ) );
	}

}
