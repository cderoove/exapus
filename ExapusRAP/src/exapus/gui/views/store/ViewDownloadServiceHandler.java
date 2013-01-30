package exapus.gui.views.store;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;

import com.google.common.io.Files;

import exapus.gui.editors.forest.graph.GraphViz;
import exapus.model.store.Store;

public class ViewDownloadServiceHandler implements IServiceHandler {

	public static String ID = "viewDownloadServiceHandler";

	public static String viewDownloadUrlExternalBrowser(String viewName) {
		StringBuilder url = new StringBuilder();
		url.append(RWT.getRequest().getRequestURL());
		appendHandlerParameter(url, viewName);
		return RWT.getResponse().encodeURL(url.toString());

	}
	
	public static String viewDownloadUrlBrowser(String viewName) {
		StringBuilder url = new StringBuilder();
		url.append(RWT.getRequest().getContextPath());
		url.append(RWT.getRequest().getServletPath());
		appendHandlerParameter(url, viewName);
		return RWT.getResponse().encodeURL(url.toString());
	}


	private static void appendHandlerParameter(StringBuilder url, String viewName) {
		url.append("?");
		url.append(IServiceHandler.REQUEST_PARAM );
		url.append("="+ID);
		url.append("&viewName=" );
		url.append(viewName);
	}
	
	public void service() throws IOException, ServletException {
		String viewName = RWT.getRequest().getParameter("viewName");
		File xmlFile = Store.getCurrent().fileForRegisteredView(viewName);
		if(xmlFile != null) {
			HttpServletResponse response = RWT.getResponse();
			response.setContentType( "application/octet-stream" );
			String contentDisposition = "attachment; filename=\"" + xmlFile.getName() + "\"";
			response.setHeader("Content-Disposition", contentDisposition);
			ServletOutputStream out = response.getOutputStream();
			Files.copy(xmlFile, out);
			out.close();
		}
	}
}

