import imglyb
from jnius import autoclass

NativeType = autoclass( 'net.imglib2.type.NativeType' )

FloatType = autoclass( 'net.imglib2.type.numeric.real.FloatType' )
VolatileFloatType = autoclass( 'net.imglib2.type.volatiles.VolatileFloatType' )
