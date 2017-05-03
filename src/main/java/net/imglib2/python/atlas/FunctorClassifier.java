package net.imglib2.python.atlas;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.atlas.classification.Classifier;
import net.imglib2.img.array.ArrayCursor;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningFloatUnsafe;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningIntUnsafe;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Intervals;
import net.imglib2.view.Views;
import net.imglib2.view.composite.Composite;
import net.imglib2.view.composite.CompositeIntervalView;
import net.imglib2.view.composite.RealComposite;

public class FunctorClassifier< I extends IntegerType< I > & NativeType< I > >
implements Classifier< Composite< FloatType >, RandomAccessibleInterval< FloatType >, RandomAccessibleInterval< I > >
{

	private final int numFeatures;

	private final ClassificationImplementation impl;

	public FunctorClassifier( final int numFeatures, final ClassificationImplementation impl )
	{
		super();
		this.numFeatures = numFeatures;
		this.impl = impl;
	}

	public static interface ClassificationImplementation
	{

		public void train( ArrayImg< FloatType, OwningFloatUnsafe > features, final int[] labels ) throws Exception;

		public void predict( ArrayImg< FloatType, OwningFloatUnsafe > features, ArrayImg< UnsignedIntType, OwningIntUnsafe > labels ) throws Exception;

	}

	@Override
	public void predictLabels( final RandomAccessibleInterval< FloatType > instances, final RandomAccessibleInterval< I > labels ) throws Exception
	{
		final CompositeIntervalView< FloatType, RealComposite< FloatType > > instancesComposites = Views.collapseReal( instances );
		final long numInstances = Intervals.numElements( labels );

		final ArrayImg< FloatType, OwningFloatUnsafe > instancesUnsafe = ArrayImgs.floats( new OwningFloatUnsafe( numInstances * numFeatures ), numFeatures, numInstances );

		long sample = 0;
		for ( final RealComposite< FloatType > instance : Views.flatIterable( instancesComposites ) )
		{
			final Cursor< FloatType > cursor = Views.hyperSlice( instancesUnsafe, 1, sample ).cursor();
			for ( int d = 0; d < numFeatures; ++d )
				cursor.next().set( instance.get( d ) );

			++sample;
		}

		final ArrayImg< UnsignedIntType, OwningIntUnsafe > labelsUnsafe = ArrayImgs.unsignedInts( new OwningIntUnsafe( numInstances ), numInstances );
		impl.predict( instancesUnsafe, labelsUnsafe );

		final Cursor< I > labelsCursor = Views.flatIterable( labels ).cursor();
		final ArrayCursor< UnsignedIntType > unsafeCursor = labelsUnsafe.cursor();
		while ( unsafeCursor.hasNext() )
			labelsCursor.next().setInteger( unsafeCursor.next().get() );

	}

	@Override
	public void trainClassifier( final Iterable< Composite< FloatType > > samples, final int[] labels ) throws Exception
	{
		final int size = labels.length * numFeatures;
		final ArrayImg< FloatType, OwningFloatUnsafe > features = ArrayImgs.floats( new OwningFloatUnsafe( size ), numFeatures, labels.length );
		final Iterator< Composite< FloatType > > it = samples.iterator();
		for ( int i = 0; i < labels.length; ++i )
		{
			final Cursor< FloatType > cursor = Views.hyperSlice( features, 1, i ).cursor();
			final Composite< FloatType > comp = it.next();
			for ( int d = 0; d < numFeatures; ++d )
				cursor.next().set( comp.get( d ) );
		}
		impl.train( features, labels );

	}

	@Override
	public void saveClassifier( final String path, final boolean overwrite ) throws Exception
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void loadClassifier( final String path ) throws Exception
	{
		// TODO Auto-generated method stub

	}

}
