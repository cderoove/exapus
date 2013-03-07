package exapus.gui.views.forest.tagcloud;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import exapus.gui.editors.SelectedForestElementBrowserViewPart;
import exapus.gui.views.forest.reference.JavaSource2HTMLLineHighlightingConverter;
import exapus.model.forest.ForestElement;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ForestElementTagCloudViewPart extends SelectedForestElementBrowserViewPart {

    private static final int GROUPNG = 21;

    public static final String ID = "exapus.gui.views.forest.tagcloud.ForestElementTagCloudView";

    protected String textToRender(ForestElement fe) {
        DescriptiveStatistics ds = new DescriptiveStatistics();
        Multiset<String> allTags = fe.getAllDualTags();
        for (String s : allTags.elementSet()) {
            ds.addValue(allTags.count(s));
        }

        Multimap<Integer, String> freqs = HashMultimap.create();
        //System.err.println("ds = " + ds.toString());
        for (String s : allTags.elementSet()) {
            int size = getSize(ds, allTags.count(s));
            //System.err.printf("%d -> %d\n", allTags.count(s), size);
            freqs.put(size, s);
        }

        List<Integer> ordered = new ArrayList<Integer>(freqs.keySet());
        Collections.sort(ordered);
        Collections.reverse(ordered);

        StringBuilder html = new StringBuilder();
        html.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_HEADER);

        int sum = 0;
        for (Integer size : ordered) {
            for (String s : freqs.get(size)) {
                if (sum + size >= GROUPNG) {
                    html.append("<br>");
                    sum = 0;
                }
                sum += size;
                html.append(String.format("<font size=\"%d\">%s</font>&nbsp;&nbsp;", size, s));
            }
        }

        //html.append(allTags.toString());
        html.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_FOOTER);

        //System.err.println("html = " + html);

        return html.toString();

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

}
