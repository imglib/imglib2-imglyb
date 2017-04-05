package net.imglib2.python.view.flat;

import net.imglib2.Dimensions;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.IntervalIndexer;

public class FlatRandomAccess< T > implements RandomAccess< T >
{

	public static int N_DIM = 1;

	private long pos = 0;

	private final Dimensions referenceDimensions;

	private final RandomAccess< T > sourceAccess;

	public FlatRandomAccess( final RandomAccessibleInterval< T > source )
	{
		this( source, source.randomAccess() );
	}

	private FlatRandomAccess( final Dimensions referenceDimensions, final RandomAccess< T > sourceAccess )
	{
		super();
		this.referenceDimensions = referenceDimensions;
		this.sourceAccess = sourceAccess;
	}

	@Override
	public void localize( final int[] position )
	{
		position[ 0 ] = ( int ) pos;
	}

	@Override
	public void localize( final long[] position )
	{
		position[ 0 ] = pos;
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int ) pos;
	}

	@Override
	public long getLongPosition( final int d )
	{
		return pos;
	}

	@Override
	public void localize( final float[] position )
	{
		position[ 0 ] = pos;
	}

	@Override
	public void localize( final double[] position )
	{
		position[ 0 ] = pos;
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return pos;
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return pos;
	}

	@Override
	public int numDimensions()
	{
		return N_DIM;
	}

	@Override
	public void fwd( final int d )
	{
		++pos;
	}

	@Override
	public void bck( final int d )
	{
		--pos;
	}

	@Override
	public void move( final int distance, final int d )
	{
		pos += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		pos += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		pos += localizable.getLongPosition( 0 );
	}

	@Override
	public void move( final int[] distance )
	{
		pos += distance[ 0 ];
	}

	@Override
	public void move( final long[] distance )
	{
		pos += distance[ 0 ];
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		pos = localizable.getLongPosition( 0 );
	}

	@Override
	public void setPosition( final int[] position )
	{
		pos = position[ 0 ];
	}

	@Override
	public void setPosition( final long[] position )
	{
		pos = position[ 0 ];
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		pos = position;
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		pos = position;
	}

	@Override
	public T get()
	{
		IntervalIndexer.indexToPosition( pos, referenceDimensions, sourceAccess );
		return sourceAccess.get();
	}

	@Override
	public FlatRandomAccess< T > copy()
	{
		return copyRandomAccess();
	}

	@Override
	public FlatRandomAccess< T > copyRandomAccess()
	{
		return new FlatRandomAccess<>( referenceDimensions, sourceAccess.copyRandomAccess() );
	}

}
