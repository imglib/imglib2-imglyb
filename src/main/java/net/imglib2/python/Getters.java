package net.imglib2.python;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.real.FloatType;

public class Getters
{

	public static < T extends NativeType< T > > float getFloat( final T ft )
	{
		final ArrayImg< FloatType, FloatArray > a = ArrayImgs.floats( 123 );
		a.update( null ).getCurrentStorageArray();
		return ( ( FloatType ) ft ).get();
	}

}
