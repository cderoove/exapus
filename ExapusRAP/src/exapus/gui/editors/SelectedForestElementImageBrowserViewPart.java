package exapus.gui.editors;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServiceHandler;

import com.google.common.io.Files;

import exapus.gui.editors.forest.graph.GraphViz;


public class SelectedForestElementImageBrowserViewPart extends SelectedForestElementBrowserViewPart {

	private final static String SERVICE_HANDLER = "imageServiceHandler";

	protected String createImageUrl(String imageKey) {
		  StringBuilder url = new StringBuilder();
		  url.append( RWT.getServiceManager().getServiceHandlerUrl(SERVICE_HANDLER));
		  url.append("&imageId=").append(imageKey);
		  url.append("&nocache=").append(System.currentTimeMillis());
		  return url.toString();
	}

	
	protected void registerImage(String id, File img) {
		RWT.getUISession().setAttribute(id, img);
	}


	private static class GraphServiceHandler implements ServiceHandler {
		@Override
		public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
			String id = request.getParameter("imageId");
			if(id != null) {
				File image = (File)RWT.getUISession().getAttribute(id);
				if(image != null) {
					response.setContentType(GraphViz.IMG_MIME);
					ServletOutputStream out = response.getOutputStream();
					Files.copy(image, out);
					out.close();
				}
			}
		}

	}

	static {
		RWT.getServiceManager().registerServiceHandler(SERVICE_HANDLER, new GraphServiceHandler());
	}
}
