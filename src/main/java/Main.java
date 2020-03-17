import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

public class Main {
    public static void main(String[] args) {

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
            XStream xStream=new XStream();
            System.out.println(xStream.toXML(x));
        });

       /*df.select("author", "_id").write()
                .format("xml")
                .option("rootTag", "catalog")
                .option("rowTag", "book")
                .save("file:///opt/spark-data/newbooks.xml");
        df.show();*/
    }
}
