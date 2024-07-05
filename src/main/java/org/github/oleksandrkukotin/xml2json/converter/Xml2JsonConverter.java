package org.github.oleksandrkukotin.xml2json.converter;

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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Xml2JsonConverter {

    private static final Logger logger = LoggerFactory.getLogger(Xml2JsonConverter.class);
    public static final String INPUT_FILE_NAME = "file.xml";
    public static final String OUTPUT_FILE_NAME = "result.json";

    public static void main(String[] args) {
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newDefaultNSInstance();
        final File resultFile = new File(OUTPUT_FILE_NAME);
        try (FileWriter fileWriter = new FileWriter(resultFile)) {
            final Path path = Paths.get(ClassLoader.getSystemResource(INPUT_FILE_NAME).toURI());
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document document = docBuilder.parse(Files.newInputStream(path));
            final Node rootNode = document.getDocumentElement(); // Use getDocumentElement() for the root element
            final JSONObject jsonObject = parseNode(rootNode);
            fileWriter.write(jsonObject.toString(4)); // Pretty print JSON with 4-space indentation
        } catch (ParserConfigurationException | SAXException | IOException | URISyntaxException e) {
            logger.error("An error occurred: ", e);
        }
    }

    private static JSONObject parseNode(Node node) {
        JSONObject jsonObject = new JSONObject();
        handleNode(node, jsonObject);
        return jsonObject;
    }

    private static void handleNode(Node node, JSONObject jsonObject) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            handleElementNode(node, jsonObject);
        } else if (node.getNodeType() == Node.TEXT_NODE && !node.getNodeValue().trim().isEmpty()) {
            jsonObject.put("#text", node.getNodeValue().trim());
        } else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            handleAttributeNode(node, jsonObject);
        }
        // Other node types can be handled here as needed
    }

    private static void handleElementNode(Node node, JSONObject jsonObject) {
        JSONObject elementObject = new JSONObject();

        // Handle attributes
        NamedNodeMap attributes = node.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                elementObject.put("@" + attr.getNodeName(), attr.getNodeValue());
            }
        }

        // Handle children
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            handleChildNode(child, elementObject);
        }

        // Add element object to parent JSON object
        String nodeName = node.getNodeName();
        if (jsonObject.has(nodeName)) {
            Object existingValue = jsonObject.get(nodeName);
            if (existingValue instanceof JSONObject) {
                // Convert existing value to array and add new object
                jsonObject.put(nodeName, new JSONObject().put("0", existingValue).put("1", elementObject));
            } else {
                // Add new object to existing array
                ((JSONObject) existingValue).put(String.valueOf(((JSONObject) existingValue).length()), elementObject);
            }
        } else {
            jsonObject.put(nodeName, elementObject);
        }
    }

    private static void handleChildNode(Node child, JSONObject elementObject) {
        String childName = child.getNodeName();
        if (child.getNodeType() == Node.ELEMENT_NODE) {
            if (elementObject.has(childName)) {
                // Convert existing value to array if needed and add new object
                Object existingValue = elementObject.get(childName);
                if (existingValue instanceof JSONObject) {
                    elementObject.put(childName, new JSONObject().put("0", existingValue).put("1", parseNode(child)));
                } else {
                    ((JSONObject) existingValue).put(String.valueOf(((JSONObject) existingValue).length()), parseNode(child));
                }
            } else {
                elementObject.put(childName, parseNode(child));
            }
        } else if (child.getNodeType() == Node.TEXT_NODE && !child.getNodeValue().trim().isEmpty()) {
            elementObject.put("#text", child.getNodeValue().trim());
        }
    }

    private static void handleAttributeNode(Node node, JSONObject jsonObject) {
        // Not used in current implementation as attributes are handled within elements
        jsonObject.put("@" + node.getNodeName(), node.getNodeValue());
    }
}