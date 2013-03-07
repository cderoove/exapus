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
import java.util.TreeSet;

public class ForestElementTagCloudViewPart extends SelectedForestElementBrowserViewPart {

    public static final String ID = "exapus.gui.views.forest.tagcloud.ForestElementTagCloudView";

    protected String textToRender(ForestElement fe) {
/*        if (fe.getParentFactForest().getDirection().equals(Direction.INBOUND)) {
            if (fe instanceof Ref)
                fe = ((Ref) fe).getDual();
            else
                return "API-centric";
        }*/

        DescriptiveStatistics ds = new DescriptiveStatistics();
        Multiset<String> allTags = fe.getAllDualTags();
        for (String s : allTags.elementSet()) {
            ds.addValue(allTags.count(s));
        }

        Multimap<Integer, String> freqs = HashMultimap.create();
        System.err.println("ds = " + ds.toString());
        for (String s : allTags.elementSet()) {
            int size = getSize(ds, allTags.count(s));
            System.err.printf("%d -> %d\n", allTags.count(s), size);
            freqs.put(size, s);
        }

        List<Integer> ordered = new ArrayList<Integer>(freqs.keySet());
        Collections.sort(ordered);
        Collections.reverse(ordered);

        StringBuilder html = new StringBuilder();
        html.append(JavaSource2HTMLLineHighlightingConverter.HTML_SITE_HEADER);

        for (Integer size : ordered) {
            for (String s : freqs.get(size)) {
                //html.append(String.format("<font size=\"%d\">%s</font>&nbsp;", size, s));
            }
            //html.append("<br>");
        }


/*
        html.append(String.format("<font size=\"%d\">%s</font><br>", 2, "2"));
        html.append(String.format("<font size=\"%d\">%s</font><br>", 3, "3"));
        html.append(String.format("<font size=\"%d\">%s</font><br>", 4, "4"));
        html.append(String.format("<font size=\"%d\">%s</font><br>", 5, "5"));
        html.append(String.format("<font size=\"%d\">%s</font><br>", 6, "6"));
        html.append(String.format("<font size=\"%d\">%s</font><br>", 7, "7"));
        html.append(String.format("<font size=\"%d\">%s</font><br>", 8, "8"));
        html.append(String.format("<font size=\"%d\">%s</font><br>", 9, "9"));
        html.append(String.format("<font size=\"%d\">%s</font><br>", 10, "10"));
        html.append(String.format("<font size=\"%d\">%s</font><br>", 11, "11"));
        html.append(String.format("<font size=\"%d\">%s</font><br>", 12, "12"));
*/

        html.append(allTags.toString());
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
