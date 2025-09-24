package org.github.oleksandrkukotin.xml2json.converter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class XmlToJsonProcessorTest {

    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    @Test
    void textOnlyElement_collapsesToScalar() throws Exception {
        String xml = "<root><title>Hello</title></root>";
        Document doc = parseXml(xml);

        XmlToJsonProcessor processor = new XmlToJsonProcessor();
        JSONObject json = processor.convert(doc);

        assertEquals("Hello", json.getJSONObject("root").get("title"));
    }

    @Test
    void elementWithAttributeAndText_keepsTextUnderHashText() throws Exception {
        String xml = "<root><price currency=\"USD\">29.99</price></root>";
        Document doc = parseXml(xml);

        XmlToJsonProcessor processor = new XmlToJsonProcessor();
        JSONObject json = processor.convert(doc);

        JSONObject price = json.getJSONObject("root").getJSONObject("price");
        assertEquals(29.99, price.get("#text"));
        assertEquals("USD", price.get("@currency"));
    }

    @Test
    void repeatedChildren_becomeJsonArray() throws Exception {
        String xml = "<root><item>a</item><item>b</item></root>";
        Document doc = parseXml(xml);

        XmlToJsonProcessor processor = new XmlToJsonProcessor();
        JSONObject json = processor.convert(doc);

        Object itemsObj = json.getJSONObject("root").get("item");
        assertInstanceOf(JSONArray.class, itemsObj);
        JSONArray items = (JSONArray) itemsObj;
        assertEquals("a", items.get(0));
        assertEquals("b", items.get(1));
    }

    @Test
    void nestedStructure_arraysAndScalars_expectedShape() throws Exception {
        String xml =
            "<library>" +
            "  <books>" +
            "    <book id=\"1\" genre=\"fiction\">" +
            "      <title>The Adventure</title>" +
            "      <chapters>" +
            "        <chapter number=\"1\"><section><title>T1</title><content>C1</content></section></chapter>" +
            "        <chapter number=\"2\"><section><title>T2</title><content>C2</content></section></chapter>" +
            "      </chapters>" +
            "      <price currency=\"USD\">19.99</price>" +
            "    </book>" +
            "  </books>" +
            "</library>";

        Document doc = parseXml(xml);
        XmlToJsonProcessor processor = new XmlToJsonProcessor();
        JSONObject out = processor.convert(doc);

        JSONObject library = out.getJSONObject("library");
        JSONObject book0 = library.getJSONObject("books").getJSONObject("book");
        assertEquals("fiction", book0.get("@genre"));
        assertEquals("1", book0.get("@id"));
        assertEquals("The Adventure", book0.get("title"));

        JSONObject price = book0.getJSONObject("price");
        assertEquals(19.99, price.get("#text"));
        assertEquals("USD", price.get("@currency"));

        JSONArray chapters = book0.getJSONObject("chapters").getJSONArray("chapter");
        assertEquals(2, chapters.length());

        JSONObject ch1 = chapters.getJSONObject(0);
        assertEquals("1", ch1.get("@number"));
        Object sectionObj = ch1.get("section");
        JSONObject section1 = sectionObj instanceof JSONArray ? ((JSONArray) sectionObj).getJSONObject(0) : (JSONObject) sectionObj;
        assertEquals("T1", section1.get("title"));
        assertEquals("C1", section1.get("content"));
    }
}


