package imagingbook.opencv;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.ShortPointer;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;

import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;


/**
 * This class defines methods for converting ImageJ images (image processors)
 * to and from OpenCV images (matrices). It is based on the bytedeco.javacpp
 * Java wrapper of OpenCV. 
 * See also http://docs.opencv.org/modules/core/doc/basic_structures.html#mat
 * Usage example:
 * <pre>
 	ImageProcessor ip1 = ...;	// original ImageJ image
	Mat mat = toMat(ip1);		// copy as OpenCV image
	ImageProcessor ip2 = toImageProcessor(mat); // converted back to ImageJ
 * </pre>
 * 
 * TODO: Check memory allocation, garbage issue...
 * 
 * @author W. Burger
 * @version 2017/03/17
 */
public abstract class Convert {
// --------------------------------------------------------------------
				// OpenCV -> ImageJ (Mat -> ImagePlus)
				// --------------------------------------------------------------------
				
		public static ImagePlus toImagePlus(Mat mat) {
			final int type = mat.type();
			ImageProcessor result = null;
			
			if (type == opencv_core.CV_8UC1) { // type = BufferedImage.TYPE_BYTE_GRAY;
				result = makeByteProcessor(mat);
			}
			else if (type == opencv_core.CV_8UC3) {	// type = BufferedImage.TYPE_3BYTE_BGR;
				result =  makeColorProcessor(mat); // faulty 
			}
			else if (type == opencv_core.CV_16UC1) {	// signed short image
				result =  makeShortProcessor(mat); 
			}
			else if (type == opencv_core.CV_32FC1) {	// float image
				result =  makeFloatProcessor(mat); 
			}
			else {
				throw new IllegalArgumentException("cannot convert Mat of type " + type);
			}	
			return new ImagePlus("",result);
		}
	// --------------------------------------------------------------------
	// OpenCV -> ImageJ (Mat -> ImageProcessor)
	// --------------------------------------------------------------------
	
	public static ImageProcessor toImageProcessor(Mat mat) {
		final int type = mat.type();
		ImageProcessor result = null;
		
		if (type == opencv_core.CV_8UC1) { // type = BufferedImage.TYPE_BYTE_GRAY;
			result = makeByteProcessor(mat);
		}
		else if (type == opencv_core.CV_8UC3) {	// type = BufferedImage.TYPE_3BYTE_BGR;
			result =  makeColorProcessor(mat); // faulty 
		}
		else if (type == opencv_core.CV_16UC1) {	// signed short image
			result =  makeShortProcessor(mat); 
		}
		else if (type == opencv_core.CV_32FC1) {	// float image
			result =  makeFloatProcessor(mat); 
		}
		else {
			throw new IllegalArgumentException("cannot convert Mat of type " + type);
		}	
		return result;
	}
	
	// private methods ----------------------------------------------
	
	private static ByteProcessor makeByteProcessor(Mat mat) {
		if (mat.type() != opencv_core.CV_8UC1)
			throw new IllegalArgumentException("wrong Mat type: " + mat.type());
		final int w = mat.cols();
		final int h = mat.rows();
		ByteProcessor bp = new ByteProcessor(w, h);
		mat.data().get((byte[]) bp.getPixels());
		return bp;
	}
	
	private static ShortProcessor makeShortProcessor(Mat mat) {
		if (mat.type() != opencv_core.CV_16UC1)
			throw new IllegalArgumentException("wrong Mat type: " + mat.type());
		final int w = mat.cols();
		final int h = mat.rows();
		ShortProcessor sp = new ShortProcessor(w, h);
		ShortPointer sptr = new ShortPointer(mat.data());
		sptr.get((short[]) sp.getPixels());
		sptr.close();
		return sp;
	}
	
	private static FloatProcessor makeFloatProcessor(Mat mat) {
		if (mat.type() != opencv_core.CV_32FC1)
			throw new IllegalArgumentException("wrong Mat type: " + mat.type());
		final int w = mat.cols();
		final int h = mat.rows();
		FloatProcessor fp = new FloatProcessor(w, h);
		FloatPointer fptr = new FloatPointer(mat.data());
		fptr.get((float[]) fp.getPixels());
		fptr.close();
		return fp;
	}
	
	private static ColorProcessor makeColorProcessor(Mat mat) {
		if (mat.type() != opencv_core.CV_8UC3)
			throw new IllegalArgumentException("wrong Mat type: " + mat.type());
		final int w = mat.cols();
		final int h = mat.rows();	
		byte[] pixels = new byte[w * h * (int) mat.elemSize()];
		mat.data().get(pixels);
		// convert byte array to int-encoded RGB values
		ColorProcessor cp = new ColorProcessor(w, h);
		int[] iData = (int[]) cp.getPixels();
		for (int i = 0; i < w * h; i++) {
							//opencv mat bgr  java bufferimage rgb
//				int red = pixels[i * 3 + 0] & 0xff;
//				int grn = pixels[i * 3 + 1] & 0xff;
//				int blu = pixels[i * 3 + 2] & 0xff;
				int blu = pixels[i * 3 + 0] & 0xff;
				int grn = pixels[i * 3 + 1] & 0xff;
				int red= pixels[i * 3 + 2] & 0xff;
			iData[i] = (red << 16) | (grn << 8) | blu;
		}
		return cp;
	}
	
