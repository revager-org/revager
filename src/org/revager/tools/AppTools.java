/* 
 * Copyright 2009 Davide Casciato, Sandra Reich, Johannes Wettinger
 * 
 * This file is part of Resi.
 *
 * Resi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Resi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Resi. If not, see <http://www.gnu.org/licenses/>.
 */
package org.revager.tools;

import java.awt.Image;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Some tools for the application component of Resi.
 */
public class AppTools {

	/**
	 * Returns a random string with random length without numbers.
	 * 
	 * @return a randomly generated string
	 */
	public static String getRandomString() {
		final int MAX_LENGTH = 20;

		String[] pool = new String[] { "*", "~", "#", "(", ")", "$", "+", "[",
				"]", "-", "_", ".", ",", "§", "a", "b", "c", "d", "e", "f",
				"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r",
				"s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D",
				"E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
				"Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

		int randNumber = (int) ((Math.random() * MAX_LENGTH) + 1);
		int randChar;

		StringBuilder result = new StringBuilder("");

		for (int i = 0; i < randNumber; i++) {
			randChar = (int) (Math.random() * pool.length);
			result.append(pool[randChar]);
		}

		return result.toString();
	}

	/**
	 * Gets an image from clipboard.
	 * 
	 * @return the image from clipboard
	 */
	public static Image getImageFromClipboard() {
		// Transferable trans =
		// Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);

		try {
			return (Image) Toolkit.getDefaultToolkit().getSystemClipboard()
					.getData(DataFlavor.imageFlavor);
		} catch (UnsupportedFlavorException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		// return null;
	}

	/**
	 * Write the given image as PNG file.
	 * 
	 * @param image
	 *            the image
	 * @param filePath
	 *            the path to store the PNG file
	 * 
	 * @return the file
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static File writeImageToPNG(Image image, String filePath)
			throws IOException {
		File file = new File(filePath);

		/*
		 * Correct file name
		 */
		if (!file.getName().toLowerCase().endsWith(".png")) {
			file = new File(file.getAbsolutePath() + ".png");
		}

		/*
		 * Convert to buffered image
		 */
		Label dummyObserver = new Label();

		int width = image.getWidth(dummyObserver);
		int height = image.getHeight(dummyObserver);

		BufferedImage bufferedImage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		bufferedImage.getGraphics().drawImage(image, 0, 0, dummyObserver);

		/*
		 * Write image to file
		 */
		ImageIO.write(bufferedImage, "png", file);

		return file;
	}

	public static void writeBufferedImageToFile(BufferedImage image, File file) {
		String filePath = file.getAbsolutePath();

		String formatName = "png";

		if (isWritableImageFile(file)) {
			String[] filePathSplit = filePath.toLowerCase().split("\\.");
			formatName = filePathSplit[filePathSplit.length - 1];
		}

		try {
			ImageIO.write(image, formatName, file);
		} catch (IOException e) {
			// TODO Handle this!
		}
	}

	public static boolean isReadableImageFile(File file) {
		boolean isReadable = false;

		String filePath = file.getAbsolutePath().toLowerCase();

		for (String fileExtension : ImageIO.getReaderFileSuffixes()) {
			if (filePath.endsWith("." + fileExtension.toLowerCase())) {
				isReadable = true;
			}
		}

		return isReadable;
	}

	public static boolean isWritableImageFile(File file) {
		boolean isWritable = false;

		String filePath = file.getAbsolutePath().toLowerCase();

		for (String fileExtension : ImageIO.getWriterFileSuffixes()) {
			if (filePath.endsWith("." + fileExtension.toLowerCase())) {
				isWritable = true;
			}
		}

		return isWritable;
	}

	public static boolean isReadableWritableImageFile(File file) {
		return isReadableImageFile(file) && isWritableImageFile(file);
	}

}
