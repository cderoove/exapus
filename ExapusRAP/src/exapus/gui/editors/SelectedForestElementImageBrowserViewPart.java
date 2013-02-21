package exapus.gui.editors;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;

import com.google.common.io.Files;

import exapus.gui.editors.forest.graph.GraphViz;


public class SelectedForestElementImageBrowserViewPart extends SelectedForestElementBrowserViewPart {

	public SelectedForestElementImageBrowserViewPart() {
		super();
		registerImageServiceHandler();
	}

	private final static String SERVICE_HANDLER = "imageServiceHandler";

	protected String createImageUrl(String imageKey) {
		StringBuffer url = new StringBuffer();
		url.append(RWT.getRequest().getContextPath());
		url.append(RWT.getRequest().getServletPath());
		url.append("?");
		url.append(IServiceHandler.REQUEST_PARAM);
		url.append("=");
		url.append(SERVICE_HANDLER);
		url.append("&imageId=");
		url.append(imageKey);
		url.append("&nocache=");
		url.append(System.currentTimeMillis());
		return RWT.getResponse().encodeURL(url.toString());
	}

	
	protected void registerImage(String id, File img) {
		RWT.getSessionStore().setAttribute(id, img);
	}


	private class GraphServiceHandler implements IServiceHandler {
		public void service() throws IOException, ServletException {
			String id = RWT.getRequest().getParameter("imageId");
			File image = (File)RWT.getSessionStore().getAttribute(id);
			if(image != null) {
				HttpServletResponse response = RWT.getResponse();
				response.setContentType(GraphViz.IMG_MIME);
				ServletOutputStream out = response.getOutputStream();
				Files.copy(image, out);
				out.close();
			}
		}
	}

	private void registerImageServiceHandler() {
		RWT.getServiceManager().registerServiceHandler(SERVICE_HANDLER, new GraphServiceHandler());
	}
}
