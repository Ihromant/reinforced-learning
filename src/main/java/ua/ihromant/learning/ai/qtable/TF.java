package ua.ihromant.learning.ai.qtable;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.tensorflow;

public class TF {
	public void buildGraph() {
		tensorflow.Placeholder holder = new tensorflow.Placeholder(new tensorflow.Scope(new FloatPointer(0.0f, 2.0f,
				3.0f,	3.0f)), tensorflow.DT_FLOAT);
		holder = new tensorflow.Placeholder(new DoublePointer(0.0, 2.0, 3.0, 3.0));
		//tensorflow.Transpose
	}
}
