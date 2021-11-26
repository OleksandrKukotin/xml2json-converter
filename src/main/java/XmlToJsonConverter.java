import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlToJsonConverter {

    public static void main(String[] args) {
        final ClassLoader classLoader = XmlToJsonConverter.class.getClassLoader();
        final String fileName = classLoader.getResource("input.xml").getFile();
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document document = docBuilder.parse(new File(fileName));
            final Node firstChild = document.getFirstChild();

            System.out.println(firstChild.getNodeName());
            handleChildren(firstChild);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleChildren(Node node) {
        if (!node.hasChildNodes()) {
            return;
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                System.out.println(item.getNodeName());
                handleChildren(item);
            }
        }
    }
}
