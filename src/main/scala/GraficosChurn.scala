import plotly._, plotly.element._, plotly.layout._, plotly.Plotly


// sbt "runMain HTMLGraficosChurn"
object HTMLGraficosChurn {

  def main(args: Array[String]): Unit = {

    // Dados
    val orderCountsMonths = Seq("2021-01", "2021-02", "2021-03") // exemplo de meses
    val firstOrders = Seq(100, 150, 120)
    val lastOrders = Seq(90, 130, 100)
    val employeeNames = Seq("Alice", "Bob", "Charlie")
    val firstLastRatio = Seq(1.2, 1.5, 1.3)

    // Gráfico de áreas empilhadas para `order_counts`
    val areaChart = Seq(
      Bar(x = orderCountsMonths, y = firstOrders, name = "First Orders", marker = Marker(color = Color.RGB(31, 119, 180))),
      Bar(x = orderCountsMonths, y = lastOrders, name = "Last Orders", marker = Marker(color = Color.RGB(255, 127, 14)))
    )

    val areaLayout = Layout(
      title = "First and Last Purchase Dates by Customer",
      xaxis = Axis(title = "Month"),
      yaxis = Axis(title = "Number of Orders"),
      barmode = BarMode.Stack,
      plot_bgcolor = Color.RGB(35, 39, 44),
      paper_bgcolor = Color.RGB(35, 39, 44),
      font = Font(size = 12, family = "Arial", color = Color.RGB(255, 255, 255))
    )

    Plotly.plot("charts/first_last_purchase_dates_by_customer.html", areaChart, areaLayout)

    // Gráfico de barras para `purchase_count`
    val barChart = Seq(
      Bar(x = employeeNames, y = firstLastRatio, marker = Marker(color = Color.RGB(174, 199, 232)))
    )

    val barLayout = Layout(
      title = "First/Last Purchase Ratio by Employee",
      xaxis = Axis(title = "Employee Name"),
      yaxis = Axis(title = "First/Last Purchase Ratio"),
      plot_bgcolor = Color.RGB(35, 39, 44),
      paper_bgcolor = Color.RGB(35, 39, 44),
      font = Font(size = 12, family = "Arial", color = Color.RGB(255, 255, 255))
    )

    Plotly.plot("charts/first_last_purchase_ratio_by_employee.html", barChart, barLayout)
  }
}
