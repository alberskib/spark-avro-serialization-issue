# spark-avro-serialization-issue

Minimal, Complete, and Verifiable example enabling to reproduce issue with spark, serialization and classes generated from avro idl.

## Usage

Repository contains source code as well as input data in order to reproduce error. The only thing you need is to download spark i.e 1.3 for hadoop 2.6 or later.

After preparing spark you can follow next steps:
 - generate avro classes (`./sbt avro:generate` from project main dir)
 - create package (`./sbt assembly` from project main dir)
 - invoke command
 ```
 $SPARK_HOME bin/spark-submit --class  pl.example.spark.TestClass --master local[4] target/scala-2.10/spark-avro-issue-assembly-0.0.1-SNAPSHOT.jar \
 file:///direct_path_to_project_main_dir/testData.avro file:///direct_path_to_output1 file:///direct_path_to_output1
 ```

 As a result two directories will be created with results:
  - `direct_path_to_output1` containing correct results for command without `cache()`
  - `direct_path_to_output2` containing correct results for command with `cache()`


