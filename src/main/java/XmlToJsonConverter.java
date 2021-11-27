import org.json.JSONObject;
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

            final JSONObject jsonObject = handleChildren(firstChild);
            // TODO: write to a Json file

        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO: handle exceptions
            e.printStackTrace();
        }
    }

    private static JSONObject handleChildren(Node node) {
        final JSONObject jsonObject = new JSONObject();

        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);

            final String textContent = item.getTextContent();

            if (item.getNodeType() == Node.ELEMENT_NODE) {
                jsonObject.accumulate(item.getNodeName(), handleChildren(item));
            } else if (item.getNodeType() == Node.TEXT_NODE && !textContent.trim().isEmpty()) {
                jsonObject.put(node.getNodeName(), textContent);
            }
        }

        // TODO: handle tag attributes

        return jsonObject;
    }
}
