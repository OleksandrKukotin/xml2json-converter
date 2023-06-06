import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
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

public class Xml2JsonConverter {

    private static final Logger logger = LoggerFactory.getLogger(Xml2JsonConverter.class);

    public static void main(String[] args) {
        final ClassLoader classLoader = Xml2JsonConverter.class.getClassLoader();
        final String fileName = Objects.requireNonNull(classLoader.getResource("input.xml")).getFile();
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        final File resultFile = new File("result.json");
        try (FileWriter fileWriter = new FileWriter(resultFile)) {
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document document = docBuilder.parse(new File(fileName));
            final Node rootNode = document.getFirstChild();
            final JSONObject jsonObject = handleNode(rootNode, new JSONObject());
            fileWriter.write(jsonObject.toString());
        } catch (ParserConfigurationException e) {
            logger.error("Error in the XML parser configuration");
        } catch (SAXException e) {
            logger.error("Error during reading the XML file");
        } catch (IOException e) {
            logger.error("Reading or writing file error");
        }
    }

    private static JSONObject handleNode(Node node, JSONObject jsonObject) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                if (node.hasChildNodes()) {
                    NodeList nodeChildren = node.getChildNodes();
                    for (int i = 0; i < nodeChildren.getLength(); i++) {
                        Node child = nodeChildren.item(i);
                        jsonObject.accumulate(node.getNodeName(), handleNode(child, jsonObject));
                    }
                }
                break;
            case Node.TEXT_NODE:
                String textContent = node.getTextContent();
                if (!textContent.trim().isEmpty()) {
                    jsonObject.put(node.getNodeName(), textContent);
                }
                break;
            case Node.ATTRIBUTE_NODE:
                jsonObject.put(node.getNodeName(), node.getNodeValue());
                break;
            default:
                break;
        }
        // TODO: handle tag attributes
        return jsonObject;
    }
}
