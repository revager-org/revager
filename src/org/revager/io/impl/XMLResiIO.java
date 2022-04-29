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
package org.revager.io.impl;

import java.io.File;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.revager.app.model.Data;
import org.revager.app.model.DataException;
import org.revager.app.model.appdata.AppSettingKey;
import org.revager.app.model.schema.Aspects;
import org.revager.app.model.schema.Catalog;
import org.revager.app.model.schema.Review;
import org.revager.io.ResiIO;
import org.revager.io.ResiIOException;
import org.xml.sax.SAXException;

/**
 * This class is part of the IO Provider which is designed as Abstract Factory.
 * It implements the {@link ResiIO} interface. The implementation stores data in
 * a XML file (Resi XML schema) and loads data from files in that format.
 */
public class XMLResiIO implements ResiIO {

	/**
	 * This enumeration gives the possibility to choose a validation mode for
	 * storing and loading Resi XML data.
	 */
	private static enum ValidationMode {
		STRICT, TOLERANT;
	}

	/*
	 * Some attributes for XML validation
	 */
	private Schema resiSchema = null;

	private XMLResiValidationEventHandler eventHandler = null;

	/**
	 * Standard constructor with some preparations for XML validation
	 */
	public XMLResiIO() {
		super();

		eventHandler = new XMLResiValidationEventHandler();

		SchemaFactory schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

		try {
			resiSchema = schemaFactory.newSchema(getClass().getResource(Data.getInstance().getResource("path.schema")));
		} catch (SAXException e) {
			/*
			 * Not part of unit testing because the exception is only thrown if
			 * an internal error occur.
			 */
			System.err.println("Error while loading Resi XML schema: " + Data.getInstance().getResource("path.schema"));
		}
	}

	/**
	 * Get an Unmarshaller to the given JAXB context to read an XML file.
	 * 
	 * @return the unmarshaller object to read an XML file
	 * @throws JAXBException
	 *             if setting up the unmarshaller fails
	 */
	private Unmarshaller getUnmarshaller(JAXBContext context, ValidationMode mode) throws JAXBException {
		Unmarshaller uma = context.createUnmarshaller();

		/*
		 * XML validation
		 */
		if (mode == ValidationMode.STRICT) {
			uma.setSchema(resiSchema);
			uma.setEventHandler(eventHandler);
		}

		return uma;
	}

	/**
	 * Get a Marshaller to the given JAXB context to write an XML file.
	 * 
	 * @return the unmarshaller object to write an XML file
	 * @throws JAXBException
	 *             if setting up the marshaller fails
	 */
	private Marshaller getMarshaller(JAXBContext context, ValidationMode mode) throws JAXBException {
		Marshaller ma = context.createMarshaller();

		/*
		 * XML validation
		 */
		if (mode == ValidationMode.STRICT) {
			ma.setSchema(resiSchema);
			ma.setEventHandler(eventHandler);
		}

		ma.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		ma.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.informatik.uni-stuttgart.de/iste/se "
				+ Data.getInstance().getResource("xmlSchemaLocation"));

