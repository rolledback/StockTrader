import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;
import java.io.*;

public class ScenarioReader {
    public static void main(String[] args) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("../scenarios/example_scenario.xml");

            Node market = doc.getElementsByTagName("market").item(0);
            NamedNodeMap marketAttributes = market.getAttributes();

            System.out.println("attributes");
            for(int i = 0; i < marketAttributes.getLength(); i++) {
                Node attribute = marketAttributes.item(i);
                System.out.println(attribute.getNodeName());
                // do something
            }

            System.out.println("\nchildren");
            NodeList marketChildren = market.getChildNodes();
            for(int i = 0; i < marketChildren.getLength(); i++) {
                Node child = marketChildren.item(i);
                    System.out.println("name: " + child.getNodeName() + "\nlocal name: " + child.getLocalName() + "\nvalue: " + child.getNodeValue() + "\ntype: " + child.getNodeType() + "\n");
                // do something
            }
        }
        catch(ParserConfigurationException e) {
            e.printStackTrace();
        }
        catch(SAXException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }
}