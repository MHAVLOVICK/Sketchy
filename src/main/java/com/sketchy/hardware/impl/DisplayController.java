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


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sketchy.hardware.Direction;
import com.sketchy.hardware.HardwareController;
import com.sketchy.utils.DelayUtils;

public class DisplayController extends HardwareController {
	
	private DisplayControllerProperties properties = null;
	
	private JFrame jFrame = null;
	
	private MyPanel myPanel = null;

	private BufferedImage image = null;

	private long lastMotorTimeNanos=0;

	private int leftMotorSteps=0;
	private int rightMotorSteps=0;
	
	int windowWidth;
	int windowHeight;
	
	double frameWidth;
	double frameHeight;
	
	double canvasWidth; 
	double canvasHeight; 
	
	double canvasTopPos;	
	double canvasLeftPos;
	
	double yPosOffset;
	
	private boolean isPenDown=true;

	public DisplayController(DisplayControllerProperties properties) {
		super(properties);
		this.properties=properties;
		setupDisplay();
	}
	
	@Override
	public DisplayControllerProperties getProperties(){
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
			DelayUtils.sleepMicros(delay);
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
		
		double leftMotorLength=leftMotorSteps;
		double rightMotorLength=rightMotorSteps;

		if (isPenDown){
			// this is the point on the frame
			Point point = getPointFromMotorLengths(leftMotorLength, rightMotorLength);

			// get FrameToImage ratios
			double frameToImageWidth = frameWidth/image.getWidth();
			double frameToImageHeight = frameHeight/image.getHeight();
			
			// map it to the image
			int pixelX=(int) (point.x/frameToImageWidth);
			int pixelY=(int) (point.y/frameToImageHeight);

			if ((pixelX>=0) && (pixelX<image.getWidth()) &&
				(pixelY>=0) && (pixelY<image.getHeight())){
				Graphics g = image.getGraphics();
				g.setColor(Color.black);
				g.fillRect(pixelX, pixelY, (int) Math.ceil(1/frameToImageWidth), (int) Math.ceil(1/frameToImageHeight));
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

		windowWidth = properties.getWindowWidth();
		windowHeight = properties.getWindowHeight();
		
		frameWidth = properties.getFrameWidth();
		frameHeight = properties.getFrameHeight();
		canvasWidth = properties.getCanvasWidth();
		canvasHeight = properties.getCanvasHeight();
		
		yPosOffset = properties.getyPosOffset();
		
		
		jFrame = new JFrame();
		jFrame.setPreferredSize(new Dimension(windowWidth, windowHeight));
		jFrame.setSize(windowWidth, windowHeight);
		jFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		jFrame.addComponentListener(new FrameListener());
		

		int panelWidth=windowWidth-(jFrame.getInsets().left + jFrame.getInsets().right);
		int panelHeight=windowHeight-(jFrame.getInsets().top + jFrame.getInsets().bottom);
		
		int imageWidth = panelWidth; 
		int imageHeight = panelHeight;
		
		image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		myPanel = new MyPanel(image);
		jFrame.getContentPane().add(myPanel);
		
		jFrame.pack();

		double currentXPos=frameWidth/2;
		double currentYPos=frameWidth/4+yPosOffset; // pen starts at the yPosOffset (usually negative, above the paper)
		
		double leftMotorLength=Math.sqrt(Math.pow(currentXPos, 2) + Math.pow(currentYPos, 2));
		double rightMotorLength=Math.sqrt(Math.pow(frameWidth-currentXPos, 2) + Math.pow(currentYPos, 2));

		leftMotorSteps = (int) leftMotorLength;
		rightMotorSteps = (int) rightMotorLength;
		
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

	
	private class FrameListener implements ComponentListener {

		@Override
		public void componentHidden(ComponentEvent arg0) {
		}	

		@Override
		public void componentMoved(ComponentEvent arg0) {
		}

		@Override
		public void componentResized(ComponentEvent arg0) {
		}

		@Override
		public void componentShown(ComponentEvent arg0) {
			if (image!=null){
				Graphics g = image.getGraphics();
				g.setColor(Color.white);
				g.fillRect(0, 0, image.getWidth()-1,  image.getHeight()-1);
				myPanel.repaint();
			}
		}
	}
	
	private class MyPanel extends JPanel{

		private static final long serialVersionUID = -4848555233972756245L;
		private BufferedImage image;
		
		public MyPanel(BufferedImage image){
			this.image = image;
		}
		
		@Override
		public void paint(Graphics g) {
			g.setColor(Color.white);
			g.drawRect((int) canvasLeftPos, (int)(canvasTopPos), (int)(canvasWidth), (int)canvasHeight);
			
			g.drawImage(image, 0, 0, myPanel.getWidth(), myPanel.getHeight(), 0,0,image.getWidth(), image.getHeight(), null);
		}
	}
	
}
