package net.imglib2.python.view.reshape;

import net.imglib2.Dimensions;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.IntervalIndexer;

public class ReshapeRandomAccess< T > extends Point implements RandomAccess< T >
{

	public static int N_DIM = 1;

	private final Dimensions referenceDimensions;

	private final Dimensions targetDimensions;

	private final RandomAccess< T > sourceAccess;

	public ReshapeRandomAccess( final RandomAccessibleInterval< T > source, final Dimensions targetDimensions )
	{
		this( source, targetDimensions, source.randomAccess() );
	}

	private ReshapeRandomAccess( final Dimensions referenceDimensions, final Dimensions targetDimensions, final RandomAccess< T > sourceAccess )
	{
		super( targetDimensions.numDimensions() );
		this.referenceDimensions = referenceDimensions;
		this.targetDimensions = targetDimensions;
		this.sourceAccess = sourceAccess;
	}


	@Override
	public T get()
	{
		final long pos = IntervalIndexer.positionToIndex( this, targetDimensions );
		IntervalIndexer.indexToPosition( pos, referenceDimensions, sourceAccess );
		return sourceAccess.get();
	}

	@Override
	public ReshapeRandomAccess< T > copy()
	{
		return copyRandomAccess();
	}

	@Override
	public ReshapeRandomAccess< T > copyRandomAccess()
	{
		return new ReshapeRandomAccess<>( referenceDimensions, targetDimensions, sourceAccess.copyRandomAccess() );
	}

}
