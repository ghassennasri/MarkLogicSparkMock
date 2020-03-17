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
       /* SparkSession spark = SparkSession
                .builder()
                .appName("JavaSparkPi")
                .getOrCreate();

        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        int slices = (args.length == 1) ? Integer.parseInt(args[0]) : 2;
        int n = 100000 * slices;
        List<Integer> l = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            l.add(i);
        }

        JavaRDD<Integer> dataSet = jsc.parallelize(l, slices);

        int count = dataSet.map(integer -> {
            double x = Math.random() * 2 - 1;
            double y = Math.random() * 2 - 1;
            return (x * x + y * y <= 1) ? 1 : 0;
        }).reduce((integer, integer2) -> integer + integer2);

        System.out.println("Pi is roughly " + 4.0 * count / n);

        spark.stop();
    }*/
        //String path = Main.class.getClassLoader().getResource("books.xml").getPath();

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
                //.textFile("file:///opt/spark-data/books.xml").
                //.load("file:///opt/spark-data/books.xml");

        Dataset<Book> books=df.as(Encoders.bean(Book.class));
        df.show();
        books.collectAsList().forEach(System.out::println);

       /*df.select("author", "_id").write()
                .format("xml")
                .option("rootTag", "catalog")
                .option("rowTag", "book")
                .save("file:///opt/spark-data/newbooks.xml");
        df.show();*/
    }
}
