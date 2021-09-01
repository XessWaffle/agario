package network;

import java.util.ArrayList;
import java.util.Random;

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

public class ExtremeLearningMachine extends NeuralNetwork{

	
	public ExtremeLearningMachine(int[] vect) {
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
		Random ran = new Random();
		ArrayList<NeuralLayer> all = new ArrayList<>();
		
		all.add(new NeuralLayer(this.getInputs()));
		all.addAll(this.getHiddenLayers());
		all.add(new NeuralLayer(this.getOutputs()));
		
		for(int i = 0; i < all.size() - 1; i++) {
			for(Neuron toConnect: all.get(i).getAllNeurons()) {
				int numConn = ran.nextInt(8) + 1;
				
				do {
					
					int layer = ran.nextInt(all.size());
					
					int neuron = ran.nextInt(all.get(layer).getAllNeurons().size());
					
					if(layer >= i) {
						if(!toConnect.hasConnectionFrom(all.get(layer).getNeuronAt(neuron)) && !Neuron.equals(toConnect, all.get(layer).getNeuronAt(neuron)) 
								&& !toConnect.hasConnectionTo(all.get(layer).getNeuronAt(neuron))) {
							
							

							Connection.connect(all.get(layer).getNeuronAt(neuron), toConnect);
							
							numConn--;
						}
					} else {
						if(!toConnect.hasConnectionTo(all.get(layer).getNeuronAt(neuron)) && !Neuron.equals(toConnect, all.get(layer).getNeuronAt(neuron)) 
								&& !toConnect.hasConnectionFrom(all.get(layer).getNeuronAt(neuron))) {
							
							Connection.connect(toConnect, all.get(layer).getNeuronAt(neuron));
							
							numConn--;
						}
					}
					
					
				} while(numConn > 0);
				
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
			int layer = 0;
			
			int neuron = 0;
			
			do {
				layer = (int)(Math.random() * this.getHiddenLayers().size());
				neuron = (int)(Math.random() * this.getHiddenLayers().get(layer).getAllNeurons().size());
			} while(neuron <= 0 && layer <= 0);
			
			
			if(Math.random() > 0.5) {
				
				if(this.getHiddenLayers().get(layer).getNeuronAt(neuron).getForwardPropConnections().size() > 0) {
					int link = (int)(Math.random() * this.getHiddenLayers().get(layer).getNeuronAt(neuron).getForwardPropConnections().size());
					this.getHiddenLayers().get(layer).getNeuronAt(neuron).getForwardPropConnections().get(link).setWeight(Math.random() - 0.5);
				}
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
