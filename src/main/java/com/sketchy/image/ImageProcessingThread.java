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

package com.sketchy.image;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import marvin.image.MarvinImage;
import marvin.image.MarvinImageMask;
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinPluginLoader;

import org.apache.commons.io.FileUtils;

import com.sketchy.drawing.DrawingSize;
import com.sketchy.image.RenderedImageAttributes.CenterOption;
import com.sketchy.image.RenderedImageAttributes.FlipOption;
import com.sketchy.image.RenderedImageAttributes.RenderOption;
import com.sketchy.image.RenderedImageAttributes.RotateOption;
import com.sketchy.server.HttpServer;
import com.sketchy.utils.image.SketchyImage;

public class ImageProcessingThread extends Thread{
	
	public enum Status{
		INIT, RENDERING, CANCELLED, ERROR, COMPLETED;
	}
	
	private RenderedImageAttributes renderedImageAttributes;
	
	private boolean cancel=false;
	
	private Status status = Status.INIT; 
	
	private String statusMessage="";
	private int progress=0;
	
	public ImageProcessingThread(RenderedImageAttributes renderedImageAttributes){
		this.renderedImageAttributes = renderedImageAttributes;
		this.cancel=false;
		this.status=Status.INIT;
		this.statusMessage="Initializing Rendering";
	}
	
	public void cancel(){
		cancel=true;
	}
	
	public RenderedImageAttributes getRenderedImageAttributes() {
		return renderedImageAttributes;
	}
	
	public int getProgress() {
		return progress;
	}

