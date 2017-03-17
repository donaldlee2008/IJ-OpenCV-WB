package opencv_tests;

import static imagingbook.opencv.Convert.toImageProcessor;
import static imagingbook.opencv.Convert.toMat;

import org.bytedeco.javacpp.opencv_core.Mat;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;


/**
 * This version uses the OpenCV bridge at the ImageProcessor level
 * @author WB
 */
public class ImageProcessor_Test implements PlugInFilter {

	ImagePlus imp = null;
	
	@Override
	public int setup(String args, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip1) {
		Mat mat = toMat(ip1);
		
		ImageProcessor ip2 = toImageProcessor(mat);
		new ImagePlus("mat", ip2).show();
	}



}
