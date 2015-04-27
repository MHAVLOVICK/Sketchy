/*
    Sketchy
    Copyright (C) 2015 Matthew Havlovick
    http://www.quickdrawbot.com

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

Contact Info:

Matt Havlovick
QuickDraw
470 I St
Washougal, WA 98671

matt@quickdrawbot.com
http://www.quickdrawbot.com

This General Public License does not permit incorporating your program into
proprietary programs.  If your program is a subroutine library, you may
consider it more useful to permit linking proprietary applications with the
library.  If this is what you want to do, use the GNU Lesser General
Public License instead of this License.
*/

package com.sketchy.hardware.impl;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sketchy.hardware.Direction;
import com.sketchy.hardware.HardwareController;
import com.sketchy.utils.DelayUtils;

public class VPlotterDisplayController extends HardwareController implements ActionListener {
	
	private VPlotterDisplayControllerProperties properties = null;
	
	private JFrame jFrame = null;
	
	private MyPanel myPanel = null;

	private long lastMotorTimeNanos=0;
	
	private int leftMotorSteps=0;
	private int rightMotorSteps=0;
	
	private boolean isPenDown=true;
	
	private BufferedImage frameImage = null; 
	
	double frameWidth;
	double frameHeight;
	
	double canvasWidth;
	double canvasHeight;
	
	double yPosOffset;
	
	double leftMotorStepsPerMM;
	double rightMotorStepsPerMM;

	public VPlotterDisplayController(VPlotterDisplayControllerProperties properties) {
		super(properties);
		this.properties=properties;
		
		frameWidth = properties.getFrameWidth();
		frameHeight = properties.getFrameHeight();
		canvasWidth = properties.getCanvasWidth();
		canvasHeight = properties.getCanvasHeight();
		yPosOffset = properties.getyPosOffset();
		leftMotorStepsPerMM=properties.getLeftMotorStepsPerMM();
		rightMotorStepsPerMM=properties.getRightMotorStepsPerMM();
		
		setupDisplay();
	}
	
	@Override
	public VPlotterDisplayControllerProperties getProperties(){
		return properties;
	}
	
	public synchronized void penUp(){
		if (isPenDown){
			isPenDown=false;
		}
	}
	
	public synchronized void penDown(){
		if (!isPenDown){
			isPenDown=true;
		}
	}
	
	public synchronized void stepMotors(Direction leftMotorDirection, Direction rightMotorDirection, long delayInMicroSeconds){
		// make sure display is visible... in case it was hidden
		if (!jFrame.isVisible()){
			jFrame.setVisible(true);
		}
		if ((leftMotorDirection==Direction.NONE) && (rightMotorDirection==Direction.NONE)) return;
		long minDelayInMicroseconds = 0;

		delayInMicroSeconds = Math.max(delayInMicroSeconds,minDelayInMicroseconds);
		
		long delay = delayInMicroSeconds-((System.nanoTime()-lastMotorTimeNanos)/1000);
		if (delay>0){
			DelayUtils.sleepMicros(delay/10); // make it 10x faster 
		}

		int stepLeftMotor=0;
		if (leftMotorDirection!=Direction.NONE){
			if (leftMotorDirection==Direction.FORWARD) {
				stepLeftMotor=1;
			} else {
				stepLeftMotor=-1;
			}
		}


		int stepRightMotor=0;
		if (rightMotorDirection!=Direction.NONE){
			if (rightMotorDirection==Direction.FORWARD){
				stepRightMotor=1;
			} else {
				stepRightMotor=-1;
			}
		}
		
		leftMotorSteps+=stepLeftMotor;
		rightMotorSteps+=stepRightMotor;

		double leftMotorLength=leftMotorSteps/leftMotorStepsPerMM;
		double rightMotorLength=rightMotorSteps/rightMotorStepsPerMM;

		if (isPenDown){
			// this is the point on the frame
			Point point = getPointFromMotorLengths(leftMotorLength, rightMotorLength);
			
			// map it to the image
			int pixelX=(int) (point.x*leftMotorStepsPerMM);
			int pixelY=(int) (point.y*rightMotorStepsPerMM);

			if ((pixelX>=0) && (pixelX<frameImage.getWidth()) &&
				(pixelY>=0) && (pixelY<frameImage.getHeight())){
				Graphics g = frameImage.getGraphics();
				g.setColor(Color.black);
				g.fillRect(pixelX, pixelY, (int) leftMotorStepsPerMM, (int) rightMotorStepsPerMM);
				myPanel.repaint();
			}
		}
		lastMotorTimeNanos=System.nanoTime();
	}

	@Override
	public synchronized void enableMotors() {}

	
	@Override
	public synchronized void disableMotors() {}

