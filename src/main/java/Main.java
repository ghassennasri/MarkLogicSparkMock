import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;


public class Main {
    public static void main(String[] args) throws IOException {
        String propFileName = "config.properties";
        Properties prop = new Properties();
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }



        // get the property value and print it out
        String host = prop.getProperty("host");
        String port = prop.getProperty("port");
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        DatabaseClient client = DatabaseClientFactory.newClient(
                host, Integer.parseInt(port),
                new DatabaseClientFactory.BasicAuthContext(username,password));
        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        SparkSession spark = SparkSession.builder().getOrCreate();
        StructType customSchema = new StructType(new StructField[] {
                new StructField("_id", DataTypes.StringType, true, Metadata.empty()),
                new StructField("author", DataTypes.StringType, true, Metadata.empty()),
                new StructField("description", DataTypes.StringType, true, Metadata.empty()),
                new StructField("genre", DataTypes.StringType, true, Metadata.empty()),
                new StructField("price", DataTypes.DoubleType, true, Metadata.empty()),
                new StructField("publish_date", DataTypes.StringType, true, Metadata.empty()),
                new StructField("title", DataTypes.StringType, true, Metadata.empty())
        });
        Dataset<Row> df = spark.read()
                .format("xml")
                .option("rowTag", "book")
                .schema(customSchema)
                .load("file:///opt/spark-data/books.xml");
        Dataset<Book> books=df.as(Encoders.bean(Book.class));
        df.show();
        books.collectAsList().forEach(x->{
            XStream xStream=new XStream(new DomDriver());
            System.out.println(xStream.toXML(x));
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(xStream.toXML(x))));
                String docId = "/example/"+x.get_id()+".xml";
                docMgr.write(docId,new DOMHandle(doc));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
