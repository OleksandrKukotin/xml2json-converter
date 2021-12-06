import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class XmlToJsonConverter {

    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(XmlToJsonConverter.class);
        final ClassLoader classLoader = XmlToJsonConverter.class.getClassLoader();
        final String fileName = Objects.requireNonNull(classLoader.getResource("input.xml")).getFile();
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document document = docBuilder.parse(new File(fileName));
            final Node firstChild = document.getFirstChild();

            final JSONObject jsonObject = handleChildren(firstChild);
            final File resultFile = new File("result.json");
            FileWriter fileWriter = new FileWriter(resultFile);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();

        } catch (ParserConfigurationException e) {
            logger.error("Error in the XML parser configuration");
        } catch (SAXException e) {
            logger.error("Error during reading the XML file");
        } catch (IOException e) {
            logger.error("Reading or writing file error");
        }
    }

    private static JSONObject handleChildren(Node node) {
        final JSONObject jsonObject = new JSONObject();

        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node item = childNodes.item(i);

            final String textContent = item.getTextContent();

            switch (item.getNodeType()){
                case Node.ELEMENT_NODE:
                    jsonObject.accumulate(item.getNodeName(), handleChildren(item));
                    if (item.hasAttributes()){
                        System.out.println("Tak");
                        NamedNodeMap attributes = item.getAttributes();
                        System.out.println();
                    }
                    break;
                case Node.TEXT_NODE:
                    if (!textContent.trim().isEmpty()) {
                        jsonObject.put(node.getNodeName(), textContent);
                    }
                    break;
            }
        }
        // TODO: handle tag attributes

        return jsonObject;
    }
}
