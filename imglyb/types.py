import imglyb
from jnius import autoclass

NativeType = autoclass( 'net.imglib2.type.NativeType' )

FloatType = autoclass( 'net.imglib2.type.numeric.real.FloatType' )
IntType = autoclass( 'net.imglib2.type.numeric.integer.IntType' )
UnsignedIntType = autoclass( 'net.imglib2.type.numeric.integer.UnsignedIntType' )
VolatileFloatType = autoclass( 'net.imglib2.type.volatiles.VolatileFloatType' )
