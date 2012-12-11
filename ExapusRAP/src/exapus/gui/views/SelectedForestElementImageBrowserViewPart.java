package exapus.gui.views;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;


public class SelectedForestElementImageBrowserViewPart extends SelectedForestElementBrowserViewPart {

	public SelectedForestElementImageBrowserViewPart() {
		super();
		registerImageServiceHandler();
	}
	
	private final static String SERVICE_HANDLER = "imageServiceHandler";
	
	protected Object createImageUrl(String imageKey) {
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
		String encodedURL = RWT.getResponse().encodeURL(url.toString());
		return encodedURL;
	}
	
	protected void registerImage(String id, BufferedImage img) {
		RWT.getSessionStore().setAttribute(id, img);
	}

	private class GraphServiceHandler implements IServiceHandler {
		public void service() throws IOException, ServletException {
			String id = RWT.getRequest().getParameter("imageId");
			BufferedImage image = (BufferedImage)RWT.getSessionStore().getAttribute(id);
			HttpServletResponse response = RWT.getResponse();
			response.setContentType("image/png");
			ServletOutputStream out = response.getOutputStream();
			ImageIO.write(image, "png", out );
		}
	}

	private void registerImageServiceHandler() {
		RWT.getServiceManager().registerServiceHandler(SERVICE_HANDLER, new GraphServiceHandler());
	}
}