	@Override
	public synchronized void shutDown() {
		penDown();
		teardownDisplay();
	}
	
	
	private Point getPointFromMotorLengths(double leftMotorLength, double rightMotorLength){
		double x=((frameWidth*frameWidth)+(leftMotorLength*leftMotorLength)-(rightMotorLength*rightMotorLength))/(frameWidth*2);
		double y=Math.sqrt((leftMotorLength*leftMotorLength)-(x*x));
		return new Point(x,y);
	}
	
	
	private void setupDisplay() {
		
		int windowWidth = properties.getWindowWidth();
		int windowHeight = properties.getWindowHeight();

		jFrame = new JFrame();
		jFrame.setPreferredSize(new Dimension(windowWidth, windowHeight));
		jFrame.setSize(windowWidth, windowHeight);
		jFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		jFrame.setResizable(false);
		
		int frameImageWidth = (int) Math.ceil(frameWidth*leftMotorStepsPerMM);
		int frameImageHeight = (int) Math.ceil(frameHeight*rightMotorStepsPerMM);
		
		frameImage = new BufferedImage(frameImageWidth, frameImageHeight, BufferedImage.TYPE_INT_ARGB);
		clearPage();
		
		myPanel = new MyPanel(frameImage);
		jFrame.getContentPane().add(myPanel, BorderLayout.CENTER);

		JButton button = new JButton("Clear Page");
		button.addActionListener(this);
		
		jFrame.getContentPane().add(button, BorderLayout.SOUTH);
		jFrame.pack();

		double currentXPos=frameWidth/2.0;
		double currentYPos=((int) (frameWidth/4.0))+properties.getyPosOffset(); // pen starts at the yPosOffset (usually negative, above the paper)
		
		double leftMotorLength=Math.sqrt(Math.pow(currentXPos, 2) + Math.pow(currentYPos, 2));
		double rightMotorLength=Math.sqrt(Math.pow(frameWidth-currentXPos, 2) + Math.pow(currentYPos, 2));

		leftMotorSteps = (int) (leftMotorLength*leftMotorStepsPerMM);
		rightMotorSteps = (int) (rightMotorLength*rightMotorStepsPerMM);
		
		penDown();
	}
	
	private void teardownDisplay(){
		jFrame.setVisible(false);
		jFrame.dispose();
		jFrame=null;
	}
	
	
	private class Point {
		public double x;
		public double y;
		
		public Point(double x, double y){
			this.x = x;
			this.y = y;
		}
	}
	
	private class MyPanel extends JPanel{

		private static final long serialVersionUID = -4848555233972756245L;
		private BufferedImage image;
		
		public MyPanel(BufferedImage image){
			super();
			this.image = image;
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			
			g.setColor(Color.white);
			
			int panelWidth=getWidth();
			int panelHeight=getHeight();
			
			int imageWidth=image.getWidth();
			int imageHeight=image.getHeight();

			double widthScale = panelWidth/(double) imageWidth;
			double heightScale = panelHeight/(double) imageHeight;
			double scale=Math.min(widthScale, heightScale);
			
			int scaleWidth=(int) (imageWidth*scale);
			int scaleHeight=(int) (imageHeight*scale); 
			
			int topPos=(panelHeight-scaleHeight)/2;
			int leftPos=(panelWidth-scaleWidth)/2;
			
			g.drawImage(image, leftPos, topPos, leftPos+scaleWidth-1, topPos + scaleHeight-1, 0,0, imageWidth, imageHeight, null);
		}
	}
	
	private void clearPage(){
		
		int frameImageWidth = frameImage.getWidth();
		int frameImageHeight = frameImage.getHeight();

		int canvasLeft = (int) Math.ceil(((frameWidth-canvasWidth)/2.0)*leftMotorStepsPerMM);
		int canvasTop = (int) Math.ceil((frameWidth/4.0)*rightMotorStepsPerMM);
		Graphics2D g = (Graphics2D) frameImage.getGraphics();
		g.setBackground(Color.white);
		g.setColor(Color.black);
		
		float thickness = (float) Math.max(leftMotorStepsPerMM, rightMotorStepsPerMM);
		//Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(thickness));
		g.clearRect(0, 0, frameImageWidth-1, frameImageHeight-1);
		g.drawRect(0, 0, frameImageWidth-1, frameImageHeight-1);
		g.drawRect(canvasLeft-1, canvasTop-1, (int) Math.ceil(canvasWidth*leftMotorStepsPerMM)+1, (int) Math.ceil(canvasHeight*rightMotorStepsPerMM)+1);
		if (myPanel!=null){
			myPanel.repaint();
		}
	}
	
	public void actionPerformed(ActionEvent evt) {
	 	clearPage();
	 }
	
}
