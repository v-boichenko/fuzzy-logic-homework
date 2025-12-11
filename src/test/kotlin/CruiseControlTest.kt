import net.sourceforge.jFuzzyLogic.FIS
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertTrue

class CruiseControlTest {
    private lateinit var fis: FIS
    @BeforeEach
    fun setup(): Unit {
        val fileName = "src/main/resources/cruise_control.fcl"
        val file = File(fileName)
        if (!file.exists()) {
            throw RuntimeException("FCL file not found at: ${file.absolutePath}")
        }
        fis = FIS.load(fileName, true) ?: error("Error loading FCL file. Check syntax.")
    }


    private fun testCaseShouldStop(speedKmPerHour: Double, distance: Double) {
        val sim = CruiseControlSimulator(
            fis = fis,
            speedKmPerHour = speedKmPerHour,
            distanceMeters = distance
        )
        println("START: Speed=${sim.getSpeed()} km/h, Dist=${sim.getDistance()} m")
        var isStopped = false
        val timeToBrake = 60
        // WHEN: We run the simulation loop
        for (time in 1..timeToBrake) {
            sim.nextSecond()
            val speed = sim.getSpeed()
            val dist = sim.getDistance()
            println("T=$time | Speed=${"%.2f".format(speed)} | Dist=${"%.2f".format(dist)}")
            // ASSERT 1: Ensure we haven't crashed (Distance must be > 0)
            assertTrue(dist > 0.0, "CRASH! Car hit the obstacle at second $time")

            // Check if stopped
            if (speed == 0.0) {
                isStopped = true
                println("Car stopped safely at distance: $dist meters")
                break
            }
        }

        // Verify the car actually stopped
        assertTrue(isStopped, "Car failed to stop within $timeToBrake seconds")

        // Verify stoopped in a reasonable distance
        assertTrue(sim.getDistance() < 20.0, "Car stopped too far away from the obstacle")
    }

    @Test
    fun `when speed is high and distance is far then should stop safely`() {
        // GIVEN: 120 km/h and 200m distance
        testCaseShouldStop(speedKmPerHour = 120.0, distance = 200.0)
    }

    @Test
    fun `when speed is medium and distance is close then should stop safely`() {
        testCaseShouldStop(speedKmPerHour = 70.0, distance = 40.0)
    }

    @Test
    fun `when speed is low and distance is  very close then should stop safely`() {
        testCaseShouldStop(speedKmPerHour = 30.0, distance = 10.0)
    }

    @Test
    fun `when distance is very close then should not move`() {
        testCaseShouldStop(speedKmPerHour = 0.0, distance = 0.01)
    }

}