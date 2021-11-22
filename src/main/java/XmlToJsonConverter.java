import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class XmlToJsonConverter {

    public static void main(String[] args) {
        final ClassLoader classLoader = XmlToJsonConverter.class.getClassLoader();
        final String fileName = Objects.requireNonNull(classLoader.getResource("plant_catalog.xml")).getFile();
        try (InputStream stream = new FileInputStream(fileName)) {
            XMLInputFactory inputFactory = XMLInputFactory.newFactory();
            inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);

            XMLStreamReader reader = inputFactory.createXMLStreamReader(stream);

            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT:
                        System.out.println("Start " + reader.getName());
                        for (int i = 0, count = reader.getAttributeCount(); i < count; i++) {
                            System.out.println(reader.getAttributeName(i) + "=" + reader.getAttributeValue(i));
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        System.out.println("End " + reader.getName());
                        break;
                    case XMLStreamConstants.CHARACTERS:
                    case XMLStreamConstants.SPACE:
                        String text = reader.getText();
                        if (!text.trim().isEmpty()) {
                            System.out.println("text: " + text);
                        }
                        break;
                }
            }
        } catch (IOException | XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
