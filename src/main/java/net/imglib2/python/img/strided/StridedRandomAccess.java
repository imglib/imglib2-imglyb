/*-
 * #%L
 * Utility and helper methods to facilitate python imglib2 interaction with shared memory.
 * %%
 * Copyright (C) 2017 - 2021 Howard Hughes Medical Institute.
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

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.type.NativeLongAccessType;

public class StridedRandomAccess< T extends NativeLongAccessType< T > > extends AbstractLocalizable implements RandomAccess< T >
{

	protected final T type;

	final StridedImg< T, ? > img;

	protected StridedRandomAccess( final StridedRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		this.img = randomAccess.img;
		this.type = img.createLinkedType();

		int index = 0;
		for ( int d = 0; d < n; d++ )
		{
			position[ d ] = randomAccess.position[ d ];
			index += position[ d ] * img.stride[ d ];
		}

		type.updateContainer( this );
		type.updateIndex( index );
	}

	public StridedRandomAccess( final StridedImg< T, ? > container )
	{
		super( container.numDimensions() );

		this.img = container;
		this.type = container.createLinkedType();

		for ( int d = 0; d < n; d++ )
			position[ d ] = 0;

		type.updateContainer( this );
		type.updateIndex( 0 );
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public void fwd( final int d )
	{
		type.incIndex( img.stride[ d ] );
		++position[ d ];
	}

	@Override
	public void bck( final int d )
	{
		type.decIndex( img.stride[ d ] );
		--position[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		type.incIndex( img.stride[ d ] * distance );
		position[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		type.incIndex( img.stride[ d ] * ( int ) distance );
		position[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		int index = 0;
		for ( int d = 0; d < n; ++d )
		{
			final int distance = localizable.getIntPosition( d );
			position[ d ] += distance;
			index += distance * img.stride[ d ];
		}
		type.incIndex( index );
	}

	@Override
	public void move( final int[] distance )
	{
		int index = 0;
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			index += distance[ d ] * img.stride[ d ];
		}
		type.incIndex( index );
	}

	@Override
	public void move( final long[] distance )
	{
		int index = 0;
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			index += distance[ d ] * img.stride[ d ];
		}
		type.incIndex( index );
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		localizable.localize( position );
		int index = 0;
		for ( int d = 0; d < n; ++d )
			index += position[ d ] * img.stride[ d ];
		type.updateIndex( index );
	}

	@Override
	public void setPosition( final int[] pos )
	{
		int index = 0;
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = pos[ d ];
			index += pos[ d ] * img.stride[ d ];
		}
		type.updateIndex( index );
	}

	@Override
	public void setPosition( final long[] pos )
	{
		int index = 0;
		for ( int d = 0; d < n; ++d )
		{
			final int p = ( int ) pos[ d ];
			position[ d ] = p;
			index += p * img.stride[ d ];
		}
		type.updateIndex( index );
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		type.incIndex( ( pos - position[ d ] ) * img.stride[ d ] );
		position[ d ] = pos;
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		type.incIndex( ( ( int ) pos - position[ d ] ) * img.stride[ d ] );
		position[ d ] = ( int ) pos;
	}

	@Override
	public StridedRandomAccess< T > copy()
	{
		return new StridedRandomAccess<>( this );
	}

	@Override
	public StridedRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}

}
