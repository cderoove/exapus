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
        Multiset<Tag> allTags = fe.getAllDualTags();
        //System.err.println("allTags = " + allTags);

        boolean withDomains = false;
        for (Tag tag : allTags) {
            if (tag.isDomain()) {
                withDomains = true;
                break;
            }
        }

        if (!withDomains) {
            return withDomain(fe, allTags, "", null);
        } else {
            return withAllDomains(fe, allTags);
        }
    }

    private String withAllDomains(ForestElement fe, Multiset<Tag> allTags) {
        final String HTML_ID = uniqueId(fe, "");

        StringBuilder html = new StringBuilder();
        html.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_HEADER);

        Multimap<String, String> domain2supTags = HashMultimap.create();
        Multiset<String> domain2count = HashMultiset.create();

        for (Tag tag : allTags.elementSet()) {
            if (tag.isDomain()) {
                domain2supTags.put(tag.getIdentifier(), tag.getAssociatedName());
                domain2count.add(tag.getIdentifier(), allTags.count(tag));
            }
        }

        DescriptiveStatistics domainStats = new DescriptiveStatistics();
        for (String domain : domain2count.elementSet()) {
/*
            if ("XML".equals(domain)) {
                System.err.println("XML count = " + domain2count.count(domain));
                System.err.println("XML APIs = " + domain2supTags.get(domain).toString());
            }
*/
            domainStats.addValue(domain2count.count(domain));
        }

/*
        System.err.println("domain2supTags = " + domain2supTags.toString());
        System.err.println("domain2count = " + domain2count.toString());
        System.err.println("domainStats = ");
*/
        for (double v : domainStats.getValues()) {
            System.err.print(v + ", ");
        }
        System.err.println("");

        Multimap<Integer, String> freqs = HashMultimap.create();
        for (String domain : domain2supTags.keySet()) {
            int size = getSize(domainStats, domain2count.count(domain));
            freqs.put(size, domain);
        }

        List<Integer> ordered = new ArrayList<Integer>(freqs.keySet());
        Collections.sort(ordered);
        Collections.reverse(ordered);

        int sum = 0;
        for (Integer size : ordered) {
            for (String domain : freqs.get(size)) {
                if (sum + size >= GROUPNG) {
                    html.append("<br>");
                    sum = 0;
                }
                sum += size;

                if (domain2supTags.containsKey(domain)) {
                    html.append(String.format(
                            "<font size=\"%d\"><a href=\"%s\">%s</a></font>&nbsp;&nbsp;",
                            size,
                            getUniqueImageURL(uniqueId(fe, domain)),
                            domain
                    ));

                    withDomain(fe, allTags, domain, domain2supTags.get(domain));
                } else {
                    html.append(String.format("<font size=\"%d\">%s</font>&nbsp;&nbsp;", size, domain));
                }
            }
        }

        html.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_FOOTER);

        registerHtml(HTML_ID, html.toString());

        return html.toString();
    }

    private String withDomain(ForestElement fe, Multiset<Tag> allTags, String domain, Collection<String> tagsWithinDomain) {
        final String HTML_ID = uniqueId(fe, domain);
        final boolean withDomain = domain != null && !domain.isEmpty();
        //System.err.printf("domain = %s, withDomain = %s\n", domain, withDomain);
        //System.err.println("tagsWithDomain = " + (tagsWithinDomain == null ? "" : tagsWithinDomain.toString()));

        DescriptiveStatistics supTagsStats = new DescriptiveStatistics();
        Set<Tag> supTags = new HashSet<Tag>();
        Multimap<String, Tag> subTags = HashMultimap.create();

        for (Tag tag : allTags.elementSet()) {
            if (tag.isSubTag()) {
                subTags.put(tag.getAssociatedName(), tag);
            }

            if (withDomain) {
                // Collecting tags within domain
                if (tagsWithinDomain.contains(tag.getIdentifier())) {
                    supTagsStats.addValue(allTags.count(tag));
                    supTags.add(tag);
                }
            } else {
                // Or collect super tags
                if (tag.isSuperTag()) {
                    supTagsStats.addValue(allTags.count(tag));
                    supTags.add(tag);
                }
            }
        }

        //System.err.println("subTags = " + subTags.toString());
        //System.err.println("supTags = " + supTags.toString());

        Multimap<Integer, Tag> freqs = HashMultimap.create();
        for (Tag tag : supTags) {
            int size = getSize(supTagsStats, allTags.count(tag));
            freqs.put(size, tag);
        }

        Map<String, DescriptiveStatistics> subTagsStats = new HashMap<String, DescriptiveStatistics>();
        for (Tag tag : allTags.elementSet()) {
            if (tag.isSubTag()) {
                if (!subTagsStats.containsKey(tag.getAssociatedName())) {
                    subTagsStats.put(tag.getAssociatedName(), new DescriptiveStatistics());
                }
                subTagsStats.get(tag.getAssociatedName()).addValue(allTags.count(tag));
            }
        }

        Map<String, String> subTagsHtml = new HashMap<String, String>();
        for (String supTag : subTags.keySet()) {
            if (withDomain) {
                if (!tagsWithinDomain.contains(supTag)) continue;
            }

            final String SUBTAGS_HTML_ID = uniqueId(fe, supTag);
            subTagsHtml.put(supTag, SUBTAGS_HTML_ID);

            StringBuilder sugTagsHtml = new StringBuilder();
            sugTagsHtml.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_HEADER);
            sugTagsHtml.append(String.format("<a href=\"%s\"><---Back</a><br><br>", getUniqueImageURL(HTML_ID)));
            int idx = 0;
            for (Tag subTag : subTags.get(supTag)) {
                sugTagsHtml.append(String.format("<font size=\"%d\">%s</font>&nbsp;&nbsp;",
                        getSize(subTagsStats.get(supTag), allTags.count(subTag)), subTag));
                if (++idx % 4 == 0) {
                    sugTagsHtml.append("<br>");
                }
            }
            sugTagsHtml.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_FOOTER);

            registerHtml(SUBTAGS_HTML_ID, sugTagsHtml.toString());
        }

        List<Integer> ordered = new ArrayList<Integer>(freqs.keySet());
        Collections.sort(ordered);
        Collections.reverse(ordered);

        StringBuilder html = new StringBuilder();
        html.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_HEADER);

        if (withDomain) {
            html.append(String.format("<a href=\"%s\"><---Back</a><br><br>", getUniqueImageURL(uniqueId(fe, ""))));
        }

        int sum = 0;
        for (Integer size : ordered) {
            for (Tag tag : freqs.get(size)) {
                if (sum + size >= GROUPNG) {
                    html.append("<br>");
                    sum = 0;
                }
                sum += size;
                if (subTagsHtml.containsKey(tag.getIdentifier())) {
                    html.append(String.format(
                            "<font size=\"%d\"><a href=\"%s\">%s</a></font>&nbsp;&nbsp;",
                            size,
                            getUniqueImageURL(subTagsHtml.get(tag.getIdentifier())),
                            tag.getIdentifier()
                    ));
                } else {
                    html.append(String.format("<font size=\"%d\">%s</font>&nbsp;&nbsp;", size, tag));
                }
            }
        }

        html.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_FOOTER);

        //System.err.println("html = " + html);
        registerHtml(HTML_ID, html.toString());
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
