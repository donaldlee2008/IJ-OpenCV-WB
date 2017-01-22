package ijopencv.opencvwb;

import java.awt.image.BufferedImage;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import ij.ImagePlus;

public class MatImagePlusConverterWB {

	public ImagePlus convert(Mat mat) {
		// Converter to OpenCV Mat
		OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
		// Converter ImageJ image to Frame
		Java2DFrameConverter converterToFrame = new Java2DFrameConverter();

		Frame frame = converterToMat.convert(mat);
		BufferedImage bf = converterToFrame.convert(frame);

		return new ImagePlus("image", bf);
	}
}
