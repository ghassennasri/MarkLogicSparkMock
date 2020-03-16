import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;


import java.util.ArrayList;
import java.util.List;

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
        SparkSession spark = SparkSession.builder().getOrCreate();
        Dataset<Row> df = spark.read()
                .format("xml")
                .option("rowTag", "book")
                .load("books.xml");

        df.select("author", "_id").write()
                .format("xml")
                .option("rootTag", "catalog")
                .option("rowTag", "book")
                .save("newbooks.xml");
        df.show();
    }
}
