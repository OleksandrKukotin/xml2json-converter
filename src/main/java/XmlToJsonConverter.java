import org.json.JSONArray;
import org.json.JSONObject;
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

public class XmlToJsonConverter {

    public static void main(String[] args) {
        final ClassLoader classLoader = XmlToJsonConverter.class.getClassLoader();
        final String fileName = classLoader.getResource("input.xml").getFile();
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document document = docBuilder.parse(new File(fileName));
            final Node firstChild = document.getFirstChild();

            JSONArray resultJson = new JSONArray();
            handleChildren(firstChild, resultJson);
            System.out.println(resultJson);
            File result = new File("result.json");
            FileWriter fileWriter = new FileWriter(result);
            fileWriter.write(resultJson.toString());
            fileWriter.close();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleChildren(Node node, JSONArray jsonArray) {
        JSONObject tempObject = new JSONObject();
        if (!node.hasChildNodes()) {
            return;
        }

        NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                tempObject.put(item.getNodeName(), item.getLastChild().getTextContent());
                System.out.println(item.getNodeName() + " : " + item.getFirstChild().getTextContent());
                handleChildren(item, jsonArray);
            }
        }
        if (!tempObject.isEmpty()){
            jsonArray.put(tempObject);
        }
    }
}
