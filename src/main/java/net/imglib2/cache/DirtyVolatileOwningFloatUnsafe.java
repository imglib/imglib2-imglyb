package net.imglib2.cache;

import net.imglib2.Dirty;
import net.imglib2.img.basictypeaccess.volatiles.VolatileFloatAccess;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningFloatUnsafe;

public class DirtyVolatileOwningFloatUnsafe extends OwningFloatUnsafe implements Dirty, VolatileFloatAccess
{

	private final boolean isValid;

	private boolean dirty = false;

	public DirtyVolatileOwningFloatUnsafe( final long numEntitites, final boolean isValid )
	{
		super( numEntitites );
		this.isValid = isValid;
	}

	@Override
	public boolean isDirty()
	{
		return dirty;
	}

	@Override
	public boolean isValid()
	{
		return isValid;
	}

	@Override
	public void setValue( final int index, final float value )
	{
		dirty = true;
		super.setValue( index, value );
	}

}
