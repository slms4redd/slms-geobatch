package it.geosolutions.geobatch.unredd.geostore.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class JAXBMarshallerBuilder {

	/**
	 * Store the Context created in order to reuse it without creating another.
	 */
	private static final ConcurrentMap<Class, Marshaller> contextStore = new ConcurrentHashMap<Class, Marshaller>();
	
	private JAXBMarshallerBuilder() {}
	
	/**
	 * 
	 * @param clazz the Class from which to extract the JAXB context.
	 * @return a Marshaller for the context created.
	 * @throws JAXBException
	 */
	public static Marshaller getJAXBMarshaller(Class clazz) throws JAXBException{ 
		Marshaller m = null;
		if(!contextStore.containsKey(clazz)){
			//create and store marshaller for the provided class
			JAXBContext context = null;
			context = JAXBContext.newInstance(clazz);
			m = context.createMarshaller();
            contextStore.putIfAbsent(clazz, m);
            return m;
		}
		else{
			return contextStore.get(clazz);
		}
		
	}
	
}
