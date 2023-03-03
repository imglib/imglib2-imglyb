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
package net.imglib2.python.img.strided;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractNativeLongAccessImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeLongAccessType;
import net.imglib2.util.Fraction;
import net.imglib2.util.Pair;
import net.imglib2.view.RandomAccessibleIntervalCursor;
import net.imglib2.view.Views;

public class StridedImg< T extends NativeLongAccessType< T >, A > extends AbstractNativeLongAccessImg< T, A >
{

	private final A data;

	protected final long[] stride;

	public StridedImg( final A data, final long[] dim, final long[] stride, final Fraction entitiesPerPixel )
	{
		super( dim, entitiesPerPixel );
		this.data = data;
		this.stride = stride;

	}

	@Override
	public ImgFactory< T > factory()
	{
		// Currently, a factory does not make any sense for this image, as this
		// is merely a view.
		return null;
	}

	@Override
	public Img< T > copy()
	{
		final Img< T > copy = factory().create( this, firstElement().createVariable() );

		for ( final Pair< T, T > p : Views.interval( Views.pair( this, copy ), this ) )
			p.getB().set( p.getA() );

		return copy;
	}

	@Override
	public A update( final Object updater )
	{
		return data;
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return new StridedRandomAccess<>( this );
	}

	@Override
	public Cursor< T > cursor()
	{
		return new RandomAccessibleIntervalCursor<>( this );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return cursor();
	}

	@Override
	public Object iterationOrder()
	{
		return null;
	}

}
