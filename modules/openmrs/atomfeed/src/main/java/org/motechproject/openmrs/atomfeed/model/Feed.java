package org.motechproject.openmrs.atomfeed.model;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("feed")
public class Feed {
    private String updated;
    private String versionId;
    private String title;
    private String id;
    private String entriesSize;

    @XStreamImplicit(itemFieldName = "link")
    private List<Link> link = new ArrayList<Link>();

    @XStreamImplicit(itemFieldName = "entry")
    private List<Entry> entry = new ArrayList<Entry>();

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public List<Entry> getEntry() {
        return entry;
    }

    public void setEntry(List<Entry> entry) {
        this.entry = entry;
    }

    public void add(Entry entry) {
        this.entry.add(entry);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public List<Link> getLink() {
        return link;
    }

    public void setLink(List<Link> link) {
        this.link = link;
    }

    public void add(Link link) {
        this.link.add(link);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntriesSize() {
        return entriesSize;
    }

    public void setEntriesSize(String entriesSize) {
        this.entriesSize = entriesSize;
    }
}
