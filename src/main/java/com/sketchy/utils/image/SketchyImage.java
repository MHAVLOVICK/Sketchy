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

package com.sketchy.utils.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.NodeList;

public class SketchyImage {
	private double dotsPerMillimeterWidth = 0;
	private double dotsPerMillimeterHeight = 0;
	
	public SketchyImage(BufferedImage image, double dotsPerMillimeterWidth, double dotsPerMillimeterHeight) {
		// make sure image is a byte array
		// if not, then convert it
		if (image.getType() !=BufferedImage.TYPE_BYTE_INDEXED){
			this.image = toByteImage(image);
		} else {
			this.image = image;
		}
		this.dotsPerMillimeterWidth = dotsPerMillimeterWidth;
		this.dotsPerMillimeterHeight=dotsPerMillimeterHeight;
	}
	
	private BufferedImage image = null;
	
	public double getDotsPerMillimeterWidth() {
		return dotsPerMillimeterWidth;
	}

	public void setDotsPerMillimeterWidth(double dotsPerMillimeterWidth) {
		this.dotsPerMillimeterWidth = dotsPerMillimeterWidth;
	}

	public double getDotsPerMillimeterHeight() {
		return dotsPerMillimeterHeight;
	}

	public void setDotsPerMillimeterHeight(double dotsPerMillimeterHeight) {
		this.dotsPerMillimeterHeight = dotsPerMillimeterHeight;
	}

	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	public int getWidth(){
		return image.getWidth();
	}
	
	public int getHeight(){
		return image.getHeight();
	}

	public int getWidthInMillimeters(){
		return  (int) Math.ceil(image.getWidth() / getDotsPerMillimeterWidth());
	}
	
