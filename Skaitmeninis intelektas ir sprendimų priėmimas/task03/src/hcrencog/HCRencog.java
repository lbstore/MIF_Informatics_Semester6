/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hcrencog;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;

/**
 *
 * @author Lemmin
 */
public class HCRencog {

    public static class DataSet{
        public double[] data;
        public double[] expected;
        
    }
    public static double[][] parsePicture(String path,boolean monochrome) throws IOException{
        BufferedImage image = ImageIO.read(new File(path));
        int h = image.getHeight();
        int w = image.getWidth();
        double[][] pixels = new double[h][w];

        for (int x = 0; x < h ; x++) {
            for (int y = 0; y < w; y++) {
                if(!monochrome){
                    pixels[x][y] = (double) (image.getRGB(y, x)); 
                }else{
                    pixels[x][y] = (double) (image.getRGB(y, x) == 0xFFFFFFFF ? 0 : 1);

                }
                
            }
        }
        return pixels;
    }
    public static DataSet getInputDataset(String path,double[] expected) throws IOException{
        DataSet set = new DataSet();
        double[][] array = parsePicture(path,true);
        ArrayList<Double> list = new ArrayList<>();
        for( int i=0; i<array.length; i++){
            for(double n:array[i]){
                list.add(n);
            }
        }
        set.expected = expected;
        set.data = new double[list.size()];
        for(int i=0; i<set.data.length; i++){
            set.data[i] = list.get(i);
        }
        return set;
    }
    public static DataSet getInputDatasetWrap(String path,Number expected) throws IOException{
        return getInputDataset(path,new double[]{expected.doubleValue()});
    }
    
    public static BasicMLDataSet formatInput(DataSet[] array){
        double[][] input = new double[array.length][array[0].data.length];
        double[][] ideal = new double[array.length][array[0].expected.length];
        for(int i=0; i<array.length; i++){
            input[i] = array[i].data;
            ideal[i] = array[i].expected;
        }
        return new BasicMLDataSet(input,ideal);
    }
    

    /**
     * The main method.
     * @param args No arguments are used.
     */
    public static void main(final String args[]) throws IOException {
            
        String home = "data/";
        DataSet[] inputs = {
            getInputDatasetWrap(home + "A1.bmp",0),
            getInputDatasetWrap(home + "A2.bmp",0),
            getInputDatasetWrap(home + "B1.bmp",0.3),
            getInputDatasetWrap(home + "B2.bmp",0.3),
            getInputDatasetWrap(home + "C1.bmp",0.6),
            getInputDatasetWrap(home + "C2.bmp",0.6)
        };

        
        
        
        DataSet test = getInputDataset(home + "test.bmp",null);
        
        // create a neural network, without using a factory
        BasicNetwork network = new BasicNetwork();
        
        network.addLayer(new BasicLayer(null,true,inputs[0].data.length));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,25));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,25));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),true,15));
        network.addLayer(new BasicLayer(new ActivationSigmoid(),false,1));
        network.getStructure().finalizeStructure();
        network.reset();

        // create training data
        MLDataSet trainingSet = formatInput(inputs);
        
        // train the neural network
        final Backpropagation train = new Backpropagation(network, trainingSet);
        
        int epoch = 1;

        do {
                train.iteration();
                double error = train.getError();
                System.out.println("Epoch #" + epoch + " Error:" + error);
                epoch++;
                if(error<0.0005){
                    break;
                }
        } while(epoch<1000);
        train.finishTraining();

        // test the neural network
        System.out.println("Neural Network Results:");
        double[] out = new double[1];
        network.compute(test.data, out);
        System.out.println(Arrays.toString(out));
        
//        for(MLDataPair pair: trainingSet ) {
//                final MLData output = network.compute(pair.getInput());
//                System.out.println(output.getData(0));
//        }

        Encog.getInstance().shutdown();
    }
    
}
