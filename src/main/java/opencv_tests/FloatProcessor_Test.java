package opencv_tests;

import static imagingbook.opencv.Convert.toImageProcessor;
import static imagingbook.opencv.Convert.toMat;

import org.bytedeco.javacpp.opencv_core.Mat;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;




/**
 * This version uses the OpenCV bridge at the ImageProcessor level
 * @author WB
 */
public class FloatProcessor_Test implements PlugInFilter {

	ImagePlus imp = null;
	
	@Override
	public int setup(String args, ImagePlus imp) {
		this.imp = imp;
		return DOES_32 + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		FloatProcessor fp = (FloatProcessor) ip;
		Mat mat = toMat(fp);
		
		ImageProcessor ip2 = toImageProcessor(mat);
		new ImagePlus("mat",ip2).show();

	}



}
