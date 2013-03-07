package exapus.model.forest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import exapus.gui.editors.forest.graph.INode;
import exapus.model.metrics.IMetricValue;
import exapus.model.metrics.MetricType;
import exapus.model.store.Store;
import exapus.model.tags.Cloud;
import exapus.model.tags.Tag;

public abstract class ForestElement implements INode {
	
    private Map<MetricType, IMetricValue> metrics = new HashMap<MetricType, IMetricValue>();

    private Cloud tags = Cloud.EMPTY_CLOUD;
    
	private ForestElement parent;

	private UqName id;
	
	private QName qid;

    private Multiset<String> allTags = HashMultiset.create();
    private Multiset<String> allDualTags = HashMultiset.create();
		
	public ForestElement(UqName id) {
		this.id = id;
	}
	
	public void setName(UqName id) {
		this.id = id;
	}
	

	public UqName getName() {
		return id;
	}

	public QName getQName() {
		if(qid == null) {
			LinkedList<String> components = new LinkedList<String>();
			ForestElement parent = this;
			while (parent != null && !(parent instanceof PackageTree)) {
				components.addFirst(parent.getName().toString());
				parent = parent.getParent();
			}
			qid = new QName(components);
		}
		return qid;
	}
	
	public void setQName(QName qid) {
		this.qid = qid;
	}

	public ForestElement getParent() {
		return parent;
	}
	
	public List<ForestElement> getAncestors() {
		LinkedList<ForestElement> ancestors = new LinkedList<ForestElement>();
		ForestElement parent = this.getParent();
		while(parent != null) {
			ancestors.addFirst(parent);
			parent = parent.getParent();
		}
		return ancestors;
	}
 
	public void setParent(ForestElement parent) {
		this.parent = parent;
	}

	public PackageLayer getParentPackageLayer() {
		ForestElement parent = getParent();
		while (parent != null && !(parent instanceof PackageLayer))
			parent = parent.getParent();
		return (PackageLayer) parent;
	}

	public Member getParentMember() {
		ForestElement parent = getParent();
		while (parent != null && !(parent instanceof Member))
			parent = parent.getParent();
		return (Member) parent;
	}
	
	public Member getParentTypeDeclaringMember() {
		Member parentMember = getParentMember();
		if(parentMember == null)
			return null;
		if(parentMember.getElement().declaresType()) {
			return parentMember;
		} else {
			return parentMember.getParentMember();
		}
	}
	
	public Member getParentMethodMember() {
		Member parentMember = getParentMember();
		if(parentMember == null)
			return null;
		if(parentMember.getElement().isMethod()) {
			return parentMember;
		} else {
			return parentMember.getParentMethodMember();
		}
	}


	public PackageTree getParentPackageTree() {
		ForestElement parent = getParent();
		while (parent != null && !(parent instanceof PackageTree))
			parent = parent.getParent();
		return (PackageTree) parent;
	}

	public FactForest getParentFactForest() {
		return getParentPackageTree().getParentFactForest();
	}
	
	
	
	public ExapusModel getExapusModel() {
		return getParentFactForest().getModel();
	}
	
	public InboundFactForest getAPICentricForest() {
		return getExapusModel().getAPICentricForest();
	}


	public InboundFactForest getProjectCentricForest() {
		return getExapusModel().getAPICentricForest();
	}

	public FactForest getDualFactForest() {
		return getParentFactForest().getDualFactForest();
	}

	public IProject getCorrespondingIProject() {
		PackageTree packageTree = getParentPackageTree();
		IProject project = packageTree.getCorrespondingIProject();
		return project;
	}

	public IJavaProject getCorrespondingJavaProject() {
		return JavaCore.create(getCorrespondingIProject());
	}

	public String getSourceString() {
		ICompilationUnit icu = getCorrespondingICompilationUnit();
		if(icu == null)
			return null;
		try {
			return icu.getSource();
		} catch (JavaModelException e) {
			e.printStackTrace();
			return null;
		}
	}

	public int getSourceCharacterIndexOffset() {
		return 0;
	}

	public int getSourceLineNumberOffset() {
		return 0;
	}

    public IMetricValue getMetric(MetricType type) {
        return metrics.get(type);
    }

    public void setMetric(IMetricValue metricValue) {
        metrics.put(metricValue.getType(), metricValue);
    }

    public Set<MetricType> getRegisteredMetrics() {
        return metrics.keySet();
    }

    public Cloud getTags() {
    	return tags;
    }

    public boolean hasTag(Tag tag) {
    	return tags.hasTag(tag);
    }

    public boolean addTag(Tag tag) {
    	Cloud before = tags;
    	tags = Store.getCurrent().getOrRegisterExtendedCloud(tags, tag);
    	return tags != before;
    }
    
    public void copyTagsFrom(ForestElement e) {
    	tags = e.tags;
    }

    public void addTagToAll(Cloud tags) {
        Multiset<String> multiset = tags.toMultiset();
        for (String s : multiset.elementSet()) {
            this.allTags.add(s, multiset.count(s));
        }

        if (this.parent != null) this.parent.addTagToAll(tags);
    }

    public void addDualTagToAll(Cloud tags) {
        Multiset<String> multiset = tags.toMultiset();
        for (String s : multiset.elementSet()) {
            this.allDualTags.add(s, multiset.count(s));
        }

        if (this.parent != null) this.parent.addDualTagToAll(tags);
    }

	public ForestElement getCorrespondingForestElement(boolean copyWhenMissing, Iterator<ForestElement> ancestors, ForestElement element) {
		ForestElement ancestor = ancestors.next();
		ForestElement correspondingAncestor = getCorrespondingForestElement(copyWhenMissing, ancestor);
		if(correspondingAncestor == null)
			return null;
		if(ancestors.hasNext())
			return correspondingAncestor.getCorrespondingForestElement(copyWhenMissing, ancestors, element);
		return correspondingAncestor.getCorrespondingForestElement(copyWhenMissing, element);
	}

    public Cloud getDualTags() {
        return Cloud.EMPTY_CLOUD;
    }

	abstract public ForestElement getCorrespondingForestElement(boolean copyWhenMissing, ForestElement ancestor);

    public abstract ICompilationUnit getCorrespondingICompilationUnit();

    public Multiset<String> getAllTags() {
        return allTags;
    }

    public Multiset<String> getAllDualTags() {
        return allDualTags;
    }
}
