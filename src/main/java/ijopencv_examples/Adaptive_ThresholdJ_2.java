package ijopencv_examples;

import static org.bytedeco.javacpp.opencv_imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.bytedeco.javacpp.opencv_imgproc.ADAPTIVE_THRESH_MEAN_C;
import static org.bytedeco.javacpp.opencv_imgproc.THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.THRESH_BINARY_INV;
import static org.bytedeco.javacpp.opencv_imgproc.adaptiveThreshold;

import org.bytedeco.javacpp.opencv_core.Mat;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ijopencv.ijwb.ImagePlusMatConverterWB;
import ijopencv.opencvwb.MatImagePlusConverterWB;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jonathan
 */
public class Adaptive_ThresholdJ_2 implements PlugInFilter {

	String method;
	String thresholdmethod;
	int maxValue = 255;
	int blockSize = 3;

	ImagePlus imp = null;
	
	@Override
	public int setup(String args, ImagePlus imp) {
		this.imp = imp;
		return DOES_ALL + NO_CHANGES;
	}
	
	@Override
	public void run(ImageProcessor ip) {
		//ImagePlus imp = IJ.getImage();

		if (!showDialog()) {
			return;
		}

		// Converter
		IJ.log("making ImagePlusMatConverterWB");
		ImagePlusMatConverterWB ic = new ImagePlusMatConverterWB();
		IJ.log("making MatImagePlusConverterWB");
		MatImagePlusConverterWB mip = new MatImagePlusConverterWB();

		IJ.log("convert");
		Mat m = ic.convert(imp);
		// opencv_imgproc.cvtColor(m, m, opencv_imgproc.COLOR_BGR2GRAY);
		IJ.log("new Mat");
		Mat res = new Mat();

		int adaptiveMethod;
		if (method.equals("Mean")) {
			adaptiveMethod = ADAPTIVE_THRESH_MEAN_C;
		} else {
			adaptiveMethod = ADAPTIVE_THRESH_GAUSSIAN_C;
		}

		int thresType;
		if (thresholdmethod.equals("Binary")) {
			thresType = THRESH_BINARY;
		} else {
			thresType = THRESH_BINARY_INV;
		}

		IJ.log("adaptiveThreshold");
		adaptiveThreshold(m, res, maxValue, adaptiveMethod, thresType, blockSize, 2);

		IJ.log("convert back");
		ImagePlus imp2 = mip.convert(res);
		imp2.show();

//		m.close();
//		res.close();
	}

	private boolean showDialog() {
		GenericDialog gd = new GenericDialog("Adaptive Threshold");
		String[] items = { "Mean", "Gaussian" };
		String[] itemsThreshold = { "Binary", "Binary Inv" };
		gd.addChoice("Method", items, items[0]);
		gd.addChoice("Threshold Type", itemsThreshold, itemsThreshold[0]);
		gd.addNumericField("Max Value", maxValue, 0);
		gd.addNumericField("Block size", blockSize, 0);
		// gd.addNumericField("Gaussian Kernel width:", gaussianKernelWidth, 0);

		gd.showDialog();
		if (gd.wasCanceled()) {
			return false;
		}

		method = gd.getNextChoice();
		thresholdmethod = gd.getNextChoice();
		maxValue = (int) gd.getNextNumber();
		blockSize = (int) gd.getNextNumber();
		return true;
	}



}
