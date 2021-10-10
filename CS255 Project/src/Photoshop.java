//Name: Keiran Hughes
//Student Number: 913861
/*
CS-255 Getting started code for the assignment
I do not give you permission to post this code online
Do not post your solution online
Do not copy code
Do not use JavaFX functions or other libraries to do the main parts of the assignment:
	Gamma Correction
	Contrast Stretching
	Histogram calculation and equalisation
	Cross correlation
All of those functions must be written by yourself
You may use libraries to achieve a better GUI
*/
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;  
import javafx.scene.image.WritableImage;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Photoshop extends Application {
	Slider gamma_level = new Slider(0,8,4);
	
    @Override
    public void start(Stage stage) throws FileNotFoundException {
		stage.setTitle("Photoshop");

		//Read the image
		Image image = new Image(new FileInputStream("raytrace.jpg"));  

		//Create the graphical view of the image
		ImageView imageView = new ImageView(image);
		
		//Create the simple GUI
		Button invert_button = new Button("Invert");
		Button gamma_button = new Button("Gamma Correct");
		Button contrast_button = new Button("Contrast Stretching");
		Button histogram_button = new Button("Histograms");
		Button cc_button = new Button("Cross Correlation");
		Button equalize_button = new Button("Equalize");
		TextField r1 = new TextField("50");
		TextField r2 = new TextField("200");
		TextField s1 = new TextField("20");
		TextField s2 = new TextField("225");
		
		//Add all the event handlers (this is a minimal GUI - you may try to do better)
		invert_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Invert");
				//At this point, "image" will be the original image
				//imageView is the graphical representation of an image
				//imageView.getImage() is the currently displayed image
                
				//Let's invert the currently displayed image by calling the invert function later in the code
				Image inverted_image=ImageInverter(imageView.getImage());
				//Update the GUI so the new image is displayed
				imageView.setImage(inverted_image);
            }
        });

		gamma_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Gamma Correction");
                Image gamma_image=GammaCorrection(imageView.getImage());
                imageView.setImage(gamma_image);
            }
        });
		
		contrast_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Contrast Stretching");
                Image csImage=ContrastStretching(imageView.getImage(), Integer.parseInt(r1.getText()),Integer.parseInt(r2.getText()),Integer.parseInt(s1.getText()),Integer.parseInt(s2.getText()));
                imageView.setImage(csImage);
            }
        });
		
		histogram_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Histogram");
                MakeHistogram(imageView.getImage());
            }
        });
		
		cc_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Cross Correlation");
                Image cc_Image=CrossCorrelator(imageView.getImage());
				imageView.setImage(cc_Image);
            }
        });
		
		equalize_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("Equalise");
                Image equalise_image=EqualiseImage(imageView.getImage());
                imageView.setImage(equalise_image);
            }
        });
		
		Label gamma_label = new Label("Select Gamma: \nValue: 4.0");
		gamma_level.setShowTickMarks(true);
		gamma_level.setMajorTickUnit(1);
		gamma_level.setMinorTickCount(1);
		gamma_level.setShowTickLabels(true);
        gamma_level.setOnMouseDragged(e -> {
            double value = gamma_level.getValue();
            if (value >= gamma_level.getMin() && value <= gamma_level.getMax()) {
                gamma_label.setText(String.format("Select Gamma: \nValue: %.1f", value));
            } else {
                gamma_label.setText("Select Gamma: \nValue: ---");
            }
        });
        Label r1Label = new Label("R1:");
        Label s1Label = new Label("S1:");
        Label r2Label = new Label("R2:");
        Label s2Label = new Label("S2:");
		
		//Using a flow pane
		FlowPane root = new FlowPane();
		//Gaps between buttons
		root.setVgap(10);
        root.setHgap(5);

		//Add all the buttons and the image for the GUI
		root.getChildren().addAll(invert_button, gamma_button, gamma_label, gamma_level, contrast_button,r1Label,r1,s1Label,s1,r2Label,r2,s2Label,s2, histogram_button, cc_button, equalize_button, imageView);

		//Display to user
        Scene scene = new Scene(root, 1024, 768);
        stage.setScene(scene);
        stage.show();
    }

	//Example function of invert
	public Image ImageInverter(Image image) {
		//Find the width and height of the image to be process
		int width = (int)image.getWidth();
        int height = (int)image.getHeight();
		//Create a new image of that width and height
		WritableImage inverted_image = new WritableImage(width, height);
		//Get an interface to write to that image memory
		PixelWriter inverted_image_writer = inverted_image.getPixelWriter();
		//Get an interface to read from the original image passed as the parameter to the function
		PixelReader image_reader=image.getPixelReader();
		
		//Iterate over all pixels
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				//For each pixel, get the colour
				Color color = image_reader.getColor(x, y);
				//Do something (in this case invert) - the getColor function returns colours as 0..1 doubles (we could multiply by 255 if we want 0-255 colours)
				color=Color.color(1.0-color.getRed(), 1.0-color.getGreen(), 1.0-color.getBlue());
				//Note: for gamma correction you may not need the divide by 255 since getColor already returns 0-1, nor may you need multiply by 255 since the Color.color function consumes 0-1 doubles.
				
				//Apply the new colour
				inverted_image_writer.setColor(x, y, color);
			}
		}
		return inverted_image;
	}
	
	//Gamma Correction is used to alter the luminance (gamma) of the image by a set value.
	//Works by replacing every pixel colour with colour^(1/gamma)
    public Image GammaCorrection(Image image) {
        double gamma = gamma_level.getValue();
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();
        WritableImage gamma_image = new WritableImage(width, height);
        PixelWriter gamma_image_writer = gamma_image.getPixelWriter();
        PixelReader image_reader=image.getPixelReader();
        for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);
                Color newColor = Color.color(Math.pow(color.getRed()/1, 1/gamma),Math.pow(color.getGreen()/1, 1/gamma),Math.pow(color.getBlue()/1, 1/gamma));
				gamma_image_writer.setColor(x, y, newColor);
			}
		}
		return gamma_image;
    }
    
    //Non uniform method to alter the intensity and brightness of each pixel based on it's original value.
    //Works by performing an equation on each pixel based on a preset maximum and minimum intensity range.
    public Image ContrastStretching(Image image, int r1, int r2, int s1, int s2) {
    	int width = (int)image.getWidth();
        int height = (int)image.getHeight();
        WritableImage csImage = new WritableImage(width, height);
        PixelWriter csWriter = csImage.getPixelWriter();
        PixelReader image_reader=image.getPixelReader();

        for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
		        double redOut=0;
		        double greenOut=0;
		        double blueOut=0;
				Color color = image_reader.getColor(x, y);
				double red = color.getRed()*255;
				if (red<r1) {
					redOut = (s1/r1)*red;
				} else if (r1 <= red && red <= r2) {
					redOut = ((s2-s1)/(r2-r1))*(red-r1)+s1;
				} else if (red >= r2) {
					redOut = ((255-s2)/(255-r2))*(red-r2)+s2;
				}
				double blue = color.getBlue()*255;
				if (blue<r1) {
					blueOut = (s1/r1)*blue;
				} else if (r1 <= blue && blue <= r2) {
					blueOut = ((s2-s1)/(r2-r1))*(blue-r1)+s1;
				} else if (blue >= r2) {
					blueOut = ((255-s2)/(255-r2))*(blue-r2)+s2;
				}
				double green = color.getGreen()*255;
				if (green<r1) {
					greenOut = (s1/r1)*green;
				} else if (r1 <= green && green <= r2) {
					greenOut = ((s2-s1)/(r2-r1))*(green-r1)+s1;
				} else if (green >= r2) {
					greenOut = ((255-s2)/(255-r2))*(green-r2)+s2;
				}
				Color newColor = new Color(redOut/255,greenOut/255,blueOut/255,1.0);
				csWriter.setColor(x, y, newColor);
			}
        }
        return csImage;
    }
    
    //Histogram displays the usage of RGB values in the image on a graph.
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void MakeHistogram(Image image) {
    	Stage stage = new Stage();
    	int[][] histogram;
    	histogram=new int[256][3];
		int h = (int) image.getHeight();
		int w = (int) image.getWidth();
		PixelReader image_reader=image.getPixelReader();
		//also need to declare/assign red, h, w, etc.
    	for (int j= 0; j<h; j++) {
    		for (int i=0; i<w; i++) {
    			Color color=image_reader.getColor(i,j);
    			int red = (int) Math.round(color.getRed()*255);
    		    int green = (int) Math.round(color.getGreen()*255);
    		    int blue = (int) Math.round(color.getBlue()*255);
    			histogram[red][0]++;
    			histogram[green][1]++;
    			histogram[blue][2]++;
    			//[0] for red, [1] green
    		}
    	}
    	for (int l=0; l<256; l++) {
    		System.out.println(l+" r="+histogram[l][0]+
    				" g="+histogram[l][1]+
    				" b="+histogram[l][2]);
    	}
        //defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("RGB Color Values");
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("Histogram");
        lineChart.setCreateSymbols(false);
        //defining a series
        XYChart.Series series1 = new XYChart.Series();
        XYChart.Series series2 = new XYChart.Series();
        XYChart.Series series3 = new XYChart.Series();
        series1.setName("Red");
        series2.setName("Green");
        series3.setName("Blue");
        for (int k = 0; k < 256; k++) {
        	series1.getData().add(new XYChart.Data(k, histogram[k][0]));
        	series2.getData().add(new XYChart.Data(k, histogram[k][1]));
        	series3.getData().add(new XYChart.Data(k, histogram[k][2]));
        }
        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().addAll(series1, series2, series3);
        stage.setScene(scene);
        stage.show();
    }
	
    //Equalise presents the RGB values in a more usable way by better distributing colour intensities across the image.
    //Mine works in greyscale.
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Image EqualiseImage(Image image) {
    	Stage stage = new Stage();
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		int size = width * height;
		int[][] histogram;
		double[][] mapping;
    	histogram=new int[256][4];
    	mapping=new double[256][4];
		WritableImage equal_image = new WritableImage(width, height);
		PixelWriter equal_image_writer = equal_image.getPixelWriter();
		PixelReader image_reader=image.getPixelReader();
		//Equalise the image
		int[][] t;
		t=new int[width*height][4];
		//defining the axes
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Equalised Color Values");
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
        lineChart.setTitle("Equalised Histogram");
        lineChart.setCreateSymbols(false);
        Scene scene  = new Scene(lineChart,800,600);
		//defining a series
		XYChart.Series series1 = new XYChart.Series();
		XYChart.Series series2 = new XYChart.Series();
		XYChart.Series series3 = new XYChart.Series();
        XYChart.Series series4 = new XYChart.Series();
        series1.setName("Red");
        series2.setName("Green");
        series3.setName("Blue");
        series4.setName("Grey");
		for (int y= 0; y<height; y++) {
    		for (int x=0; x<width; x++) {
    			Color color = image_reader.getColor(x, y);
    			double red = color.getRed()*255;
    		    double green = color.getGreen()*255;
    		    double blue = color.getBlue()*255;
    		    double grey = ((color.getRed() + color.getGreen() + color.getBlue())/3)*255;
    		    double greyScale = ((color.getRed() + color.getGreen() + color.getBlue())/3);
    		    equal_image_writer.setColor(x, y,new Color(greyScale, greyScale, greyScale, 1.0));
    			histogram[(int) red][0]++;
    			histogram[(int) green][1]++;
    			histogram[(int) blue][2]++;
    			histogram[(int) grey][3]++;
    		}
    	}
		for (int l=0; l<256; l++) {
    		//System.out.println(l+" grey="+histogram[l]);
    		if(l==0) {
    			t[0][0]=histogram[0][0];
    			t[0][1]=histogram[0][1];
    			t[0][2]=histogram[0][2];
    			t[0][3]=histogram[0][3];
    		} else {
    			t[l][0]=t[l-1][0]+histogram[l][0];
    			t[l][1]=t[l-1][1]+histogram[l][1];
    			t[l][2]=t[l-1][2]+histogram[l][2];
    			t[l][3]=t[l-1][3]+histogram[l][3];
    		}
    		//System.out.println(l+" CD="+t[l]);
    		mapping[l][0]= ((t[l][0]*255/size));
    		mapping[l][1]= ((t[l][1]*255/size));
    		mapping[l][2]= ((t[l][2]*255/size));
    		mapping[l][3]= ((t[l][3]*255/size));
    	}
		for (int l=0; l<256; l++) {
			series1.getData().add(new XYChart.Data(l, mapping[l][0]));
			series2.getData().add(new XYChart.Data(l, mapping[l][1]));
        	series3.getData().add(new XYChart.Data(l, mapping[l][2]));
        	series4.getData().add(new XYChart.Data(l, mapping[l][3]));
		}
		
		for (int l=0; l<256; l++) {
			System.out.println(l + " r = " + mapping[l][0] +
					" g = " + mapping[l][1] +
					" b = " + mapping[l][2] +
					" grey = " + mapping[l][3]);
		}
		
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Color color = image_reader.getColor(x, y);
				double greyScaled = ((color.getRed() + color.getGreen() + color.getBlue())/3)*255;
				color = Color.color((mapping[(int) greyScaled][3])/255, (mapping[(int) greyScaled][3])/255, (mapping[(int) greyScaled][3])/255);
				equal_image_writer.setColor(x, y,color);
			}
		}
		lineChart.getData().addAll(series1,series2,series3,series4);
		stage.setScene(scene);
		stage.show();
		return equal_image;
	}
    
	//Cross Correlation performs is a measure of similarity for a pixel and its neighbours and is used to reduce noise in an image.
	//This can be used to prepare for edge detection.
	public Image CrossCorrelator(Image image) {
		double max = 0;
		double min = 0;
		double r = 0;
		double g = 0;
		double b = 0;
		int width = (int)image.getWidth();
		int height = (int)image.getHeight();
		WritableImage ccImage = new WritableImage(width, height);
		PixelWriter ccImageWriter = ccImage.getPixelWriter();
		PixelReader image_reader=image.getPixelReader();
		//laplacianMatrix[x][y]
		for (int y= 0; y<height; y++) {
    		for (int x=0; x<width; x++) {
    			Color color = image_reader.getColor(x, y);
    		    double greyScale = ((color.getRed() + color.getGreen() + color.getBlue())/3);
    		    ccImageWriter.setColor(x, y,new Color(greyScale, greyScale, greyScale, 1.0));
    		}
		}
		for (int y = 2; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {
            	Color c00 = image_reader.getColor(x-2, y-2);
                Color c01 = image_reader.getColor(x-2, y-1);
                Color c02 = image_reader.getColor(x-2, y);
                Color c03 = image_reader.getColor(x-2, y+1);
                Color c04 = image_reader.getColor(x-2, y+2);
                Color c10 = image_reader.getColor(x-1, y-2);
                Color c11 = image_reader.getColor(x-1, y-1);
                Color c12 = image_reader.getColor(x-1, y);
                Color c13 = image_reader.getColor(x-1, y+1);
                Color c14 = image_reader.getColor(x-1, y+2);
                Color c20 = image_reader.getColor(x, y-2);
                Color c21 = image_reader.getColor(x, y-1);
                Color c22 = image_reader.getColor(x, y);
                Color c23 = image_reader.getColor(x, y+1);
                Color c24 = image_reader.getColor(x, y+2);
                Color c30 = image_reader.getColor(x+1, y-2);
                Color c31 = image_reader.getColor(x+1, y-1);
                Color c32 = image_reader.getColor(x+1, y);
                Color c33 = image_reader.getColor(x+1, y+1);
                Color c34 = image_reader.getColor(x+1, y+2);
                Color c40 = image_reader.getColor(x+2, y-2);
                Color c41 = image_reader.getColor(x+2, y-1);
                Color c42 = image_reader.getColor(x+2, y);
                Color c43 = image_reader.getColor(x+2, y+1);
                Color c44 = image_reader.getColor(x+2, y+2);
                r = 		-4*(c00.getRed()*255) -   (c01.getRed()*255) +   (c02.getRed()*255) -   (c03.getRed()*255) - 4*(c04.getRed()*255)
                        	-  (c10.getRed()*255) + 2*(c11.getRed()*255) + 3*(c12.getRed()*255) + 2*(c13.getRed()*255) -   (c14.getRed()*255)
                        	+  (c20.getRed()*255) + 3*(c21.getRed()*255) + 4*(c22.getRed()*255) + 3*(c23.getRed()*255) +   (c24.getRed()*255)
                        	-  (c30.getRed()*255) + 2*(c31.getRed()*255) + 3*(c32.getRed()*255) + 2*(c33.getRed()*255) -   (c34.getRed()*255)
                        	-4*(c40.getRed()*255) -   (c41.getRed()*255) +   (c42.getRed()*255) -   (c43.getRed()*255) - 4*(c44.getRed()*255);
                g = 		-4*(c00.getGreen()*255) -   (c01.getGreen()*255) +   (c02.getGreen()*255) -   (c03.getGreen()*255) - 4*(c04.getGreen()*255)
                        	-  (c10.getGreen()*255) + 2*(c11.getGreen()*255) + 3*(c12.getGreen()*255) + 2*(c13.getGreen()*255) -   (c14.getGreen()*255)
                        	+  (c20.getGreen()*255) + 3*(c21.getGreen()*255) + 4*(c22.getGreen()*255) + 3*(c23.getGreen()*255) +   (c24.getGreen()*255)
                        	-  (c30.getGreen()*255) + 2*(c31.getGreen()*255) + 3*(c32.getGreen()*255) + 2*(c33.getGreen()*255) -   (c34.getGreen()*255)
                        	-4*(c40.getGreen()*255) -   (c41.getGreen()*255) +   (c42.getGreen()*255) -   (c43.getGreen()*255) - 4*(c44.getGreen()*255);
                b = 		-4*(c00.getBlue()*255) -   (c01.getBlue()*255) +   (c02.getBlue()*255) -   (c03.getBlue()*255) - 4*(c04.getBlue()*255)
                    		-  (c10.getBlue()*255) + 2*(c11.getBlue()*255) + 3*(c12.getBlue()*255) + 2*(c13.getBlue()*255) -   (c14.getBlue()*255)
                    		+  (c20.getBlue()*255) + 3*(c21.getBlue()*255) + 4*(c22.getBlue()*255) + 3*(c23.getBlue()*255) +   (c24.getBlue()*255)
                    		-  (c30.getBlue()*255) + 2*(c31.getBlue()*255) + 3*(c32.getBlue()*255) + 2*(c33.getBlue()*255) -   (c34.getBlue()*255)
                    		-4*(c40.getBlue()*255) -   (c41.getBlue()*255) +   (c42.getBlue()*255) -   (c43.getBlue()*255) - 4*(c44.getBlue()*255);
                //Calculate max and min before performing matrix calculation
                if (r>max) {
                	max = r;
                } else if (g>max) {
                	max = g;
                } else if (b>max) {
                	max = b;
                }
                if (r<min) {
                	min = r;
                } else if (g<min) {
                	min = g;
                } else if (b<min) {
                	min = b;
                }
            }
		}
		for (int y = 2; y < height - 2; y++) {
            for (int x = 2; x < width - 2; x++) {
            	Color c00 = image_reader.getColor(x-2, y-2);
                Color c01 = image_reader.getColor(x-2, y-1);
                Color c02 = image_reader.getColor(x-2, y);
                Color c03 = image_reader.getColor(x-2, y+1);
                Color c04 = image_reader.getColor(x-2, y+2);
                Color c10 = image_reader.getColor(x-1, y-2);
                Color c11 = image_reader.getColor(x-1, y-1);
                Color c12 = image_reader.getColor(x-1, y);
                Color c13 = image_reader.getColor(x-1, y+1);
                Color c14 = image_reader.getColor(x-1, y+2);
                Color c20 = image_reader.getColor(x, y-2);
                Color c21 = image_reader.getColor(x, y-1);
                Color c22 = image_reader.getColor(x, y);
                Color c23 = image_reader.getColor(x, y+1);
                Color c24 = image_reader.getColor(x, y+2);
                Color c30 = image_reader.getColor(x+1, y-2);
                Color c31 = image_reader.getColor(x+1, y-1);
                Color c32 = image_reader.getColor(x+1, y);
                Color c33 = image_reader.getColor(x+1, y+1);
                Color c34 = image_reader.getColor(x+1, y+2);
                Color c40 = image_reader.getColor(x+2, y-2);
                Color c41 = image_reader.getColor(x+2, y-1);
                Color c42 = image_reader.getColor(x+2, y);
                Color c43 = image_reader.getColor(x+2, y+1);
                Color c44 = image_reader.getColor(x+2, y+2);
                r = 		-4*(c00.getRed()*255) -   (c01.getRed()*255) +   (c02.getRed()*255) -   (c03.getRed()*255) - 4*(c04.getRed()*255)
                        	-  (c10.getRed()*255) + 2*(c11.getRed()*255) + 3*(c12.getRed()*255) + 2*(c13.getRed()*255) -   (c14.getRed()*255)
                        	+  (c20.getRed()*255) + 3*(c21.getRed()*255) + 4*(c22.getRed()*255) + 3*(c23.getRed()*255) +   (c24.getRed()*255)
                        	-  (c30.getRed()*255) + 2*(c31.getRed()*255) + 3*(c32.getRed()*255) + 2*(c33.getRed()*255) -   (c34.getRed()*255)
                        	-4*(c40.getRed()*255) -   (c41.getRed()*255) +   (c42.getRed()*255) -   (c43.getRed()*255) - 4*(c44.getRed()*255);
                g = 		-4*(c00.getGreen()*255) -   (c01.getGreen()*255) +   (c02.getGreen()*255) -   (c03.getGreen()*255) - 4*(c04.getGreen()*255)
                        	-  (c10.getGreen()*255) + 2*(c11.getGreen()*255) + 3*(c12.getGreen()*255) + 2*(c13.getGreen()*255) -   (c14.getGreen()*255)
                        	+  (c20.getGreen()*255) + 3*(c21.getGreen()*255) + 4*(c22.getGreen()*255) + 3*(c23.getGreen()*255) +   (c24.getGreen()*255)
                        	-  (c30.getGreen()*255) + 2*(c31.getGreen()*255) + 3*(c32.getGreen()*255) + 2*(c33.getGreen()*255) -   (c34.getGreen()*255)
                        	-4*(c40.getGreen()*255) -   (c41.getGreen()*255) +   (c42.getGreen()*255) -   (c43.getGreen()*255) - 4*(c44.getGreen()*255);
                b = 		-4*(c00.getBlue()*255) -   (c01.getBlue()*255) +   (c02.getBlue()*255) -   (c03.getBlue()*255) - 4*(c04.getBlue()*255)
                    		-  (c10.getBlue()*255) + 2*(c11.getBlue()*255) + 3*(c12.getBlue()*255) + 2*(c13.getBlue()*255) -   (c14.getBlue()*255)
                    		+  (c20.getBlue()*255) + 3*(c21.getBlue()*255) + 4*(c22.getBlue()*255) + 3*(c23.getBlue()*255) +   (c24.getBlue()*255)
                    		-  (c30.getBlue()*255) + 2*(c31.getBlue()*255) + 3*(c32.getBlue()*255) + 2*(c33.getBlue()*255) -   (c34.getBlue()*255)
                    		-4*(c40.getBlue()*255) -   (c41.getBlue()*255) +   (c42.getBlue()*255) -   (c43.getBlue()*255) - 4*(c44.getBlue()*255);
                r=((r-min)*255)/(max-min);
                g=((g-min)*255)/(max-min);
                b=((b-min)*255)/(max-min);
                r = Math.min(255, Math.max(0, r))/255;
                g = Math.min(255, Math.max(0, g))/255;
                b = Math.min(255, Math.max(0, b))/255;
                Color color = new Color(r, g, b, 1.0);
                ccImageWriter.setColor(x, y, color);
            }
        }
		return ccImage;
	}
    public static void main(String[] args) {
        launch();
    }

}
