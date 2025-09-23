package org.github.oleksandrkukotin.xml2json.converter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Encapsulates XML to JSON conversion logic.
 */
public final class XmlToJsonProcessor {

    public JSONObject convert(Document document) {
        Node rootNode = document.getDocumentElement();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(rootNode.getNodeName(), convertElement(rootNode));
        return jsonObject;
    }

    private Object convertElement(Node element) {
        JSONObject resultObject = new JSONObject();

        NamedNodeMap attributes = element.getAttributes();
        if (attributes != null) {
            for (int i = 0; i < attributes.getLength(); i++) {
                Node attr = attributes.item(i);
                resultObject.put("@" + attr.getNodeName(), attr.getNodeValue());
            }
        }

        NodeList childNodes = element.getChildNodes();
        boolean hasElementChildren = false;
        StringBuilder textBuffer = new StringBuilder();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                hasElementChildren = true;
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getNodeValue();
                if (text != null) {
                    textBuffer.append(text.trim());
                }
            }
        }

        String textContent = textBuffer.toString();
        if (!hasElementChildren && resultObject.isEmpty()) {
            if (!textContent.isEmpty()) {
                return parseScalar(textContent);
            }
            return JSONObject.NULL;
        }

        if (!textContent.isEmpty()) {
            resultObject.put("#text", parseScalar(textContent));
        }

        java.util.Map<String, java.util.List<Object>> nameToValues = new java.util.LinkedHashMap<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node child = childNodes.item(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String childName = child.getNodeName();
            Object childValue = convertElement(child);
            nameToValues.computeIfAbsent(childName, k -> new java.util.ArrayList<>()).add(childValue);
        }

        for (java.util.Map.Entry<String, java.util.List<Object>> entry : nameToValues.entrySet()) {
            String name = entry.getKey();
            java.util.List<Object> values = entry.getValue();
            if (values.size() == 1) {
                resultObject.put(name, values.get(0));
            } else {
                resultObject.put(name, new JSONArray(values));
            }
        }

        return resultObject;
    }

    private Object parseScalar(String text) {
        if (text == null) {
            return JSONObject.NULL;
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        try {
            if (trimmed.contains(".")) {
                return Double.parseDouble(trimmed);
            } else {
                return Long.parseLong(trimmed);
            }
        } catch (NumberFormatException ignored) {
        }
        return trimmed;
    }
}


