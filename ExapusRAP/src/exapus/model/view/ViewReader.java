package exapus.model.view;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class ViewReader {

	public View read(InputStream s) throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(View.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		View view = (View) jaxbUnmarshaller.unmarshal(s);
		return view;
	}
	
}
