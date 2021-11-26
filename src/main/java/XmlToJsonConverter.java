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
        ClassLoader classLoader = XmlToJsonConverter.class.getClassLoader();
        String fileName = classLoader.getResource("input.xml").getFile();
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        JSONObject resultArray = new JSONObject();
        try {
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(new File(fileName));
            NodeList nodeList = document.getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    JSONObject tempObject = new JSONObject();
                    tempObject.put(node.getNodeName(), node.getTextContent());
                    resultArray.put(tempObject.toString(), " ");
                }
            }
            File result = new File("result.json");
            FileWriter writer = new FileWriter(result);
            writer.write(resultArray.toString());
            writer.close();
            System.out.println(resultArray.toString());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
