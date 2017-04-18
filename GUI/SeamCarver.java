/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seamcarver;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author howezhao
 */
public class SeamCarver {
    
    // The representation of the given image
    private int[][] color;
//    private int[][] recordSeams;
    // The energy of each pixel in the image
    private double[][] energy;
   
    // The current width and height
    private int w;
    private int h;
    
    boolean left = true;
    /**
     * Create a seam carver object based on the given picture.
     * 
     * @param picture the given picture
     * @throws NullPointerException if the given picture is {@code null}.
     */
    public SeamCarver(Picture picture) {
        if (picture == null) throw new java.lang.NullPointerException();
        
        // Initialize the dimensions of the picture
        w = picture.width();
        h = picture.height();
        
        // Store the picture's color information in an int array,
        // using the RGB coding described at:
        // http://docs.oracle.com/javase/8/docs/api/java/awt/Color.html#getRGB()
        color = new int[h][w];
       // recordSeams = new int[h][w];
        // Set the dimensions of the energy array
        energy = new double[h][w];
        
        // Store color information
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                color[i][j] = picture.get(j, i).getRGB();
                // recordSeams[i][j] = color[i][j];
            }
        }
        
        calcEnergy();
    }
    
    /**
     * Current picture.
     * 
     * @return the current picture.
     */
    public Picture getPicture() {
        
        // Create and return a new pic with the stored color information
        Picture pic = new Picture(width(), height());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                pic.set(j, i, new Color(color[i][j]));
            }
        }
        
        return new Picture(pic);
    }

    public int width() {
        return w;
    }
   
    public int height() {
        return h;
    }
    
    public Picture getGreyScalePicture() {

    	Picture pic = new Picture(width(), height());
    	for (int i = 0; i < energy.length; i++) {
    		for (int j = 0; j < energy[0].length; j++) {
    			 //int ori = (int)energy[i][j];
    			 int gray = ((int)energy[i][j] << 16) + ((int)energy[i][j] << 8) + (int)energy[i][j]; 
    			// String binaryString = Integer.toBinaryString(gray);
    			 pic.set(j, i, new Color(gray));
    		}
    	}
    	return pic;
    }
    /**
     * Energy of pixel at column x and row y.
     */
    public double energy(int x, int y) {        
        if (x >= width() || y >= height() || x < 0 || y < 0)
            throw new java.lang.IndexOutOfBoundsException();
        
        return energy[y][x];
    }
          
    private void calcEnergy() {
    	// calculate the energy array
        for (int i = 0; i < h; i++) { // height
            for (int j = 0; j < w; j++) { //width
                energy[i][j] = calcEnergy(j, i, "green");
            }
        }
    }
    
    private double calcEnergy(int x, int y) { 
    	// y is height, x is width
        if (x >= width() || y >= height() || x < 0 || y < 0)
            throw new java.lang.IndexOutOfBoundsException("index out of bounds");
                
        // Return 1000.0 for border pixels
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1)
            return (double) 1000;
        
        // Store pixel values in Color objects.
        Color up = new Color(color[y - 1][x]);
        Color down = new Color(color[y + 1][x]);
        Color left = new Color(color[y][x - 1]);
        Color right = new Color(color[y][x + 1]);
        
        return Math.sqrt(gradient(up, down) + gradient(left, right));
    }
    
    private double calcEnergy(int x, int y, String filterColor) {        
        if (x >= width() || y >= height() || x < 0 || y < 0)
            throw new java.lang.IndexOutOfBoundsException();
                
        // Return 1000.0 for border pixels
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1)
            return (double) 1000;
        if (filterColor == "green" && color[y][x] < Color.green.getRGB() + 20 && color[y][x] > Color.green.getRGB() - 20) {
        	return 0;
        }
        // Store pixel values in Color objects.
        Color up = new Color(color[y - 1][x]);
        Color down = new Color(color[y + 1][x]);
        Color left = new Color(color[y][x - 1]);
        Color right = new Color(color[y][x + 1]);
        
        return Math.sqrt(gradient(up, down) + gradient(left, right));
    }
    
    /**
     * Returns the gradient computed from the two Colors a and b
     */
    private double gradient(Color a, Color b) {
        return Math.pow(a.getRed() - b.getRed(), 2) +
               Math.pow(a.getBlue() - b.getBlue(), 2) +
               Math.pow(a.getGreen() - b.getGreen(), 2);
    }
    
    
    private double[][] copyArray(double[][] array) {
    	if (array == null || array.length == 0 || array[0].length == 0) {
    		throw new java.lang.IllegalArgumentException("copy array is null or empty");
    	}
    	
    	double[][] copy = new double[array.length][array[0].length];
    	for (int i = 0; i < array.length; i++) {
    		for (int j = 0; j < array[0].length; j++) {
    			copy[i][j] = array[i][j]; 
    		}
    	}
    	return copy;
    }
   
    private int findVerticalSeamBottomIdx(double[][] energyVertialSum) {
    	return findVerticalSeamBottomIdx(energyVertialSum, 0, 9);
    }
    
    // get vertical seam bottom idx from colums between [a,b)
    // O(w) w = width of the picture 
    public int findVerticalSeamBottomIdx(double[][] energyVertialSum, int a, int b) {
    	if (a < 0 || a > 8 || b < 1 || b > 10) {
    		throw new java.lang.IllegalArgumentException("colums should between [0, 9] inclusively"); 
    	}
    	if (a >= b) {
    		throw new java.lang.IllegalArgumentException("b should greater than a");
    	}
    	
    	double minValue = Double.POSITIVE_INFINITY;
    	int columRange = energyVertialSum[0].length / 10;
    	
        for (int i = a * columRange; i < b * columRange; i++) {
        	if (minValue> energyVertialSum[energyVertialSum.length - 1][i]) {
        		minValue = energyVertialSum[energyVertialSum.length - 1][i];
        	}
        }
        
        List<Integer> validIdx = new ArrayList<>();
        for (int i = a * columRange; i < b * columRange; i++) {
        	if (minValue + 0.01 >  energyVertialSum[energyVertialSum.length - 1][i]) {
        		validIdx.add(i);
        	}
        }
        
        Random rd = new Random();
        int radomNum = rd.nextInt(validIdx.size());
        
        return validIdx.get(radomNum);
    }
    
    
    /**
     * @return the sequence of indices for the vertical seam.
     */
    public int[] findVerticalSeam() {
    	double[][] energyVertialSum = copyArray(energy);

        // dynamic programming
    	energyVertialSum = calculateEnergyPathDp(energyVertialSum);
      
        //get the seam
        int idx = findVerticalSeamBottomIdx(energyVertialSum);
        int [] seam = findVerticalSeamFromBottom(energyVertialSum, idx);
        return seam;
    }
    
    // O(h*w) h = height, w = width
    public int[] findVerticalSeam(int a, int b) {
    	double[][] energyVertialSum = copyArray(energy);

        // dynamic programming
    	 // O(h*w) h = height, w = width
    	energyVertialSum = calculateEnergyPathDp(energyVertialSum);
    
        //get the seam
    	//O(w)
        int idx = findVerticalSeamBottomIdx(energyVertialSum, a, b);
        //O(h)
        int [] seam = findVerticalSeamFromBottom(energyVertialSum, idx);
        return seam;
    }
    
    // O(h*w) h = height, w = width
    private double[][] calculateEnergyPathDp(double[][] energyVertialSum) {
    	if (energyVertialSum == null) {
    		throw new java.lang.NullPointerException();
    	}
    	if (energyVertialSum.length == 0 || energyVertialSum[0].length == 0) {
    		throw new java.lang.IllegalArgumentException("input energyVertialSum is empty");
    	}
    	
    	for (int i = 1; i < energyVertialSum.length; i++) {
        	for (int j = 0; j < energyVertialSum[0].length; j++) {
        		double min = Double.POSITIVE_INFINITY;
        		if (j > 0) {
        			min = Math.min(min, energyVertialSum[i - 1][j - 1]);
        		}	
        		if (j < energyVertialSum[0].length - 1) {
        			min = Math.min(min, energyVertialSum[i - 1][j + 1]);
        		}    
        		min = Math.min(min, energyVertialSum[i - 1][j]);
        		energyVertialSum[i][j] += min;
        		
        	}	
        }  		
    	return energyVertialSum;
    }
    //get seam pixel position from top to bottom
    //O(h), h = height of the picture
    private int[] findVerticalSeamFromBottom(double[][] energyVertialSum, int minIdx) {
    	int[] seam = new int[height()];
    	double[] seamValue= new double[height()];
        for (int i = energyVertialSum.length - 1; i >= 0; i--) {
        	seam[i] = minIdx; // put index with minimum energy value to array
        	if (i == 0) {
        		break;
        	}
        	int temp = minIdx;
        	double minValue= Double.POSITIVE_INFINITY;
        	
        	if (minValue >= energyVertialSum[i - 1][temp]) {
        		minIdx = temp;
        		minValue = energyVertialSum[i - 1][temp];
        		seamValue[i] = minValue;
        	}
        	
        	if (temp > 0) {   //check boundary
        		if (minValue > energyVertialSum[i - 1][temp - 1]) {
        			minIdx = temp - 1;
        			minValue = energyVertialSum[i - 1][temp - 1];
        			seamValue[i] = minValue;
        		}
        	}
        	
        	if (temp < energyVertialSum[0].length - 1) {
        		if (minValue > energyVertialSum[i - 1][temp + 1]) {
        			minIdx = temp + 1;
        			minValue = energyVertialSum[i - 1][temp + 1];
        			seamValue[i] = minValue;
        		}
        	}
        	
        }
        
        return seam;
    }
    
    /**
     * Remove vertical seam from current picture.
     */
    public void removeVerticalSeam(int[] seam) {
        
        // Check for bad input
        if (width() <= 1) {
        	throw new java.lang.IllegalArgumentException("Picture too narrow");
        }
        if (seam == null) {
        	throw new java.lang.NullPointerException();
        }
        if (seam.length != height()) {
        	throw new java.lang.IllegalArgumentException("Invalid seam length");
        }
        
        // Create replacement arrays
        int[][] newColor = new int[height()][width() - 1];
        double[][] newEnergy = new double[height()][width() - 1];
        
        // Populate replacement arrays, skipping pixels in the seam
        for (int i = 0; i < height(); i++) {
            int s = seam[i];
            
            for (int j = 0; j < s; j++) {
                newColor[i][j] = color[i][j];
                newEnergy[i][j] = energy[i][j];
            }
            
            for (int j = s + 1; j < width(); j++) {
                newColor[i][j - 1] = color[i][j];
                newEnergy[i][j - 1] = energy[i][j];
            }
        }
        
        color = newColor;
        energy = newEnergy;
        w--;
        
        // Recalculate the energy along the seam
        for (int i = 0; i < height(); i++) {
            int s = seam[i];
            
            // Left edge removed
            if (s == 0) {
                energy[i][s] = calcEnergy(s, i);
            }
            
            // Right edge removed
            else if (s == width()) {
                energy[i][s - 1] = calcEnergy(s - 1, i);
            }
            
            // Middle pixel removed
            else {
                energy[i][s] = calcEnergy(s, i);
                energy[i][s - 1] = calcEnergy(s - 1, i);
            }
        }        
    }
    
    public Picture getPicture(int[][] pictureColors) {
    	// Create and return a new pic with the stored color information
        Picture pic = new Picture(pictureColors[0].length, pictureColors.length);
        for (int i = 0; i < pictureColors.length; i++) {
            for (int j = 0; j < pictureColors[i].length; j++) {
                pic.set(j, i, new Color(pictureColors[i][j]));
            }
        }
        return new Picture(pic);
    }
    
    public void CarvingOnce() {
    	int[] seam = this.findVerticalSeam();
    	this.removeVerticalSeam(seam);
    }
    
    public void CarvingOnce(int a, int b) {
    	int[] seam = this.findVerticalSeam(a, b);
    	this.removeVerticalSeam(seam);
    }  
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
              
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SeamCarverGUI().setVisible(true);
            }
        });
        
        System.out.println("main function start");
    }	
}