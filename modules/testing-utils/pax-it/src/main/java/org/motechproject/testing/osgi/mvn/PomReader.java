package org.motechproject.testing.osgi.mvn;

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

    public PomReader(String pomFilePath) {
        this.pomFilePath = pomFilePath;
    }

    public String getArtifactId() {
        return getPomInfo("artifactId");
    }

    public String getVersion() {
        return getPomInfo("version");
    }

    public String getGroupId() {
        String groupId = getPomInfo("groupId");
        return (isBlank(groupId)) ? "org.motechproject" : groupId;
    }

    private String getPomInfo(String tagName) {
        try {
            final Element root = getPomDoc().getDocumentElement();
            final Node node = getChild(root, tagName);
            return getNodeContent(node, Node.TEXT_NODE);
        } catch (Exception e) {
            return null;
        }
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

    private Document getPomDoc() throws IOException, SAXException, ParserConfigurationException {
        if (pomDoc == null) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setIgnoringComments(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            pomDoc = documentBuilder.parse(pomFilePath);
        }
        return pomDoc;
    }

    private static boolean isBlank(String string) {
        return string == null || string.isEmpty();
    }
}
