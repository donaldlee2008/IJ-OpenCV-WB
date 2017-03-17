package imagingbook.opencv;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;




/**
 * Defines static methods for converting images between ImageJ and OpenCV. 
 * See http://docs.opencv.org/modules/core/doc/basic_structures.html#mat
 * 
 * @author W. Burger
 * @version 2015/10/22
 */

public abstract class Convert {

	// --------------------------------------------------------------------
	// OpenCV -> ImageJ (Mat -> ImageProcessor)
	// --------------------------------------------------------------------
	
	public static ImageProcessor toImageProcessor(Mat in) {
//		IJ.log("in.type = " + in.type());
//		IJ.log("in.channels = " + in.channels());
//		IJ.log("in.depth = " + in.depth());
//		IJ.log("in.elemSize = " + in.elemSize());
//		
//		IJ.log("opencv_core.CV_8UC1 = " + opencv_core.CV_8UC1);
//		IJ.log("opencv_core.CV_8UC3 = " + opencv_core.CV_8UC3);
//		IJ.log("opencv_core.CV_16UC1 = " + opencv_core.CV_16UC1);
//		IJ.log("opencv_core.CV_8UC3 = " + opencv_core.CV_32FC1);
		
		final int type = in.type();
		ImageProcessor result = null;
		
		if (type == opencv_core.CV_8UC1) { // type = BufferedImage.TYPE_BYTE_GRAY;
			result = makeByteProcessor(in);
		}
		else if (type == opencv_core.CV_8UC3) {	// type = BufferedImage.TYPE_3BYTE_BGR;
			result =  makeColorProcessor(in); // faulty 
		}
		else if (type == opencv_core.CV_16UC1) {	// signed short image
			result =  makeShortProcessor(in); 
		}
		else if (type == opencv_core.CV_32FC1) {	// float image
			result =  makeFloatProcessor(in); 
		}
		else {
			throw new IllegalArgumentException("cannot convert Mat of type " + type);
		}	
		return result;
	}
	
	// private methods ----------------------------------------------
	
	private static ByteProcessor makeByteProcessor(Mat in) {
		if (in.type() != opencv_core.CV_8UC1) // opencv_core.CV_8UC1) 
			throw new IllegalArgumentException("wrong Mat type: " + in.type());
		Size size = in.size();
		final int w = size.width(); // in.width(); 
		final int h = size.height(); // in.height();
		
		ByteProcessor bp = new ByteProcessor(w, h);
		byte[] bData = (byte[]) bp.getPixels();

		in.data(new BytePointer(bData));	//in.get(0, 0, bData);
		return new ByteProcessor(w, h, bData);
	}
	
	private static ColorProcessor makeColorProcessor(Mat in) {
		if (in.type() != opencv_core.CV_8UC3)
			throw new IllegalArgumentException("wrong Mat type: " + in.type());
		Size size = in.size();
		final int w = size.width(); // in.width(); 
		final int h = size.height(); // in.height();
		
		byte[] bData = new byte[w * h * (int) in.elemSize()];
		
		in.data(new BytePointer(bData));	//in.get(0, 0, bData);

		ColorProcessor cp = new ColorProcessor(w, h);
		int[] iData = (int[]) cp.getPixels();
		for (int i = 0; i < w * h; i++) {
			int red = bData[i * 3 + 0] & 0xff;
			int grn = bData[i * 3 + 1] & 0xff;
			int blu = bData[i * 3 + 2] & 0xff;
			iData[i] = (red << 16) | (grn << 8) | blu;
		}
		return cp;
	}
	
	private static ShortProcessor makeShortProcessor(Mat in) {
		if (in.type() != opencv_core.CV_16UC1)
			throw new IllegalArgumentException("wrong Mat type: " + in.type());
		Size size = in.size();
		final int w = size.width(); // in.width(); 
		final int h = size.height(); // in.height();
		
		ShortProcessor sp = new ShortProcessor(w, h);
		short[] sData = (short[]) sp.getPixels(); //new short[w * h];  // elemSize = 1
		in.get(0, 0, sData);
		
		return sp;
	}
	
	private static FloatProcessor makeFloatProcessor(Mat in) {
		if (in.type() != opencv_core.CV_32FC1)
			throw new IllegalArgumentException("wrong Mat type: " + in.type());
		Size size = in.size();
		final int w = size.width(); // in.width(); 
		final int h = size.height(); // in.height();
		
		FloatProcessor fp = new FloatProcessor(w, h);
		float[] fData = (float[]) fp.getPixels();
		//in.get(0,  0, fData);
		in.data(new FloatPointer(fData));	
		
		return new FloatProcessor(w, h, fData);
	}
	
	// --------------------------------------------------------------------
	// ImageJ -> OpenCV (ImageProcessor -> Mat)
	// --------------------------------------------------------------------
	
	/**
	 * Dispatcher method.
	 * @param ip the ImageProcessor to be converted
	 * @return the OpenCV image (of type Mat) 
	 */
	public static Mat toMat(ImageProcessor ip) {
		Mat src = null;
		if (ip instanceof ByteProcessor) {
			src = Convert.toMat((ByteProcessor) ip);
		}
		else if (ip instanceof ColorProcessor) {
			src = Convert.toMat((ColorProcessor) ip);
		}
		else if (ip instanceof ShortProcessor) {
			src = Convert.toMat((ShortProcessor) ip);
		}
		else if (ip instanceof FloatProcessor) {
			src = Convert.toMat((FloatProcessor) ip);
		}
		else {
			throw new IllegalArgumentException("cannot convert to Mat: " + ip);
		}
		return src;
	}
	
	
	public static Mat toMat(ByteProcessor bp) {
		final int w = bp.getWidth();
		final int h = bp.getHeight();
		final byte[] bData = (byte[]) bp.getPixels();
		Mat out = new Mat(h, w, opencv_core.CV_8UC1);
		out.put(0, 0, bData);
		return out;
	}
	
	public static Mat toMat(ColorProcessor cp) {
		final int w = cp.getWidth();
		final int h = cp.getHeight();
		final int[] iData = (int[]) cp.getPixels();
		
		Mat out = new Mat(h, w, opencv_core.CV_8UC3);
		byte[] bData = new byte[w * h * (int) out.elemSize()];
		for (int i = 0; i < iData.length; i++) {
			bData[i * 3 + 0] = (byte) ((iData[i] >> 16) & 0xFF);	// red
			bData[i * 3 + 1] = (byte) ((iData[i] >>  8) & 0xFF);	// grn
			bData[i * 3 + 2] = (byte) ((iData[i])       & 0xFF);	// blu
		}
		out.put(0, 0, bData);
		return out;
	}
	
	public static Mat toMat(ShortProcessor sp) {
		final int w = sp.getWidth();
		final int h = sp.getHeight();
		final short[] sData = (short[]) sp.getPixels();
		
		Mat out = new Mat(h, w, opencv_core.CV_16UC1);
		out.put(0, 0, sData);
		return out;
	}
	
	public static Mat toMat(FloatProcessor cp) {
		final int w = cp.getWidth();
		final int h = cp.getHeight();
		final float[] fData = (float[]) cp.getPixels();
		
		Mat out = new Mat(h, w, opencv_core.CV_32FC1);
		out.put(0, 0, fData);
		return out;
	}
}