		return ma;
	}

	/**
	 * Loads Aspects data stored in a XML file with a format as specified in the
	 * Resi XML schema.
	 * 
	 * @param filePath
	 *            the path to the file to read
	 * @throws ResiIOException
	 *             if loading of XML file fails
	 */
	@Override
	public void loadAspects(String filePath) throws ResiIOException {
		JAXBElement<Aspects> asp = null;

		try {
			asp = getUnmarshaller(JAXBContext.newInstance(Aspects.class), ValidationMode.STRICT)
					.unmarshal(new StreamSource(new File(filePath)), Aspects.class);

			/*
			 * Set the aspects object
			 */
			Data.getInstance().getResiData().setAspects(asp.getValue());

			/*
			 * Save file path in the Data model
			 */
			Data.getInstance().getResiData().setAspectsPath(filePath);
		} catch (Exception e) {
			throw new ResiIOException(eventHandler.getMessage());
		}
	}

	/**
	 * Loads Catalog data stored in a XML file with a format as specified in the
	 * Resi XML schema.
	 * 
	 * @param filePath
	 *            the path to the file to read
	 * @throws ResiIOException
	 *             if loading of XML file fails
	 */
	@Override
	public void loadCatalog(String filePath) throws ResiIOException {
		JAXBElement<Catalog> cat = null;

		try {
			cat = getUnmarshaller(JAXBContext.newInstance(Catalog.class), ValidationMode.STRICT)
					.unmarshal(new StreamSource(new File(filePath)), Catalog.class);

			/*
			 * Set the catalog object
			 */
			Data.getInstance().getResiData().setCatalog(cat.getValue());

			/*
			 * Save file path in the Data model
			 */
			Data.getInstance().getResiData().setCatalogPath(filePath);
		} catch (Exception e) {
			throw new ResiIOException(eventHandler.getMessage());
		}
	}

	/**
	 * Loads Review data stored in a XML file with a format as specified in the
	 * Resi XML schema.
	 * 
	 * @param filePath
	 *            the path to the file to read
	 * @throws ResiIOException
	 *             if loading of XML file fails
	 */
	@Override
	public void loadReview(String filePath) throws ResiIOException {
		JAXBElement<Review> rev = null;

		try {
			// URI fileUri = new File(filePath).toURI();
			// InputStream fileStream = fileUri.toURL().openStream();

			rev = getUnmarshaller(JAXBContext.newInstance(Review.class), ValidationMode.STRICT)
					.unmarshal(new StreamSource(new File(filePath)), Review.class);

			/*
			 * Set the review object
			 */
			Data.getInstance().getResiData().setReview(rev.getValue());

			/*
			 * Save file path in the Data model
			 */
			Data.getInstance().getResiData().setReviewPath(filePath);
		} catch (Exception e) {
			throw new ResiIOException(eventHandler.getMessage());
		}
	}

	/**
	 * Loads review backup in a XML file with a format as specified in the Resi
	 * XML schema, but without strict validation.
	 * 
	 * @throws ResiIOException
	 *             if loading of XML file fails
	 */
	@Override
	public void loadReviewBackup() throws ResiIOException {
		JAXBElement<Review> rev = null;

		/*
		 * Read path of the backuped review file
		 */
		String filePath;

		try {
			filePath = Data.getInstance().getAppData().getSetting(AppSettingKey.APP_LAST_REVIEW_PATH);
		} catch (DataException e) {
			/*
			 * Not part of unit testing because the exception is only thrown if
			 * an internal error occur.
			 */
			throw new ResiIOException("Cannot read the path of backuped review file.");
		}

		/*
		 * Path to backuped review data
		 */
		String backupPath = Data.getInstance().getAppData().getAppDataPath()
				+ Data.getInstance().getResource("revBakFileName");

		try {

			rev = getUnmarshaller(JAXBContext.newInstance(Review.class), ValidationMode.TOLERANT)
					.unmarshal(new StreamSource(new File(backupPath)), Review.class);

			/*
			 * Set the review object
			 */
			Data.getInstance().getResiData().setReview(rev.getValue());

			/*
			 * Save file path in the Data model
			 */
			Data.getInstance().getResiData().setReviewPath(filePath);
		} catch (Exception e) {
			throw new ResiIOException(eventHandler.getMessage());
		}
	}

	/**
	 * Stores Aspects data stored in a XML file with a format as specified in
	 * the Resi XML schema.
	 * 
	 * @param filePath
	 *            the path to the file to write
	 * @throws ResiIOException
	 *             if writing of XML file fails
	 */
	@Override
	public void storeAspects(String filePath) throws ResiIOException {
		try {
			getMarshaller(JAXBContext.newInstance(Aspects.class), ValidationMode.STRICT).marshal(
					new JAXBElement<Aspects>(new QName("http://www.informatik.uni-stuttgart.de/iste/se", "aspects", ""),
							Aspects.class, Data.getInstance().getResiData().getAspects()),
					new File(filePath));

		} catch (Exception e) {
			throw new ResiIOException(eventHandler.getMessage());
		}

		/*
		 * Save file path in the Data model
		 */
		Data.getInstance().getResiData().setAspectsPath(filePath);
	}

	/**
	 * Stores Catalog data stored in a XML file with a format as specified in
	 * the Resi XML schema.
	 * 
	 * @param filePath
	 *            the path to the file to write
	 * @throws ResiIOException
	 *             if writing of XML file fails
	 */
	@Override
	public void storeCatalog(String filePath) throws ResiIOException {
		try {
			getMarshaller(JAXBContext.newInstance(Catalog.class), ValidationMode.STRICT).marshal(
					new JAXBElement<Catalog>(new QName("http://www.informatik.uni-stuttgart.de/iste/se", "catalog", ""),
							Catalog.class, Data.getInstance().getResiData().getCatalog()),
					new File(filePath));

		} catch (Exception e) {
			throw new ResiIOException(eventHandler.getMessage());
		}

		/*
		 * Save file path in the Data model
		 */
		Data.getInstance().getResiData().setCatalogPath(filePath);
	}

	/**
	 * Stores Review data stored in a XML file with a format as specified in the
	 * Resi XML schema.
	 * 
	 * @param filePath
	 *            the path to the file to write
	 * @throws ResiIOException
	 *             if writing of XML file fails
	 */
	@Override
	public void storeReview(String filePath) throws ResiIOException {
		try {
			getMarshaller(JAXBContext.newInstance(Review.class), ValidationMode.STRICT).marshal(
					new JAXBElement<Review>(new QName("http://www.informatik.uni-stuttgart.de/iste/se", "review", ""),
							Review.class, Data.getInstance().getResiData().getReview()),
					new File(filePath));
		} catch (Exception e) {
			throw new ResiIOException(eventHandler.getMessage());
		}

		/*
		 * Save file path in the Data model
		 */
		Data.getInstance().getResiData().setReviewPath(filePath);
	}

	/**
	 * Stores review as backup in a XML file with a format as specified in the
	 * Resi XML schema, but without strict validation.
	 * 
	 * @throws ResiIOException
	 *             if writing of XML file fails
	 */
	@Override
	public void storeReviewBackup() throws ResiIOException {
		/*
		 * Path to backup review data
		 */
		String backupPath = Data.getInstance().getAppData().getAppDataPath()
				+ Data.getInstance().getResource("revBakFileName");

		try {
			getMarshaller(JAXBContext.newInstance(Review.class), ValidationMode.TOLERANT).marshal(
					new JAXBElement<Review>(new QName("http://www.informatik.uni-stuttgart.de/iste/se", "review", ""),
							Review.class, Data.getInstance().getResiData().getReview()),
					new File(backupPath));
		} catch (Exception e) {
			/*
			 * Not part of unit testing because the exception is only thrown if
			 * an internal error occur.
			 */
			throw new ResiIOException(eventHandler.getMessage());
		}

		/*
		 * Save path of backuped review file
		 */
		String filePath = Data.getInstance().getResiData().getReviewPath();

		if (filePath == null) {
			filePath = "";
		}

		try {
			Data.getInstance().getAppData().setSetting(AppSettingKey.APP_LAST_REVIEW_PATH, filePath);
		} catch (DataException e) {
			/*
			 * Not part of unit testing because the exception is only thrown if
			 * an internal error occur.
			 */
			throw new ResiIOException("Cannot save the path of backuped review file.");
		}
	}

}
