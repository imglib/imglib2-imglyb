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
