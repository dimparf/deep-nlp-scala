package de.hpi.anlp.ann

import org.scalaml.core.Types.ScalaMl.DblVector
import org.scalaml.util.FormatUtils

		/**
		 * <p>:Class that defines a MLP layer. A MLP layer is built using the
		 * input vector and add an extra element (or neuron) to account for the intercept
		 * weight w0. The MLP layer is fully defined by its rank in the Neuron Network with
		 * input layer having id = 0 and the output layer having id = number of layers -1.</p>
		 * @constructor Create a layer for a multi-layer perceptron. 
		 * @throws IllegalArgumentException if the class parameters are incorrect
		 * @param id Identifier or rank of the MLP layer in the network.
		 * @param len Number of elements or neuron in the MLP layer.
		 * 
		 * @author Patrick Nicolas
		 * @since May 6, 2014
		 * @note Scala for Machine Learning Chapter 9 Artificial Neural Network / Multilayer perceptron 
		 * / Model definition
		 */
final protected class MLPLayer(val id: Int, val len: Int) {
	import de.hpi.anlp.ann.MLPLayer._
	check(id, len)

		/**
		 * Values of the output vector (Output layer). It is used in
		 * forward propagation.
		 */
	val output = new DblVector(len) 
	
		/**
		 * Difference for the propagated error on the source or upstream
		 * layer
		 */
	val delta = new DblVector(len)  // used for back propagation
	output.update(0, 1.0)

		/**
		 * <p>Initialize the value of the input for this MLP layer.</p>
		 * @param _x input vector for this layer.
		 * @throws IllegalArgumentException if the input vector is undefined
		 */
	def set(_x: DblVector): Unit = {
		require( !_x.isEmpty, 
				s"MLPLayer.set Cannot initialize this MLP layer $id with undefined data")
		_x.copyToArray(output, 1)
	}

		/**
		 * <p>Compute the sum of squared error of the neurons/elements of this MLP layer.
		 * The SSE value is divided by 2 in the normalized C-formulation.</p>
		 * @param labels target output value
		 * @return sum of squared of errors/2
		 * @throws IllegalArgumentException if the size of the output vector is not equals to the 
		 * size of the input vector + 1
		 */
	final def sse(labels: DblVector): Double = {
		require( !labels.isEmpty, 
				"MLPLayer.sse Cannot compute the sum of squared errors with undefined labels")
		require(output.size == labels.size+1, 
				s"MLPLayer.sse The size of the output ${output.size} != to size of target ${labels.size+1}")
  	
			// Create a indexed vector of the output minus the first
			// element (bias element +1). Then compute the sum of squared
			// errors
		var _sse = 0.0
		output.drop(1).zipWithIndex.foreach(on => {
			val err = labels(on._2) - on._1
			delta.update(on._2+1, on._1* (1.0- on._1)*err)
			_sse += err*err
		})
		_sse*0.5	// Note that the normalized version of sse is divided by 2
	}
   
		/**
		 * <p>Test if this neural network layer is the output layer (last layer in the network).</p>
		 * @param lastId id of the output layer in this neural network
		 * @return true if this layer is the output layer, false, otherwise
		 */
	@inline
	final def isOutput(lastId: Int): Boolean = id == lastId

			/**
		 * Textual and formatted description of a layer in the Multi-layer perceptron
		 */
	override def toString: String = {
		val buf = new StringBuilder
		
		buf.append(s"\nLayer: $id output: ")
		output.foreach(x => buf.append(s"${FormatUtils.format(x,"", FormatUtils.ShortFormat)}"))
		buf.toString.substring(0, buf.length-1)
	}
}

		/**
		 * Companion object for the MLP layer used to define a default constructor
		 * and validate its input parameters
		 * @author Patrick Nicolas
		 * @note Scala for Machine Learning Chapter 9 Artificial Neural Network / Multilayer perceptron 
		 * / Model definition
		 */
object MLPLayer {
		/**
		 * Default constructor for MLPLayer
		 * @param id Identifier or rank of the MLP layer in the network.
		 * @param len Number of elements or neuron in the MLP layer.
		 */
	def apply(id: Int, len: Int): MLPLayer = new MLPLayer(id, len)
	
	private def check(id: Int, len: Int): Unit = {
		require(id >= 0, s"MLPLayer Create a MLP layer with incorrect id: $id")
		require(len > 0, s"MLPLayer Create a MLP layer with incorrect length $len")
	}
}


// -------------------------------------  EOF ------------------------------------------------