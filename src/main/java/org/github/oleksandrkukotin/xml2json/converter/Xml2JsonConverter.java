package org.github.oleksandrkukotin.xml2json.converter;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
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
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        final File resultFile = new File(OUTPUT_FILE_NAME);
        try (FileWriter fileWriter = new FileWriter(resultFile)) {
            final Path path = Paths.get(ClassLoader.getSystemResource(INPUT_FILE_NAME).toURI());
            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document document = docBuilder.parse(Files.newInputStream(path));
            XmlToJsonProcessor processor = new XmlToJsonProcessor();
            final JSONObject jsonObject = processor.convert(document);
            fileWriter.write(jsonObject.toString(4));
        } catch (ParserConfigurationException | SAXException | IOException | URISyntaxException e) {
            logger.error("An error occurred: ", e);
        }
    }
}