package net.imglib2.cache;

import net.imglib2.Dirty;
import net.imglib2.img.basictypelongaccess.unsafe.owning.OwningFloatUnsafe;

public class VolatileOwningFloatUnsafe extends OwningFloatUnsafe implements Dirty
{

	private final boolean isValid;

	public VolatileOwningFloatUnsafe( final long numEntitites, final boolean isValid )
	{
		super( numEntitites );
		this.isValid = isValid;
	}

	@Override
	public boolean isDirty()
	{
		return isValid;
	}

}
