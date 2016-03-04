package org.motechproject.testing.osgi.mvn;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commons.api.MotechException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Reads information from the given pom Path
 */
public class PomReader {

    private final String pomFilePath;

    private Document pomDoc;

    /**
     * Initializes the reader using path to the pom that should be read.
     * @param pomFilePath the path to the pom
     */
    public PomReader(String pomFilePath) {
        this.pomFilePath = pomFilePath;
    }

    /**
     * Reads the project artifact id from the pom.
     * @return the project artifact id
     */
    public String getArtifactId() {
        return getPomInfo("artifactId");
    }

    /**
     * Reads the project version from the pom.
     * @return the project version
     */
    public String getVersion() {
        return getPomInfo("version");
    }

    /**
     * Reads the project artifact id from the pom.
     * @return the project artifact id
     */
    public String getGroupId() {
        String groupId = getPomInfo("groupId");
        return (StringUtils.isBlank(groupId)) ? "org.motechproject" : groupId;
    }

    private String getPomInfo(String tagName) {
        final Element root = getPomDoc().getDocumentElement();
        final Node node = getChild(root, tagName);
        return getNodeContent(node, Node.TEXT_NODE);
    }

    private String getNodeContent(Node node, short nodeType) {
        final NodeList nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node item = nodes.item(i);
            if (item.getNodeType() == nodeType) {
                return item.getNodeValue().trim();
            }
        }
        return null;
    }

    private Node getChild(Element root, String tagName) {
        final NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            final Node item = nodes.item(i);
            if (tagName.equals(item.getNodeName())) {
                return item;
            }
        }
        return null;
    }

    private Document getPomDoc() {
        if (pomDoc == null) {
            try {
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                documentBuilderFactory.setIgnoringComments(true);
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                pomDoc = documentBuilder.parse(pomFilePath);
            } catch (SAXException | IOException | ParserConfigurationException e) {
                throw new MotechException("Unable to read the pom file", e);
            }
        }
        return pomDoc;
    }
}