	// --------------------------------------------------------------------
	// ImageJ -> OpenCV (ImageProcessor -> Mat)
	// --------------------------------------------------------------------
	
	/**
	 * Dispatcher method. Duplicates {@link ImageProcessor} to the corresponding
	 * OpenCV image of type  {@link Mat}.
	 * TODO: Could be coded more elegantly ;-)
	 * 
	 * @param ip The {@link ImageProcessor} to be converted
	 * @return The OpenCV image (of type {@link Mat}) 
	 */
	public static Mat toMat(ImageProcessor ip) {
		Mat mat = null;
		if (ip instanceof ByteProcessor) {
			mat = toMat((ByteProcessor) ip);
		}
		else if (ip instanceof ColorProcessor) {
			mat = toMat((ColorProcessor) ip);
		}
		else if (ip instanceof ShortProcessor) {
			mat = toMat((ShortProcessor) ip);
		}
		else if (ip instanceof FloatProcessor) {
			mat = toMat((FloatProcessor) ip);
		}
		else {
			throw new IllegalArgumentException("cannot convert to Mat: " + ip);
		}
		return mat;
	}
	
	/**
	 * Duplicates {@link ByteProcessor} to the corresponding OpenCV image
	 * of type  {@link Mat}.
	 * @param bp The {@link ByteProcessor} to be converted
	 * @return The OpenCV image (of type {@link Mat}) 
	 */
	public static Mat toMat(ByteProcessor bp) {
		final int w = bp.getWidth();
		final int h = bp.getHeight();
		final byte[] pixels = (byte[]) bp.getPixels();
		
		// version A - copies the pixel data to a new array
//		Size size = new Size(w, h);
//		Mat mat = new Mat(size, opencv_core.CV_8UC1);
//		mat.data().put(bData);

		// version 2 - reuses the existing pixel array
		return new Mat(h, w, opencv_core.CV_8UC1, new BytePointer(pixels));
	}
	
	/**
	 * Duplicates {@link ShortProcessor} to the corresponding OpenCV image
	 * of type  {@link Mat}.
	 * @param bp The {@link ShortProcessor} to be converted
	 * @return The OpenCV image (of type {@link Mat}) 
	 */
	public static Mat toMat(ShortProcessor sp) {
		final int w = sp.getWidth();
		final int h = sp.getHeight();
		final short[] pixels = (short[]) sp.getPixels();
		return new Mat(h, w, opencv_core.CV_16UC1, new ShortPointer(pixels));
	}
	
	/**
	 * Duplicates {@link FloatProcessor} to the corresponding OpenCV image
	 * of type  {@link Mat}.
	 * @param bp The {@link FloatProcessor} to be converted
	 * @return The OpenCV image (of type {@link Mat}) 
	 */
	public static Mat toMat(FloatProcessor cp) {
		final int w = cp.getWidth();
		final int h = cp.getHeight();
		final float[] pixels = (float[]) cp.getPixels();
		return new Mat(h, w, opencv_core.CV_32FC1, new FloatPointer(pixels));
	}
	
	/**
	 * Duplicates {@link ColorProcessor} to the corresponding OpenCV image
	 * of type  {@link Mat}.
	 * @param bp The {@link ColorProcessor} to be converted
	 * @return The OpenCV image (of type {@link Mat}) 
	 */
	public static Mat toMat(ColorProcessor cp) {
		final int w = cp.getWidth();
		final int h = cp.getHeight();
		final int[] pixels = (int[]) cp.getPixels();
		byte[] bData = new byte[w * h * 3];
		
		//opencv mat bgr  java bufferimage rgb
			// convert int-encoded RGB values to byte array
			for (int i = 0; i < pixels.length; i++) {
//				bData[i * 3 + 0] = (byte) ((pixels[i] >> 16) & 0xFF);	// red
//				bData[i * 3 + 1] = (byte) ((pixels[i] >>  8) & 0xFF);	// grn
//				bData[i * 3 + 2] = (byte) ((pixels[i])       & 0xFF);	// blu
				bData[i * 3 + 2] = (byte) ((pixels[i] >> 16) & 0xFF);	// blu
				bData[i * 3 + 1] = (byte) ((pixels[i] >>  8) & 0xFF);	// grn
				bData[i * 3 + 0] = (byte) ((pixels[i])       & 0xFF);	// red
		}
		return new Mat(h, w, opencv_core.CV_8UC3, new BytePointer(bData));
	}
}
