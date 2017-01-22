package ijopencv_examples;

import org.bytedeco.javacpp.opencv_core.Mat;

import ij.IJ;
import ij.plugin.PlugIn;

public class _Test_Installation implements PlugIn {

	@Override
	public void run(String arg0) {
		IJ.log("Starting OpenCV Test ...");
		
		IJ.log("Checking opencv class ...");
		Class<?> clazz = org.bytedeco.javacpp.opencv_core.class;
		IJ.log("class loader= " + clazz.getClassLoader().toString());
//		try {
//			clazz = Class.forName("org.bytedeco.javacpp.opencv_core");
//		} catch (ClassNotFoundException e) { }
//		if (clazz == null) {
//			IJ.log("class org.bytedeco.javacpp.opencv_core not found!");
//			return;
//		}
		
		
		IJ.log("class org.bytedeco.javacpp.opencv_core found!");
		
		String version = org.bytedeco.javacpp.opencv_core.CV_VERSION;
		IJ.log("OpenCV version = " + version);
		IJ.log("Number of CPUs = " + org.bytedeco.javacpp.opencv_core.getNumberOfCPUs());
//		IJ.log("Build Info = " + 
//				new String(org.bytedeco.javacpp.opencv_core.getBuildInformation().getStringBytes()));
		
		Mat mat = Mat.eye(3, 3, org.bytedeco.javacpp.opencv_core.CV_8UC1).asMat();
		IJ.log("mat = " + mat.toString());
	}

}
