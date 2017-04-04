package net.imglib2.python;

import net.imglib2.AbstractWrappedInterval;
import net.imglib2.Interval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;

public class ReferenceGuardingRandomAccessibleInterval< T > extends AbstractWrappedInterval< RandomAccessibleInterval< T > > implements RandomAccessibleInterval< T >
{


	public static interface ReferenceHolder
	{

	}

	// This variable just needs to be around until the object gets
	// destroyed/garbage collected.
	private final ReferenceHolder referenceHolder;

	public ReferenceHolder getReferenceHolder()
	{
		return referenceHolder;
	}

	public ReferenceGuardingRandomAccessibleInterval( final RandomAccessibleInterval< T > source, final ReferenceHolder referenceHolder )
	{
		super( source );
		this.referenceHolder = referenceHolder;
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return sourceInterval.randomAccess();
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval interval )
	{
		return sourceInterval.randomAccess( interval );
	}

}
