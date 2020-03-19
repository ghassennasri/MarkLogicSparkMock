import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
    private final static Logger LOGGER=LogManager.getRootLogger();

    public static void main(String[] args) throws IOException, AnalysisException {
        String propFileName = "config.properties";
        Properties prop = new Properties();
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(propFileName);

        if (inputStream != null) {
            prop.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        // get MarkLogic client connection properties
        String host = prop.getProperty("host");
        String port = prop.getProperty("port");
        String username = prop.getProperty("username");
        String password = prop.getProperty("password");
        //MarkLogic database connection client
        DatabaseClient client = DatabaseClientFactory.newClient(
                host, Integer.parseInt(port),
                new DatabaseClientFactory.BasicAuthContext(username,password));
        //MarkLogic java client api XMLDocumentManager
        XMLDocumentManager docMgr = client.newXMLDocumentManager();
        //parser that produces DOM object trees from XML documents
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentMetadataHandle collectionsMetadata = new DocumentMetadataHandle()
                .withCollections("Books");

        SparkSession spark = SparkSession.builder().getOrCreate();
        //custom
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
        //playing with dataframe -- selecting and displaying books for which the price is less than 10$
        df.createGlobalTempView("book");
        Dataset<Row> booksCheaperThan10 = spark.sql("SELECT _id, title, author, genre, price FROM global_temp.book WHERE price <=10");
        booksCheaperThan10.show();
        //looping through book dataset and writing documents to MarkLogic
        books.collectAsList().forEach(x->{
            XStream xStream=new XStream(new DomDriver());
            //XML String representation of a book row
            System.out.println(xStream.toXML(x));

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                //convert xml string to org.w3c.dom.Document
                Document doc = builder.parse(new InputSource(new StringReader(xStream.toXML(x))));
                //uri of the the book document
                String docId = "/Books/"+x.get_id()+".xml";
                //write to MarkLogic

                docMgr.write(docId,collectionsMetadata,new DOMHandle(doc));
            }catch (Exception e){
                LOGGER.error(e);
            }

        });
        if (client!=null)
            client.release();


    }
}
