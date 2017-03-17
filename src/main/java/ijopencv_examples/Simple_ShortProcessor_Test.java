package ijopencv_examples;

import static imagingbook.opencv.Convert.toImageProcessor;
import static imagingbook.opencv.Convert.toMat;

import org.bytedeco.javacpp.opencv_core.Mat;

import ij.ImagePlus;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;



/**
 * This version uses the OpenCV bridge at the ImageProcessor level
 * @author WB
 */
public class Simple_ShortProcessor_Test implements PlugInFilter {

	ImagePlus imp = null;
	
	@Override
	public int setup(String args, ImagePlus imp) {
		this.imp = imp;
		return DOES_16 + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		ShortProcessor sp = (ShortProcessor) ip;
		Mat mat = toMat(sp);
		
		ImageProcessor ip2 = toImageProcessor(mat);
		new ImagePlus("mat",ip2).show();

	}



}
