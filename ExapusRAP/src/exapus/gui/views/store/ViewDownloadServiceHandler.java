package exapus.gui.views.store;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceHandler;

import com.google.common.io.Files;

import exapus.model.store.Store;

public class ViewDownloadServiceHandler implements ServiceHandler {

	public static String ID = "viewDownloadServiceHandler";

	public static String viewDownloadUrlBrowser(String viewName) {
		StringBuilder url = new StringBuilder();
		url.append( RWT.getServiceManager().getServiceHandlerUrl(ID));
		url.append("&viewName=");
		url.append(viewName);
		return url.toString();
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		String viewName = request.getParameter("viewName");
		File xmlFile = Store.getCurrent().fileForRegisteredView(viewName);
		if(xmlFile != null) {
			response.setContentType( "application/octet-stream" );
			String contentDisposition = "attachment; filename=\"" + xmlFile.getName() + "\"";
			response.setHeader("Content-Disposition", contentDisposition);
			ServletOutputStream out = response.getOutputStream();
			Files.copy(xmlFile, out);
			out.close();		
		}	
	}	
}

