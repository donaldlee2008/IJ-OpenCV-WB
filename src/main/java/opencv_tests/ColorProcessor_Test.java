package opencv_tests;

import static imagingbook.opencv.Convert.toImageProcessor;
import static imagingbook.opencv.Convert.toMat;

import org.bytedeco.javacpp.opencv_core.Mat;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;




/**
 * This version uses the OpenCV bridge at the ImageProcessor level
 * @author WB
 */
public class ColorProcessor_Test implements PlugInFilter {

	ImagePlus imp = null;
	
	@Override
	public int setup(String args, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		ColorProcessor bp = (ColorProcessor) ip;
		Mat mat = toMat(bp);
		
		ImageProcessor ip2 = toImageProcessor(mat);
		new ImagePlus("mat",ip2).show();

	}



}
