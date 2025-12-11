package pl.lodz.uni

import net.sourceforge.jFuzzyLogic.FIS
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart
import java.io.InputStream


fun main() {
    val fileName = "wentylator.fcl"
    val inputStream: InputStream = object {}.javaClass.getResourceAsStream("/$fileName") ?: error("No file '$fileName' in src/main/resources/")

    val fis = FIS.load(inputStream, true) ?: error("Error: FIS creation failed. Check syntax in '$fileName'.")

    val temps = listOf(
        0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100
    )

    temps.forEach { temp ->
        fis.setVariable("temperature", temp.toDouble())
        fis.evaluate()
        val speed = fis.getVariable("fan_speed").value
        println("$temp : ${"%.2f".format(speed)}")
        if (temp in 10..20) {
            JFuzzyChart.get().chart(fis)
        }
        Thread.sleep(100)
    }

}