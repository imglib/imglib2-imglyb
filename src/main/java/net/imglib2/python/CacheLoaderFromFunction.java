package net.imglib2.python;

import java.util.function.Function;

import net.imglib2.cache.CacheLoader;

public class CacheLoaderFromFunction< K, V > implements CacheLoader< K, V >
{

	private final Function< K, V > func;

	public CacheLoaderFromFunction( final Function< K, V > func )
	{
		super();
		this.func = func;
	}

	@Override
	public V get( final K key )
	{
		final V val = func.apply( key );
		return val;
	}

}
