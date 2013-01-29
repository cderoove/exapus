package exapus.model.view;

import java.io.File;
import java.io.PrintStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class ViewWriter {

	private View view;
	
	public ViewWriter(View view) {
		this.view = view;
	}
		
	public void write(PrintStream s) throws JAXBException {
	    JAXBContext context = JAXBContext.newInstance(View.class);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    m.marshal(view, s);
	}

}
