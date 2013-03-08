package exapus.model.tags;

import java.util.*;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import exapus.model.forest.Member;

public class Cloud {

	private Cloud() {
		this.tags = new TreeSet<Tag>();
	}
	
	private Cloud(Cloud c) {
		this(c.tags);
	}
	
	private Cloud(SortedSet<Tag> tags) {
		this.tags = new TreeSet<Tag>(tags);
	}
	
	
	public String getCanonicalString() {
        List<String> names = new ArrayList<String>();
        for (Tag tag : tags) {
            //System.err.println(tag.toDebugString());
            names.add(tag.getLabelName());
        }

		return Joiner.on(',').join(names);
	}
	
	public String toString() {
		return getCanonicalString();
	}
		
	private TreeSet<Tag> tags;

    public Multiset<String> toMultiset(boolean onlySuper) {
        Multiset<String> tags = HashMultiset.create();
        for (Tag tag : this.tags) {
            if ((onlySuper && tag.isSuperTag()) || !onlySuper)
                tags.add(tag.getIdentifier());
        }
        return tags;
    }

    public Multiset<String> toMultiset() {
        return toMultiset(false);
    }

	private boolean add(Tag t) {
		return tags.add(t);
	}
	
	private boolean add(Cloud c) {
		return tags.addAll(c.tags);
	}
	
	public boolean hasTag(Tag t) {
		return tags.contains(t);
	}
	
	static public Cloud from(Tag t) {
		Cloud cloud = new Cloud();
		cloud.add(t);
		return cloud;
	}
	
	static public Cloud from(Cloud c, Tag t) {
		Cloud clone = new Cloud(c);
		clone.add(t);
		return clone;
	}
	
	static public Cloud from(Cloud c, Cloud tobeadded) {
		Cloud clone = new Cloud(c);
		clone.add(tobeadded);
		return clone;
	}
	
	static public Cloud EMPTY_CLOUD;
	
	static {
		EMPTY_CLOUD = new Cloud();
	}
	
}
