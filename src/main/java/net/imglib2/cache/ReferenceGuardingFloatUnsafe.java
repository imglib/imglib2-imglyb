package net.imglib2.cache;

import net.imglib2.Dirty;
import net.imglib2.img.basictypelongaccess.unsafe.FloatUnsafe;

public class ReferenceGuardingFloatUnsafe extends FloatUnsafe implements Dirty
{

	public static interface ReferenceHolder
	{

	}

	private final ReferenceHolder reference;

	private final boolean isDirty;

	public ReferenceGuardingFloatUnsafe( final long address, final ReferenceHolder reference, final boolean isDirty )
	{
		super( address );
		this.reference = reference;
		this.isDirty = isDirty;
	}

	@Override
	public boolean isDirty()
	{
		return isDirty;
	}

	public ReferenceHolder getReference()
	{
		return reference;
	}

}
