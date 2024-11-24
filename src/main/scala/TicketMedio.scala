import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import java.io.{File, PrintWriter}

// sbt "runMain TicketMedio"

object TicketMedio {
  def main(args: Array[String]): Unit = {
    // Inicializa Spark
    implicit val spark: SparkSession = SparkSession.builder()
      .appName("Ticket Medio Calculation")
      .master("local[*]")
      .getOrCreate()

    // Define o nível de log para erro
    spark.sparkContext.setLogLevel("ERROR")

    // Função para ler arquivos CSV com tratamento de erro
    def readCsv(filePath: String, delimiter: String = ";"): Option[DataFrame] = {
      try {
        Some(
          spark.read
            .option("header", "true")
            .option("delimiter", delimiter)
            .option("inferSchema", "true")
            .csv(filePath)
        )
      } catch {
        case e: Exception =>
          println(s"Erro ao ler $filePath: ${e.getMessage}")
          None
      }
    }

    // Carrega os DataFrames e verifica se foram carregados com sucesso
    val clientesDf = readCsv("data/customers.csv")
    val comprasDf = readCsv("data/orders.csv")
    val detalhesDf = readCsv("data/order_details.csv")

    // Verifica se todos os DataFrames foram carregados corretamente
    if (clientesDf.isDefined && comprasDf.isDefined && detalhesDf.isDefined) {
      println("Todos os arquivos CSV foram carregados com sucesso.")

        // Realiza o cálculo do ticket médio
      val ticketMedioDf = detalhesDf.get
        .groupBy("order_id")
        .agg(sum(col("unit_price") * col("quantity") * (lit(1.0) - col("discount"))).as("total_value"))

      // Escreve o resultado em um arquivo TXT
      val filePath = "charts/ticket_medio.txt"
      try { 
        val writer = new PrintWriter(new File(filePath))
        ticketMedioDf.collect().foreach(row => writer.println(row))
        writer.close()
        println(s"Arquivo $filePath criado com sucesso.")
      } catch {
        case e: Exception =>
          println(s"Erro ao escrever o arquivo de ticket médio: ${e.getMessage}")
      }
    } else {
      println("Falha ao carregar um ou mais arquivos CSV. Verifique o formato e a existência dos arquivos.")
    }

    // Encerra a sessão Spark
    spark.stop()
  }
}
