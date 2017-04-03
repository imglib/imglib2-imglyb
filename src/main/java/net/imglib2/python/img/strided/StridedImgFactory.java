package net.imglib2.python.img.strided;

import net.imglib2.exception.IncompatibleTypeException;
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

	public StridedImg< T, A > create( final long[] dim, final long[] stride, final A access, final T type )
	{
		final StridedImg< T, A > img = new StridedImg<>( access, dim, stride, type.getEntitiesPerPixel() );
		final T linkedType = type.createVariable();
		img.setLinkedType( linkedType );
		return img;
	}

}
