package net.imglib2.cache;

import java.util.Arrays;

import net.imglib2.img.basictypeaccess.array.DirtyFloatArray;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;
import net.imglib2.util.Intervals;

/**
 *
 * c&p from
 * https://github.com/imglib/imglib2-cache-examples/blob/master/src/main/java/net/imglib2/cache/example06/Example06.java
 * with modifications
 *
 */
public class CheckerboardLoader implements CacheLoader< Long, Cell< DirtyFloatArray > >
{
	private final CellGrid grid;

	public CheckerboardLoader( final CellGrid grid )
	{
		this.grid = grid;
	}

	@Override
	public Cell< DirtyFloatArray > get( final Long key ) throws Exception
	{
		final long index = key;

		final int n = grid.numDimensions();
		final long[] cellMin = new long[ n ];
		final int[] cellDims = new int[ n ];
		grid.getCellDimensions( index, cellMin, cellDims );
		final int blocksize = ( int ) Intervals.numElements( cellDims );
		final DirtyFloatArray array = new DirtyFloatArray( blocksize );

		final long[] cellGridPosition = new long[ n ];
		grid.getCellGridPositionFlat( index, cellGridPosition );
		long sum = 0;
		for ( int d = 0; d < n; ++d )
			sum += cellGridPosition[ d ];
		final float color = sum % 2 == 0 ? 0f : 65000f;
		Arrays.fill( array.getCurrentStorageArray(), color );

		return new Cell<>( cellDims, cellMin, array );
	}
}
