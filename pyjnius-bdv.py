# config needs to be set-up before "import jnius" is run
import jnius_config
jnius_config.add_options('-Xmx40g')

import os
conda_prefix = os.environ['CONDA_PREFIX']

classpath = [
	'{}/share/pyjnius/pyjnius.jar'.format( conda_prefix ),
	os.environ['IMGLYB_JAR']
	# "/home/hanslovskyp/workspace/bdv-python/bigdataviewer-vistools/target/bigdataviewer-vistools-1.0.0-beta-5-SNAPSHOT-jar-with-dependencies.jar",
	#/home/phil/workspace/bdv-python/imglib2-python-fat-jar/target/imglib2-python-fat-jar-0.0.1-SNAPSHOT-jar-with-dependencies.jar",
	#"/home/phil/workspace/bdv-python/pyjnius/build/pyjnius.jar"
]
jnius_config.set_classpath(*classpath)

import ctypes

import jnius
from jnius import autoclass, PythonJavaClass, java_method, cast

import numpy as np

import random

import time

import vigra


class Renderer (PythonJavaClass):
	__javainterfaces__ = ['net.imglib2.ui.OverlayRenderer']
	def __init__( self ):
		self.w = 1
		self.h = 1

	@java_method('(Ljava/awt/Graphics;)V')
	def drawOverlays( self, g ):
		print("Drawing")
		# Color = jpype.JPackage("java.awt").Color
		# g.setColor( Color.white );
		# g.drawLine( 0, 0, self.w, self.h );

	@java_method('(II)V')
	def setCanvasSize( self, width, height ):
		print ("Setting size")
		self.w = width
		self.h = height

class SetOneConverter(PythonJavaClass):
	__javainterfaces__ = ['net.imglib2.converter.Converter']

	

class SetZero(PythonJavaClass):
	__javainterfaces__ = ['java.util.function.IntUnaryOperator']

	@java_method('(I)I')
	def applyAsInt(self, input):
		return 0

class JIterator( PythonJavaClass ):
	__javainterfaces__ = ['java/util/Iterator']
	


	@java_method('()Z')
	def hasNext(self):
		return True

	#@java_method('()LJava/lang/Object;')
	@java_method('()V;')
	def next(self):
		return 1

ArrayImgs = autoclass('net.imglib2.img.array.ArrayImgs')
UnsafeImgs = autoclass('net.imglib2.img.unsafe.UnsafeImgs')
IntUnsafe = autoclass('net.imglib2.img.basictypelongaccess.unsafe.IntUnsafe')
FloatUnsafe = autoclass('net.imglib2.img.basictypelongaccess.unsafe.FloatUnsafe')
PythonFunctions = autoclass('net.imglib.python.PythonFunctions')
def toArrayImg( source, tag ):
	if tag == 'argb':
		ct_pt = ctypes.cast( source.ctypes.data_as(ctypes.POINTER(ctypes.c_int)), ctypes.c_void_p ).value
		return PythonFunctions.toARGB( ct_pt, *source.shape )
	elif tag == 'float32':
		ct_pt = ctypes.cast( source.ctypes.data_as(ctypes.POINTER(ctypes.c_float)), ctypes.c_void_p ).value
		print ( 'ok?', source.dtype, source.shape )
		return PythonFunctions.toFloat( ct_pt, *source.shape )
	return None
		
if __name__ == "__main__":
	autoclass('org.jnius.NativeInvocationHandler').DEBUG = True
	Views = autoclass('net.imglib2.view.Views')
	BdvFunctions = autoclass('bdv.util.BdvFunctions')
	BdvOptions = autoclass('bdv.util.BdvOptions')
	IntStream = autoclass('java.util.stream.IntStream')
	Random = autoclass('java.util.Random')
	Arrays = autoclass('java.util.Arrays')
	DistanceTransform = autoclass('net.imglib2.algorithm.morphology.distance.DistanceTransform')
	Runtime = autoclass('java.lang.Runtime')
	print(jnius)
	rng = Random( 100 )

	# seem to need that one
	autoclass('org.jnius.NativeInvocationHandler')

	bfly = vigra.readImage('/home/phil/Dropbox/misc/butterfly.jpg').astype(np.uint32)
	bflyArgb = \
	np.left_shift(bfly[...,0], np.zeros(bfly.shape[:-1],dtype=np.uint8) + 16) + \
	np.left_shift(bfly[...,1], np.zeros(bfly.shape[:-1],dtype=np.uint8) + 8)  + \
	np.left_shift(bfly[...,2], np.zeros(bfly.shape[:-1],dtype=np.uint8) + 0)
	bflyImgLib = toArrayImg( bflyArgb, 'argb' )
	print(bflyArgb.dtype)
	print(bflyImgLib)
	bdv = BdvFunctions.show(bflyImgLib, 'test', BdvOptions.options().is2D())
	print(bdv)
	print(bflyImgLib.numDimensions(), bflyImgLib.dimension(0), bflyImgLib.dimension(1))
	avg = np.mean(bfly, axis=2).astype(np.float32)
	avgScaled = (avg * 2 ** 16 / avg.max()).astype(np.float32)
	avgImgLib = toArrayImg( avgScaled, 'float32' )

	# if ( bdv is None ):
	# 	bdv = BdvFunctions.show(avgImgLib, 'avg', BdvOptions.options().is2D())
	# else:
	BdvFunctions.show( avgImgLib, 'avg', BdvOptions.options().addTo( bdv ) )

	filtered = vigra.filters.gaussianGradientMagnitude(avg, 3.0)
	filteredScaled = (filtered * 2 ** 16 / filtered.max()).astype(np.float32)
	filteredImgLib = toArrayImg( filteredScaled, 'float32' )
	
	BdvFunctions.show( filteredImgLib, 'gradient', BdvOptions.options().addTo( bdv ) )

	distTarget = np.empty(filtered.shape, dtype=np.float32)
	distTmp = np.empty(filtered.shape, dtype=np.float32)
	dt = filtered.copy()
	print ( "dt mean before: ", dt.mean())
	dtImgLib = toArrayImg( dt, 'float32' )
	PythonFunctions.distanceTransform( dtImgLib, 0.00001 )
	print ( "dt mean after: ", dt.mean())
	dtScaled = (dt * 2 ** 16 / dt.max()).astype(np.float32)
	dtScaledImgLib = toArrayImg( dtScaled, 'float32' )
	
	BdvFunctions.show( dtScaledImgLib, 'dt', BdvOptions.options().addTo( bdv ) )
	

	# np_img = (np.random.rand(300, 400, 50) * (2**32)).astype(np.int32)
	# ct_pt = ctypes.cast( np_img.ctypes.data_as(ctypes.POINTER(ctypes.c_int)), ctypes.c_void_p ).value
	# # print( type(ct_pt), ct_pt, int(ct_pt), np_img.shape )

	# bdv = BdvFunctions.show(img, 'test', BdvOptions.options())

		
	# rotated = Views.rotate(img, 1, 0)
	# rot = BdvFunctions.show(rotated, 'rotated', BdvOptions.options().is2D())
	

	# it = JIterator()
	# print( it.hasNext(), it.next() )
	r = Renderer()
	# # r.drawOverlays(None)
	# # r.setCanvasSize(1,2)

	sz = SetZero()
	print (IntStream.range(0,3).toArray())
	seq = IntStream.range(0,3).map( sz ).toArray()
	print( seq )


	# vp = bdv.getBdvHandle().getViewerPanel().getDisplay().addOverlayRenderer( r ) # crashes ?
	
	while True:
		# print( it.next() )
		time.sleep(0.1)
