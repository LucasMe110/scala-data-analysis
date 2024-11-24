import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.knowm.xchart.{PieChartBuilder, BitmapEncoder, PieSeries}
import org.knowm.xchart.style.PieStyler
import java.io.File

// sbt "runMain Churnrate"

object Churnrate {
  def main(args: Array[String]): Unit = {
    // Inicializando Spark
    implicit val spark: SparkSession = SparkSession.builder()
      .appName("Customer Analysis")
      .master("local[*]")
      .getOrCreate()

    // Configurando a zona de tempo da sessão Spark
    spark.conf.set("spark.sql.session.timeZone", "America/Sao_Paulo")

    // Função para ler arquivos CSV
    def readCsv(filePath: String, delimiter: String = ";"): DataFrame = {
      spark.read
        .option("header", "true")
        .option("delimiter", delimiter)
        .option("inferSchema", "true")
        .csv(filePath)
    }

    // Leitura dos arquivos CSV
    val clientesDf = readCsv("data/customers.csv")
    val comprasDf = readCsv("data/orders.csv")
    val firstPurchaseDf = readCsv("data/first_purchase.csv")
    val lastPurchaseDf = readCsv("data/last_purchase.csv")

    // Converter colunas de data para o tipo Date
    val comprasDfWithDate = comprasDf.withColumn("order_date", to_date(col("order_date"), "yyyy-MM-dd"))
    val firstPurchaseDfWithDate = firstPurchaseDf.withColumn("first_order_date", to_date(col("first_order_date"), "yyyy-MM-dd"))
    val lastPurchaseDfWithDate = lastPurchaseDf.withColumn("last_order_date", to_date(col("last_order_date"), "yyyy-MM-dd"))

    // Filtrar clientes que não realizaram compras desde o último dia de dezembro de 1997
    val clientesSemCompraDesdeDezembro1997 = clientesDf
      .join(lastPurchaseDfWithDate.filter(col("last_order_date") <= "1997-12-31"), "customer_id")

    // Filtrar clientes que fizeram sua primeira compra a partir de primeiro de janeiro de 1998
    val clientesPrimeiraCompraJaneiro1998 = clientesDf
      .join(firstPurchaseDfWithDate.filter(col("first_order_date") >= "1998-01-01"), "customer_id")

    // Criar uma tabela com clientes que não estão em nenhuma das duas listas
    val clientesNaoEmLista = clientesDf
      .join(clientesSemCompraDesdeDezembro1997, Seq("customer_id"), "leftanti")
      .join(clientesPrimeiraCompraJaneiro1998, Seq("customer_id"), "leftanti")

    // Contagem dos clientes em cada categoria
    val numClientesSemCompra = clientesSemCompraDesdeDezembro1997.count()
    val numClientesPrimeiraCompra = clientesPrimeiraCompraJaneiro1998.count()
    val numClientesNaoEmLista = clientesNaoEmLista.count()

    // Função para gerar gráfico de pizza
    def generatePieChart(
      numClientesSemCompra: Long,
      numClientesPrimeiraCompra: Long,
      numClientesNaoEmLista: Long
    ): Unit = {
      val chart = new PieChartBuilder().width(800).height(600).title("Churn Rate partindo de 01/1998").build()

      // Configurações de estilo
      chart.getStyler.setLegendVisible(true)
      chart.getStyler.setAnnotationDistance(1.15)
      chart.getStyler.setPlotContentSize(0.7)

      // Definindo estilo de série padrão
      chart.getStyler.setDefaultSeriesRenderStyle(PieSeries.PieSeriesRenderStyle.Pie)

      // Dados do gráfico
      chart.addSeries("Saída de clientes", numClientesSemCompra)
      chart.addSeries("Novos clientes", numClientesPrimeiraCompra)
      chart.addSeries("Clientes retidos", numClientesNaoEmLista)

      // Caminho do arquivo de gráfico
      val filePath = "charts/churn_rate_chart.png"
      try {
        // Salvar o gráfico como imagem
        BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG)
      } catch {
        case e: Exception =>
          println(s"Erro ao salvar o gráfico: ${e.getMessage}")
      }
    }

    // Integrar o gráfico no fluxo de execução
    generatePieChart(numClientesSemCompra, numClientesPrimeiraCompra, numClientesNaoEmLista)

    // Encerrar sessão Spark
    spark.stop()
  }
}
