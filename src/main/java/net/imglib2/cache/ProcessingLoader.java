package net.imglib2.cache;

import java.util.stream.IntStream;

import net.imglib2.FinalInterval;
import net.imglib2.Interval;
import net.imglib2.img.cell.Cell;
import net.imglib2.img.cell.CellGrid;

public class ProcessingLoader< A > implements CacheLoader< Long, Cell< A > >
{

	public static interface Processor< A >
	{
		public A process( Interval interval );
	}

	private final Processor< A > proc;

	private final CellGrid grid;

	public ProcessingLoader( final Processor< A > proc, final CellGrid grid )
	{
		super();
		this.proc = proc;
		this.grid = grid;
	}

	@Override
	public Cell< A > get( final Long key ) throws Exception
	{
		final long index = key;

		final int n = grid.numDimensions();
		final long[] cellMin = new long[ n ];
		final int[] cellDims = new int[ n ];
		grid.getCellDimensions( index, cellMin, cellDims );
		final long[] cellMax = IntStream.range( 0, n ).mapToLong( i -> cellMin[ i ] + cellDims[ i ] - 1 ).toArray();
		final FinalInterval interval = new FinalInterval( cellMin, cellMax );

		final A a = proc.process( interval );


		return new Cell<>( cellDims, cellMin, a );
	}
}
