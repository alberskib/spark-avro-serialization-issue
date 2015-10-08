package pl.example.spark

import com.esotericsoftware.kryo.Kryo
import com.twitter.chill.avro.AvroSerializer
import org.apache.avro.Schema
import org.apache.avro.mapred.AvroKey
import org.apache.avro.mapreduce.{AvroKeyInputFormat, AvroJob}
import org.apache.hadoop.io.NullWritable
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
import org.apache.spark.serializer.KryoRegistrator
import org.apache.spark.{SparkConf, SparkContext}
import pl.example.avro.Data

import scala.reflect.ClassTag

object TestClass {
  def main(args: Array[String]) {
    val input = args(0)
    val output = args(1)
    val output2 = args(2)
    val spark = createContext("TestAPP")
    val rddCached  = loadFormHdfs[Data](input, spark, Data.SCHEMA$).cache()
    val rdd  = loadFormHdfs[Data](input, spark, Data.SCHEMA$)
    println("Count with caching: " + rddCached.map(x => x.getUserId + x.getName).distinct().count())
    println("Count without caching: " + rdd.map(x => x.getUserId + x.getName).distinct().count())
    rdd.saveAsTextFile(output)
    rddCached.saveAsTextFile(output2)
  }


  def createContext(name: String) = {
    val sc = new SparkConf()
      .setAppName(name)
      .set("spark.yarn.executor.memoryOverhead", "1024")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryo.registrator", classOf[MyRegistrator].getCanonicalName)
      .set("spark.io.compression.codec", "lzf")
      .set("spark.speculation", "true")
    new SparkContext(sc)
  }

  def loadFormHdfs[T](inputPath: String, spark: SparkContext, schema: Schema)(implicit tag: ClassTag[T]) = {
    val job = Job.getInstance(spark.hadoopConfiguration)
    FileInputFormat.setInputPaths(job, inputPath)
    AvroJob.setInputKeySchema(job, schema)

    spark.newAPIHadoopRDD(job.getConfiguration,
      classOf[AvroKeyInputFormat[T]],
      classOf[AvroKey[T]],
      classOf[NullWritable]).map(_._1.datum())
  }
}

class MyRegistrator extends KryoRegistrator {
  override def registerClasses(kryo: Kryo) {
    kryo.register(classOf[Data], AvroSerializer.SpecificRecordBinarySerializer[Data])
  }
}
