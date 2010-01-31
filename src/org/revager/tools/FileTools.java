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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * This class provides some useful tools for handling files, directories and ZIP
 * archives.
 */
public class FileTools {

	/**
	 * Deletes this given directory recursivly.
	 * 
	 * @param directory
	 *            the path to the directory as File object
	 * 
	 * @return true if the path was deleted, otherwise false
	 */
	public static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();

			for (File f : files) {
				if (f.isDirectory()) {
					deleteDirectory(f);
				} else {
					f.delete();
				}
			}
		}

		return (directory.delete());
	}

	/**
	 * Copies a given source file to target. The target file will be overridden
	 * if it exists.
	 * 
	 * @param source
	 *            source file to copy
	 * @param target
	 *            target file to copy to
	 * 
	 * @throws IOException
	 *             if an error occurs while copying the file
	 */
	public static void copyFile(File source, File target) throws IOException {
		InputStream in = new FileInputStream(source);
		OutputStream out = new FileOutputStream(target);

		byte[] buffer = new byte[1024];
		int length;

		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.close();
	}

	/**
	 * Copies a given source URL to target file. The target file will be
	 * overridden if it exists.
	 * 
	 * @param source
	 *            source URL to copy
	 * @param target
	 *            target file to copy to
	 * 
	 * @throws IOException
	 *             if an error occurs while copying the file
	 */
	public static void copyFile(URL source, File target) throws IOException {
		OutputStream out = new FileOutputStream(target);

		BufferedReader fileReader = null;
		InputStream fileStream = null;

		fileStream = (InputStream) source.openStream();

		fileReader = new BufferedReader(new InputStreamReader(fileStream,
				"UTF-8"));

		byte[] buffer = new byte[1024];
		int length;

		while ((length = fileStream.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		fileReader.close();
		fileStream.close();
	}

	/**
	 * Copies a given source directory to target directory. The target directory
	 * will be overridden if it exists.
	 * 
	 * @param source
	 *            source directory to copy
	 * @param target
	 *            target directory to copy to
	 * 
	 * @throws IOException
	 *             if an error occurs while copying the directory
	 */
	public static void copyDirectory(File source, File target)
			throws IOException {
		if (!target.exists()) {
			target.mkdir();
		}

		if (source.exists()) {
			File[] files = source.listFiles();

			for (File f : files) {
				if (f.isDirectory()) {
					copyDirectory(f, new File(target, f.getName()));
				} else {
					copyFile(f, new File(target, f.getName()));
				}
			}
		}
	}

	/**
	 * Returns the list of all files, which are directly in the given directory;
	 * not recursivly.
	 * 
	 * @param directory
	 *            the directory
	 * 
	 * @return the list of files
	 */
	public static List<File> getListOfFiles(File directory) {
		List<File> list = new ArrayList<File>();

		if (directory.exists() && directory.isDirectory()) {
			File[] files = directory.listFiles();

			for (File f : files) {
				if (f.isFile()) {
					list.add(f);
				}
			}
		}

		return list;
	}

	/**
	 * Write to zip.
	 * 
	 * @param files
	 *            the files
	 * @param urlEncoded
	 *            true if the files in the zip should be encoded with the
	 *            URLEncoder
	 * @param zipFile
	 *            the zip file
	 * @param transformUmlauts
	 *            the transform umlauts
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static void writeToZip(List<File> files, File zipFile,
			boolean urlEncoded, boolean transformUmlauts) throws IOException {
		/*
		 * Open the Zip
		 */
		final FileOutputStream fos = new FileOutputStream(zipFile);
		final ZipOutputStream zip = new ZipOutputStream(fos);
		zip.setLevel(9);
		zip.setMethod(ZipOutputStream.DEFLATED);

		List<String> existingFileNames = new ArrayList<String>();

		/*
		 * Write given files to the Zip
		 */
		for (File f : files) {
			String fileName;

			if (urlEncoded) {
				fileName = URLEncoder.encode(f.getName(), "UTF-8");
			} else if (transformUmlauts) {
				fileName = transformUmlauts(f.getName());

				while (containsFileName(fileName, existingFileNames)) {
					fileName = "_" + fileName;
				}
			} else {
				fileName = f.getName();
			}

			existingFileNames.add(fileName);

			final ZipEntry entry = new ZipEntry(fileName);
			entry.setTime(f.lastModified());

			/*
			 * read contents of file external file we are going to put in the
			 * Zip
			 */
			final int fileLength = (int) f.length();
			final FileInputStream fis = new FileInputStream(f);
			final byte[] wholeFile = new byte[fileLength];

			fis.read(wholeFile, 0, fileLength);
			fis.close();

			/*
			 * no need to setCRC, or setSize as they are computed automatically.
			 */
			zip.putNextEntry(entry);

			/*
			 * write the contents directly into the zip just after the Zip
			 * element
			 */
			zip.write(wholeFile, 0, fileLength);
			zip.closeEntry();
		}

		/*
		 * close the entire Zip
		 */
		zip.close();
	}

	/**
	 * Extracts the given zip file to the given target directory.
	 * 
	 * @param zipFile
	 *            file to extract
	 * @param targetDirectory
	 *            traget directory for extracting the file
	 * @param urlEncoded
	 *            true if the files in the zip are encoded with the URLEncoder
	 * 
	 * @throws IOException
	 *             if an error occurs while extracting the zip file
	 */
	public static void extractZipFile(File zipFile, File targetDirectory,
			boolean urlEncoded) throws IOException {
		final FileInputStream fis = new FileInputStream(zipFile);
		final ZipInputStream zip = new ZipInputStream(fis);

		ZipFile file = new ZipFile(zipFile);

		/*
		 * loop over each entry in Zip
		 */
		while (true) {
			final ZipEntry entry = zip.getNextEntry();

			if (entry == null) {
				break;
			}

			/*
			 * Input stream for a single file in the Zip
			 */
			InputStream eis = file.getInputStream(entry);

			/*
			 * where we will write the element as an external file
			 */
			String entryName;

			if (urlEncoded) {
				entryName = URLDecoder.decode(entry.getName(), "UTF-8");
			} else {
				entryName = entry.getName();
			}

			final File elementFile = new File(targetDirectory, entryName);

			if (!elementFile.isDirectory()) {
				final FileOutputStream fos = new FileOutputStream(elementFile);

				copyStream(eis, fos, true);

				fos.close();

				elementFile.setLastModified(entry.getTime());
			}

			zip.closeEntry();
		}

		file.close();
		zip.close();
	}

	/**
	 * Transform the umlauts to "ae", "oe" etc.
	 * 
	 * @param origFileName
	 *            the original file name
	 * 
	 * @return the string
	 */
	private static String transformUmlauts(String origFileName) {
		origFileName = origFileName.replace("ä", "ae");
		origFileName = origFileName.replace("ö", "oe");
		origFileName = origFileName.replace("ü", "ue");
		origFileName = origFileName.replace("Ä", "Ae");
		origFileName = origFileName.replace("Ö", "Oe");
		origFileName = origFileName.replace("Ü", "Ue");
		origFileName = origFileName.replace("ß", "ss");

		return origFileName;
	}

	/**
	 * Checks if the given file name is part of the given list of files.
	 * 
	 * @param fileName
	 *            the file name
	 * @param files
	 *            the list of file names
	 * 
	 * @return true, if the list contains the given file name
	 */
	private static boolean containsFileName(String fileName,
			List<String> fileNames) {
		for (String fn : fileNames) {
			if (fn.equals(fileName)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Copies directly from one stream to another without knowing the size,
	 * length or EOF of the source.
	 * 
	 * @author http://mindprod.com/
	 * 
	 * @param source
	 *            Stream to copy from
	 * @param target
	 *            Stream to copy to
	 * @param closeTarget
	 *            true if the target Stream should be closed after copy
	 * 
	 * @return true if copy was successful; otherwise false
	 */
	private static boolean copyStream(InputStream source, OutputStream target,
			boolean closeTarget) {
		/*
		 * The buffer size for chunking
		 */
		final int BUFFER_SIZE = 63 * 1024;

		/*
		 * The timeout for reading from input stream
		 */
		final int READ_TIMEOUT = 40 * 1000;

		if (source == null || target == null) {
			return false;
		}

		try {
			/*
			 * Read and write by chunks
			 */
			int chunkSize = BUFFER_SIZE;

			byte[] ba = new byte[chunkSize];

			/*
			 * keep reading till hit EOF
			 */
			int bytesRead;
			while ((bytesRead = readBytesBlocking(source, ba, 0, chunkSize,
					READ_TIMEOUT)) > 0) {
				target.write(ba, 0, bytesRead);
			}

			/*
			 * close the streams
			 */
			source.close();

			if (closeTarget) {
				target.close();
			}
		} catch (IOException e) {
			return false;
		}

		/*
		 * all was ok
		 */
		return true;
	}

	/**
	 * This helper method reads blocks of a given InputStream. It is used by the
	 * copyStream method.
	 * 
	 * @author http://mindprod.com/
	 * 
	 * @param in
	 *            the InputStream to read from
	 * @param b
	 *            the bytes to read as array
	 * @param off
	 *            the offset from which to read
	 * @param len
	 *            the length to read
	 * @param timeoutInMillis
	 *            the timeout in milliseconds to wait for reaction
	 * 
	 * @return true if reading was successful; otherwise false
	 * 
	 * @throws IOException
	 *             if an error occurs while reading the block
	 */
	private static int readBytesBlocking(InputStream in, byte b[], int off,
			int len, int timeoutInMillis) throws IOException {
		final int SLEEP_TIME = 100;

		int totalBytesRead = 0;
		int bytesRead;

		long whenToGiveUp = System.currentTimeMillis() + timeoutInMillis;

		while (totalBytesRead < len
				&& (bytesRead = in.read(b, off + totalBytesRead, len
						- totalBytesRead)) >= 0) {
			if (bytesRead == 0) {
				try {
					if (System.currentTimeMillis() >= whenToGiveUp) {
						throw new IOException("timeout");
					}
					/*
					 * don't hammer the system and suck up all the CPU beating a
					 * tight loop when there are no chars. If this keeps up we
					 * may trigger a java.net.SocketTimeoutException exception.
					 */
					Thread.sleep(SLEEP_TIME);
				} catch (InterruptedException e) {
					/*
					 * do nothing
					 */
				}
			} else {
				totalBytesRead += bytesRead;

				whenToGiveUp = System.currentTimeMillis() + timeoutInMillis;
			}
		}

		return totalBytesRead;
	}

}