	public void run(){
		status=Status.RENDERING;
		
		try{
			progress=5;

			DrawingSize drawingSize = DrawingSize.parse(renderedImageAttributes.getDrawingSize());
			
			double dotsPerMM = 1/renderedImageAttributes.getPenWidth();
			
			statusMessage = "Loading Image";
			progress=10;

			File sourceImage = HttpServer.getUploadFile(ImageAttributes.getImageFilename(renderedImageAttributes.getSourceImageName()));
			BufferedImage bufferedImage = ImageIO.read(sourceImage);

			if (cancel){
				throw new CancelledException();
			}
			progress=25;
			if ((renderedImageAttributes.isInvertImage())){
				statusMessage = "Inverting Image";
				MarvinImage image = new MarvinImage(bufferedImage);
				MarvinImagePlugin plugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.color.invert.jar");
				if (plugin==null){
					throw new Exception("Error loading Marvin Invert Image Plugin!");
				}
				plugin.process(image,image,null,MarvinImageMask.NULL_MASK, false);
				image.update();
				bufferedImage = image.getBufferedImage();
			}
			
			if (cancel){
				throw new CancelledException();
			}
			progress=30;
			if ((renderedImageAttributes.getBrightness()!=50) || (renderedImageAttributes.getContrast()!=50)){
				statusMessage = "Adjusting Contrast/Brightness";
				MarvinImage image = new MarvinImage(bufferedImage);
				MarvinImagePlugin plugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.color.brightnessAndContrast.jar");
				if (plugin==null){
					throw new Exception("Error loading Marvin Brightness Plugin!");
				}
				// Range is -127 to 127.. convert from 0 - 100
				int brightnessValue = (int)(renderedImageAttributes.getBrightness()*2.54)-127;  
				int contrastValue = (int)(renderedImageAttributes.getContrast()*2.54)-127;
				plugin.setAttribute("brightness", brightnessValue);
				plugin.setAttribute("contrast", contrastValue);
				plugin.process(image,image,null,MarvinImageMask.NULL_MASK, false);
				image.update();
				bufferedImage = image.getBufferedImage();
			}
			
			if (cancel){
				throw new CancelledException();
			}

			progress=35;
			if (renderedImageAttributes.getRotateOption()!=RotateOption.ROTATE_NONE){
				statusMessage = "Rotating Image";
				bufferedImage = rotateImage(bufferedImage, renderedImageAttributes.getRotateOption());
			}
			
			if (cancel){
				throw new CancelledException();
			}
			
			progress=40;
			if (renderedImageAttributes.getFlipOption()!=FlipOption.FLIP_NONE){
				statusMessage = "Flipping Image";
				bufferedImage = flipImage(bufferedImage, renderedImageAttributes.getFlipOption());
			}
			
			if (cancel){
				throw new CancelledException();
			}

			progress=50;
			if (renderedImageAttributes.getRenderOption()==RenderOption.RENDER_EDGE_DETECTION){
				statusMessage = "Processing Edge Detection";
				MarvinImage image = new MarvinImage(bufferedImage);
				MarvinImagePlugin plugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.edge.edgeDetector.jar");
				if (plugin==null){
					throw new Exception("Error loading Marvin EdgeDetector Plugin!");
				}
				plugin.process(image,image,null,MarvinImageMask.NULL_MASK, false);
				image.update();
				bufferedImage = image.getBufferedImage();
			}
			
			int drawingWidth = (int) Math.ceil(drawingSize.getWidth()*dotsPerMM);
			int drawingHeight = (int) Math.ceil(drawingSize.getHeight()*dotsPerMM);

			if (cancel){
				throw new CancelledException();
			}
			
			progress=55;
			statusMessage = "Scaling Image";
			bufferedImage = resizeImage(bufferedImage, drawingWidth, drawingHeight, renderedImageAttributes.getCenterOption()); 

			if (cancel){
				throw new CancelledException();
			}
			
			progress=60;
			if (renderedImageAttributes.getRenderOption()==RenderOption.RENDER_HALFTONE){
				statusMessage = "Processing Halftone";
				MarvinImage image = new MarvinImage(bufferedImage);
				MarvinImagePlugin plugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.halftone.errorDiffusion.jar");
				if (plugin==null){
					throw new Exception("Error loading Marvin Halftone Plugin!");
				}
				plugin.process(image,image,null,MarvinImageMask.NULL_MASK, false);
				image.update();
				bufferedImage = image.getBufferedImage();
			}

			if (cancel){
				throw new CancelledException();
			}
			
			progress=65;
			if ((renderedImageAttributes.getThreshold()!=50) && (renderedImageAttributes.getRenderOption()!=RenderOption.RENDER_HALFTONE)){
				statusMessage = "Processing Threshold";
				MarvinImage image = new MarvinImage(bufferedImage);
				MarvinImagePlugin plugin = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.color.thresholding.jar");
				if (plugin==null){
					throw new Exception("Error loading Marvin Threshold Plugin!");
				}
				// Range is 0 to 256.. convert from 0 - 100
				int thresholdValue = (256-(int)(renderedImageAttributes.getThreshold()*2.56));  
				plugin.setAttribute("threshold", thresholdValue);
				plugin.process(image,image,null,MarvinImageMask.NULL_MASK, false);
				image.update();
				bufferedImage = image.getBufferedImage();
			}
		
			if (cancel){
				throw new CancelledException();
			}
			statusMessage = "Saving Rendered Image";

			File renderedFile = HttpServer.getUploadFile(renderedImageAttributes.getImageFilename());
			
			renderedImageAttributes.setWidth(bufferedImage.getWidth());
			renderedImageAttributes.setHeight(bufferedImage.getHeight());
			
			progress=70;
			SketchyImage sketchyImage = new SketchyImage(bufferedImage, dotsPerMM, dotsPerMM);

			if (cancel){
				throw new CancelledException();
			}
			
			progress=80;
			SketchyImage.save(sketchyImage, renderedFile);
			
	
			File renderedDataFile = HttpServer.getUploadFile(renderedImageAttributes.getDataFilename());
			FileUtils.writeStringToFile(renderedDataFile, renderedImageAttributes.toJson());
			progress=100;

			status=Status.COMPLETED;
			statusMessage = "Complete";
		} catch (CancelledException e){
			status=Status.CANCELLED;
			statusMessage = "Cancelled!";
		} catch (Throwable t){
			status=Status.ERROR;
			statusMessage = "Exception occured while processing Image!! " + t.getMessage();
			throw new RuntimeException("Error Running ImageProcessingThread!  " + t.toString());
		}
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public Status getStatus() {
		return status;
	}
	
	private class CancelledException extends Exception {
		
		private static final long serialVersionUID = 1235734712190851307L;
		
		public CancelledException() {
			super();
		}
	}
	
	public static BufferedImage resizeImage(BufferedImage image, int drawingWidth, int drawingHeight, CenterOption centerOption){

		int imageWidth=image.getWidth();
		int imageHeight=image.getHeight();

		double widthScale = drawingWidth/(double) imageWidth;
		double heightScale = drawingHeight/(double) imageHeight;
		double scale=Math.min(widthScale, heightScale);
		
		int scaleWidth=(int) (imageWidth*scale);
		int scaleHeight=(int) (imageHeight*scale); 
		
		BufferedImage resizedImage = new BufferedImage(scaleWidth, scaleHeight, BufferedImage.TYPE_INT_ARGB);
		
	    Graphics2D g = resizedImage.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
	    g.drawImage(image, 0, 0, scaleWidth, scaleHeight, 0, 0, imageWidth, imageHeight, null); 
	    g.dispose();
	    
		int cropWidth = (int) Math.min(scaleWidth, drawingWidth);
		int cropHeight = (int) Math.min(scaleHeight, drawingHeight);
		
		int drawingLeft=0;
		int drawingTop=0;
		if ((centerOption==CenterOption.CENTER_HORIZONTAL) || (centerOption==CenterOption.CENTER_BOTH)){
			drawingLeft=(int) ((drawingWidth-cropWidth)/2.0);
		}
		if ((centerOption==CenterOption.CENTER_VERTICAL) || (centerOption==CenterOption.CENTER_BOTH)){
			drawingTop=(int) ((drawingHeight-cropHeight)/2.0);
		}

		BufferedImage croppedImage = resizedImage.getSubimage(0, 0, cropWidth, cropHeight);
		resizedImage=null;
		
		BufferedImage drawingImage = new BufferedImage(drawingWidth, drawingHeight, BufferedImage.TYPE_INT_ARGB);
		g = drawingImage.createGraphics();
	    g.drawImage(croppedImage, drawingLeft, drawingTop, drawingLeft+cropWidth, drawingTop+cropHeight, 0, 0, cropWidth, cropHeight, null);
	    g.dispose();
	    
	    croppedImage=null;
	    return drawingImage;
	}
	
	public static BufferedImage rotateImage(BufferedImage image, RotateOption rotateOption){

		if (rotateOption==RotateOption.ROTATE_NONE) return image;
		
		int degrees=0;
		int imageWidth=image.getWidth();
		int imageHeight=image.getHeight();
		
		BufferedImage rotatedImage = null;
		if (rotateOption==RotateOption.ROTATE_90){
			degrees=90;
			rotatedImage = new BufferedImage(imageHeight, imageWidth, BufferedImage.TYPE_INT_ARGB);
		} else if (rotateOption==RotateOption.ROTATE_180){
			degrees=180;
			rotatedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		} else if (rotateOption==RotateOption.ROTATE_270){
			degrees=270;
			rotatedImage = new BufferedImage(imageHeight, imageWidth, BufferedImage.TYPE_INT_ARGB);
		}
		
	    Graphics2D g = rotatedImage.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

	    g.rotate(Math.toRadians(degrees), 0, 0);

	    if (degrees==90){
	    	g.drawImage(image, null, 0,-imageHeight);
	    } else if (degrees==180){
	    	g.drawImage(image, null, -imageWidth, -imageHeight);
	    } else if (degrees==270) {
	    	g.drawImage(image, null, -imageWidth,0);
	    }
	    g.dispose();
	    return rotatedImage;
	}

	public static BufferedImage flipImage(BufferedImage image, FlipOption flipOption){

		if (flipOption==FlipOption.FLIP_NONE) return image;
		
		int imageWidth=image.getWidth();
		int imageHeight=image.getHeight();
		
		BufferedImage flippedImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
		
	    Graphics2D g = flippedImage.createGraphics();
	    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

	    
	    int startX=0;
	    int startY=0;
	    int endX=imageWidth;
	    int endY=imageHeight;
	    if ((flipOption==FlipOption.FLIP_HORIZONTAL) || (flipOption==FlipOption.FLIP_BOTH)){
	    	startX=imageWidth;
	    	endX=-imageWidth;
	    } 
	    if ((flipOption==FlipOption.FLIP_VERTICAL) || (flipOption==FlipOption.FLIP_BOTH)){
	    	startY=imageHeight;
	    	endY=-imageHeight;
	    }
	    
    	g.drawImage(image, startX, startY, endX, endY, null);
	    g.dispose();
	    return flippedImage;
	}
	
	
}
