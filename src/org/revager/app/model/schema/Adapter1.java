//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.05.29 at 10:25:38 PM MESZ 
//

package org.revager.app.model.schema;

import java.util.Calendar;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter1 extends XmlAdapter<String, Calendar> {

	@Override
	public Calendar unmarshal(String value) {
		return (jakarta.xml.bind.DatatypeConverter.parseDate(value));
	}

	@Override
	public String marshal(Calendar value) {
		if (value == null) {
			return null;
		}
		return (jakarta.xml.bind.DatatypeConverter.printDate(value));
	}

}
