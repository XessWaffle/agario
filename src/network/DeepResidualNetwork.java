package network;

import java.util.ArrayList;

import core.Cell;
import core.Connection;
import core.NeuralLayer;
import core.NeuralLink;
import core.NeuralNetwork;
import core.Neuron;
import coreutils.Vector;
import error.MeanSquaredError;
import learning.Backpropagation;
import neuron.InputNeuron;
import neuron.OutputNeuron;


public class DeepResidualNetwork extends NeuralNetwork{

	public static final int LAYER_DISTANCE = 2;
	
	public DeepResidualNetwork(int[] vect) {
		super();
		
		System.out.println(vect[0]);
		
		for(int i = 0; i < vect[0]; i++) {
			this.addInput(new InputNeuron());
		}
		
		System.out.println(this.getInputs().size());
		
		for(int i = 1; i < vect.length - 1; i++) {
			this.hiddenLayers.add(new NeuralLayer());
			this.hiddenLayers.get(i - 1).addNeuron(vect[i]);
		}
		
		for(int i = 0; i < vect[vect.length - 1]; i++) {
			this.addOutput(new OutputNeuron());
		}
		
		this.rule = new Backpropagation(this);
		this.calc = new MeanSquaredError();
		
		initalize();
		
	}
	
	@Override
	public void initalize() {
		// TODO Auto-generated method stub
		this.outputBuffer = new double[output.size()];
		
		connect();
		
		super.initalize();
	}

	@Override
	public void connect() {
		// TODO Auto-generated method stub
		ArrayList<NeuralLayer> all = new ArrayList<>();
		
		all.add(new NeuralLayer(this.getInputs()));
		all.addAll(this.getHiddenLayers());
		all.add(new NeuralLayer(this.getOutputs()));
		
		
		for(int i = 1; i < all.size(); i++) {
			Connection.connect(all.get(i - 1), all.get(i));
		}
		
		for(int i = 0; i < all.size(); i+= LAYER_DISTANCE) {
			
			if(i + LAYER_DISTANCE < all.size()) {
				Connection.connect(all.get(i).getFirstNeuron(), all.get(i + LAYER_DISTANCE).getFirstNeuron());
				//Connection.connect(this.getHiddenLayers().get(i - LAYER_DISTANCE).getLastNeuron(), this.getHiddenLayers().get(i).getLastNeuron());
			}	
		}
		
		for(NeuralLayer get: all) {
			for(Neuron n: get.getAllNeurons()) {
				for(NeuralLink links: n.getForwardPropConnections()) {
					System.out.println(n.getLabel() + "-" + links.getFromNeuron().getLabel());
				}
			}
		}
		
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(NeuralLayer toCalc: this.getHiddenLayers()) {
			toCalc.calculate();
		}
		
		int ind = 0;
		
		for(Neuron out: this.getOutputs()) {
			out.calculate();
			
			outputBuffer[ind++] = out.getNetOutput();
		}
		
		this.rule.computeAndPush(error);
	}
	
	public Vector getNextVect(double[] inputs) {
		
		
		int i = 0;
		
		for(Neuron in: this.input) {
			
			//System.out.println("DUB: " + inputs[i]);
		
			
			((InputNeuron) in).setNetOutput(inputs[i++]);
			
			
			((InputNeuron) in).calculate();
		}
		
		this.run();
		
		//System.out.println("OUT:" + outputBuffer[0] + ", " + outputBuffer[1]);
		

		if(Math.random() > 0.8) {
			int layer = (int)(Math.random() * this.getHiddenLayers().size());
			int neuron = (int)(Math.random() * this.getHiddenLayers().get(layer).getAllNeurons().size());
		
			if(Math.random() > 0.5) {
				int link = (int)(Math.random() * this.getHiddenLayers().get(layer).getNeuronAt(neuron).getForwardPropConnections().size());
				this.getHiddenLayers().get(layer).getNeuronAt(neuron).getForwardPropConnections().get(link).setWeight(Math.random() - 0.5);
			} else {
				this.getHiddenLayers().get(layer).getNeuronAt(neuron).setBias(Math.random() - 0.5);
			}
		
		}
		
		
		
		Vector toRet = new Vector((int)(outputBuffer[0] * Cell.MAX_SPD * 2), (int)(outputBuffer[1] * Cell.MAX_SPD * 2));
		return toRet;
	}
	
	public double doLaunch() {
		return outputBuffer[2];
	}
	
	
	public void update(){
		this.rule.adjust();
	}
}