	public int getHeightInMillimeters(){
		return  (int) Math.ceil(image.getHeight() / getDotsPerMillimeterHeight());
	}
	

	
	public static void save(SketchyImage sketchyImage, File file) throws Exception {
		if (!file.getParentFile().canWrite()){
			throw new Exception("Can not write to File: " + file.getPath() + "!");
		}
		
		if (!StringUtils.endsWithIgnoreCase(file.getName(), ".png")){
			throw new Exception("Can not save SketchyImage! Must be a .png file!");
		}
		
		Iterator<ImageWriter> imageWriters = ImageIO.getImageWritersByFormatName("png");
		ImageWriter imageWriter = null;
	    if (imageWriters.hasNext()) {  // Just get first one
	    	imageWriter = imageWriters.next();
	    }
	    if (imageWriter==null){
	    	// this should never happen!! if so.. we got problems
	    	throw new Exception("Can not find ImageReader for .png Files!");
	    }
			
		ImageOutputStream os = null;
	    try {
	    	os = ImageIO.createImageOutputStream(file);
	    	imageWriter.setOutput(os);
	    	
	         ImageWriteParam imageWriterParam = imageWriter.getDefaultWriteParam();
	         IIOMetadata metadata = imageWriter.getDefaultImageMetadata(ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_BYTE_BINARY), imageWriterParam);

	         String metaDataFormatName = metadata.getNativeMetadataFormatName();
	         IIOMetadataNode metaDataNode =(IIOMetadataNode) metadata.getAsTree(metaDataFormatName);
	         
	         NodeList childNodes = metaDataNode.getElementsByTagName("pHYs");
	         IIOMetadataNode physNode = null;
	         if (childNodes.getLength() == 0) {
	        	 physNode = new IIOMetadataNode("pHYs");
	        	 physNode.setAttribute("pixelsPerUnitXAxis", Integer.toString((int)Math.ceil(sketchyImage.dotsPerMillimeterWidth*1000)));
	        	 physNode.setAttribute("pixelsPerUnitYAxis", Integer.toString((int)Math.ceil(sketchyImage.dotsPerMillimeterHeight*1000)));
	        	 physNode.setAttribute("unitSpecifier","meter"); // always meter
	        	 metaDataNode.appendChild(physNode);
	         } else {
	        	 for (int nodeIdx=0;nodeIdx<childNodes.getLength();nodeIdx++){
	        		 physNode = (IIOMetadataNode) childNodes.item(nodeIdx);
	        		 physNode.setAttribute("pixelsPerUnitXAxis", Integer.toString((int)Math.ceil(sketchyImage.dotsPerMillimeterWidth*1000)));
		        	 physNode.setAttribute("pixelsPerUnitYAxis", Integer.toString((int)Math.ceil(sketchyImage.dotsPerMillimeterHeight*1000)));
		        	 physNode.setAttribute("unitSpecifier","meter"); // always meter
	        		 metaDataNode.appendChild(physNode);
	        	 }
	         }
	         metadata.setFromTree(metaDataFormatName, metaDataNode);
	         imageWriter.write(new IIOImage(sketchyImage.image, null, metadata));
	         os.flush();
	    } catch (Exception e){
	    	throw new Exception("Error Saving SketchyImage File: " + file.getPath() + "!  " + e.getMessage());
	    } finally {
	    	IOUtils.closeQuietly(os);
	    } 
	}
	
	public static SketchyImage load(File file) throws Exception {
		SketchyImage sketchyImage = null;
		
		if (!file.exists() || !file.canRead()){
			throw new Exception("Can not find or read File: " + file.getPath() + "!");
		}
		
		if (!StringUtils.endsWithIgnoreCase(file.getName(), ".png")){
			throw new Exception("Can not load SketchyImage! Must be a .png file!");
		}
		
		Iterator<ImageReader> imageReaders = ImageIO.getImageReadersByFormatName("png");
	    ImageReader imageReader = null;
	    if (imageReaders.hasNext()) { // Just get first one
	    	imageReader = imageReaders.next();
	    }
	    if (imageReader==null){
	    	// this should never happen!! if so.. we got problems
	    	throw new Exception("Can not find ImageReader for .png Files!");
	    }
			
		ImageInputStream is = null;
	    try {
	    	is = ImageIO.createImageInputStream(file);
	    	imageReader.setInput(is, true);
	    	IIOMetadata metaData = imageReader.getImageMetadata(0); // always get first image
	        IIOMetadataNode metaDataNode = (IIOMetadataNode) metaData.getAsTree(metaData.getNativeMetadataFormatName());
	        if (metaDataNode==null){
	        	throw new Exception("Error retreiving MetaData properties from .png File!");
	        }
	        
	        NodeList childNodes = metaDataNode.getElementsByTagName("pHYs");
	        // only look in the first node
	        if (childNodes.getLength()==0){
	        	throw new Exception("Invalid SketchyImage file. It must contain 'pixelsPerUnit' MetaData!");
	        }
        	IIOMetadataNode physNode = (IIOMetadataNode) childNodes.item(0);
        	String pixelsPerUnitXAxisAttribute = physNode.getAttribute("pixelsPerUnitXAxis");
        	String pixelsPerUnitYAxisAttribute = physNode.getAttribute("pixelsPerUnitYAxis");
        	// String unitSpecifierAttribute = physNode.getAttribute("unitSpecifier"); Just assuming meter
        	if (StringUtils.isBlank(pixelsPerUnitXAxisAttribute)){
        		throw new Exception("Invalid SketchyImage file. It must contain 'pixelsPerUnitXAxis' MetaData!");
	        }
        	if (StringUtils.isBlank(pixelsPerUnitYAxisAttribute)){
        		throw new Exception("Invalid SketchyImage file. It must contain 'pixelsPerUnitYAxis' MetaData!");
	        }
        	
        	int pixelsPerUnitXAxis;
        	try{
        		pixelsPerUnitXAxis = Integer.parseInt(pixelsPerUnitXAxisAttribute);
        		if (pixelsPerUnitXAxis<=0) throw new Exception("Value must be > 0");
        	} catch (Exception e){
        		throw new Exception("Invalid 'pixelsPerUnitXAxis' MetaData Attribute! " + e.getMessage());
        	}
        	
        	int pixelsPerUnitYAxis;
        	try{
        		pixelsPerUnitYAxis = Integer.parseInt(pixelsPerUnitYAxisAttribute);
        		if (pixelsPerUnitYAxis<=0) throw new Exception("Value must be > 0");
        	} catch (Exception e){
        		throw new Exception("Invalid 'pixelsPerUnitYAxis' MetaData Attribute! " + e.getMessage());
        	}
        	
        	// We successfully processed the MetaData.. now read/set the image 
        	BufferedImage bufferedImage = imageReader.read(0); // always get first image

        	double xPixelsPerMM = pixelsPerUnitXAxis/1000.0;
        	double yPixelsPerMM = pixelsPerUnitYAxis/1000.0;
        	
        	sketchyImage = new SketchyImage(bufferedImage, xPixelsPerMM, yPixelsPerMM);
	    } catch (Exception e){
	    	throw new Exception("Error Loading SketchyImage File: " + file.getPath() + "! " + e.getMessage());
	    } finally {
	    	IOUtils.closeQuietly(is);
	    } 
		
		return sketchyImage;
	}
	
	public boolean[][] toBooleanBitmapArray(int x, int y, int width, int height) throws Exception {
	    byte[] buffer = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	    boolean[][] ret = new boolean[width][height];

	    int bufferIdx=0;
	    for (int yIdx=0;yIdx<height;yIdx++){
		    for (int xIdx=0;xIdx<width;xIdx++){
		    	ret[xIdx][yIdx] = buffer[bufferIdx++]==0;
		    }
	    }
	    return ret;
    }
	
	public static BufferedImage createByteImage(int width, int height){
		IndexColorModel model = new IndexColorModel(8,2,new byte[]{0,(byte)255}, new byte[]{0,(byte)255}, new byte[]{0,(byte)255});
		return new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED, model);		
	}
	
	public static BufferedImage toByteImage(BufferedImage image) {
		BufferedImage newImage = createByteImage(image.getWidth(), image.getHeight());
		Graphics2D graphics = newImage.createGraphics();
		graphics.setBackground(Color.white);
		graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
		graphics.drawImage(image, 0, 0, null);
		return newImage;
	}
	
	
	public boolean[][] toBooleanBitmapArray(int blackValue) throws Exception {
		return toBooleanBitmapArray(0,0, image.getWidth(), image.getHeight());
    }
	
}
