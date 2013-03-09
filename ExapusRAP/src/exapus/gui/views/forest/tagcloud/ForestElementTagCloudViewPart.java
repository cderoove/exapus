package exapus.gui.views.forest.tagcloud;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import exapus.gui.editors.SelectedForestElementBrowserViewPart;
import exapus.gui.views.forest.reference.JavaSource2HTMLLineHighlightingConverter;
import exapus.model.forest.ForestElement;
import exapus.model.tags.Tag;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.eclipse.rwt.RWT;
import org.eclipse.rwt.service.IServiceHandler;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;


public class ForestElementTagCloudViewPart extends SelectedForestElementBrowserViewPart {

    private static final int GROUPNG = 21;

    public static final String ID = "exapus.gui.views.forest.tagcloud.ForestElementTagCloudView";

    public static final String HANDLER = "subTagsHtmlHandler";

    public ForestElementTagCloudViewPart() {
        super();
        registerHandler();
    }

    private void registerHandler() {
        RWT.getServiceManager().registerServiceHandler(HANDLER, new SubTagsHtmlHandler());
    }

    protected String textToRender(ForestElement fe) {
        final String MAIN_HTML_ID = uniqueId(fe, "");

        DescriptiveStatistics ds = new DescriptiveStatistics();
        Multiset<Tag> allTags = fe.getAllDualTags();
        //Multiset<String> allTags = HashMultiset.create();

        System.err.println("allTags = " + allTags);

        Multimap<String, Tag> subTags = HashMultimap.create();

        for (Tag s : allTags.elementSet()) {
            if (s.isSubTag()) {
                //String supTag = s.substring(0, s.indexOf("::"));
                //String subTag = s.substring(s.indexOf("::") + 2);
                subTags.put(s.getParentName(), s);
            }
            ds.addValue(allTags.count(s));
        }

        System.err.println("subTags = " + subTags.toString());

        Multimap<Integer, Tag> freqs = HashMultimap.create();
        //System.err.println("ds = " + ds.toString());
        for (Tag s : allTags.elementSet()) {
            //if (s.contains("::")) continue;
            if (s.isSubTag()) continue;
            int size = getSize(ds, allTags.count(s));
            //System.err.printf("%d -> %d\n", allTags.count(s), size);
            freqs.put(size, s);
        }

        Map<String, DescriptiveStatistics> subTagsDs = new HashMap<String, DescriptiveStatistics>();
        for (Tag tag : allTags.elementSet()) {
            if (tag.isSubTag()) {
                //String supTag = tag.substring(0, tag.indexOf("::"));

                if (!subTagsDs.containsKey(tag.getParentName())) {
                    subTagsDs.put(tag.getParentName(), new DescriptiveStatistics());
                }
                subTagsDs.get(tag.getParentName()).addValue(allTags.count(tag));
            }
        }

        //System.err.println("subTagsDs = " + subTagsDs.toString());

        Map<String, String> subTagsHtml = new HashMap<String, String>();
        for (String tag : subTags.keySet()) {
            final String SUBTAGS_HTML_ID = uniqueId(fe, tag);
            subTagsHtml.put(tag, SUBTAGS_HTML_ID);

            StringBuilder sugTagsHtml = new StringBuilder();
            sugTagsHtml.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_HEADER);
            sugTagsHtml.append(String.format("<a href=\"%s\"><---Back</a><br><br>", getUniqueImageURL(MAIN_HTML_ID)));
            for (Tag subTag : subTags.get(tag)) {
                sugTagsHtml.append(String.format("<font size=\"%d\">%s</font>&nbsp;&nbsp;",
                        getSize(subTagsDs.get(tag), allTags.count(subTag)), subTag));
            }
            sugTagsHtml.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_FOOTER);

            registerHtml(SUBTAGS_HTML_ID, sugTagsHtml.toString());
        }

        List<Integer> ordered = new ArrayList<Integer>(freqs.keySet());
        Collections.sort(ordered);
        Collections.reverse(ordered);

        StringBuilder html = new StringBuilder();
        html.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_HEADER);

        int sum = 0;
        for (Integer size : ordered) {
            for (Tag s : freqs.get(size)) {
                //if (s.contains("::")) continue;
                if (s.isSubTag()) continue;
                if (sum + size >= GROUPNG) {
                    html.append("<br>");
                    sum = 0;
                }
                sum += size;
                if (subTagsHtml.containsKey(s.getIdentifier())) {
                    html.append(String.format("<font size=\"%d\"><a href=\"%s\">%s</a></font>&nbsp;&nbsp;", size, getUniqueImageURL(subTagsHtml.get(s.getIdentifier())), s.getIdentifier()));
                } else {
                    html.append(String.format("<font size=\"%d\">%s</font>&nbsp;&nbsp;", size, s));
                }
            }
        }

        //html.append(allTags.toString());
        html.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_FOOTER);

        //System.err.println("html = " + html);


        registerHtml(MAIN_HTML_ID, html.toString());
        return html.toString();

    }

    private String uniqueId(ForestElement fe, String supTag) {
        return String.format("%s.tagCloud.%s", fe.getQName().toString(), supTag);
    }


    private int getSize(DescriptiveStatistics ds, int count) {
        // Note: Maximum font size that is shown in the browser is 7

        if (ds.getN() == 1) return count;

        if (count < ds.getPercentile(15)) return 1;
        if (count < ds.getPercentile(30)) return 2;
        if (count < ds.getPercentile(45)) return 3;
        if (count < ds.getPercentile(60)) return 4;
        if (count < ds.getPercentile(75)) return 5;
        if (count < ds.getPercentile(90)) return 6;
        return 7;
    }

    private String getUniqueImageURL(String id) {
        return createImageUrl(id);
    }


    protected void registerHtml(String id, String html) {
        RWT.getSessionStore().setAttribute(id, html);
    }

    protected String createImageUrl(String id) {
        StringBuffer url = new StringBuffer();
        url.append(RWT.getRequest().getContextPath());
        url.append(RWT.getRequest().getServletPath());
        url.append("?");
        url.append(IServiceHandler.REQUEST_PARAM);
        url.append("=");
        url.append(HANDLER);
        url.append("&htmlId=");
        url.append(id);
        url.append("&nocache=");
        url.append(System.currentTimeMillis());
        return RWT.getResponse().encodeURL(url.toString());
    }

    private static class SubTagsHtmlHandler implements IServiceHandler {
        public void service() throws IOException, ServletException {
            String id = RWT.getRequest().getParameter("htmlId");
            String html = (String) RWT.getSessionStore().getAttribute(id);
            if (html != null) {
                HttpServletResponse response = RWT.getResponse();
                response.setContentType("text/HTML");
                ServletOutputStream out = response.getOutputStream();
                out.write(html.getBytes(Charset.forName("UTF-8")));
                out.close();
            }
        }
    }

}
