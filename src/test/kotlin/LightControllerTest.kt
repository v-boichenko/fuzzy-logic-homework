import net.sourceforge.jFuzzyLogic.FIS
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LightControllerTest {
    private lateinit var fis: FIS
    @BeforeEach
    fun setup(): Unit {
        // Load the FCL file
        val fileName = "src/main/resources/light.fcl" // Adjust path as needed
        val file = File(fileName)
        if (!file.exists()) {
            throw RuntimeException("FCL file not found at: ${file.absolutePath}")
        }
        fis = FIS.load(fileName, true) ?: error("Error loading FCL file. Check syntax.")
    }

    private fun evaluateLighting(ambientLight: Double, timeOfDay: Double): Double {
        // Set inputs
        fis.setVariable("ambient_light", ambientLight)
        fis.setVariable("time_of_day", timeOfDay)

        // Run the fuzzy inference engine
        fis.evaluate()

        // Get output
        return fis.getVariable("lamp_brightness").value
    }

    @Test
    fun `Case 1 - Evening low ambient then high light`() {
        val givenAmbientLight = 10.0 // 10 lx (Dark)
        val givenTimeOfDay = 20.0 // 20:00 (Evening)
        val actualBrightness = evaluateLighting(ambientLight = givenAmbientLight, timeOfDay = givenTimeOfDay)
        
        // Should be High (approx 85-95%)
        assertEquals(88.0, actualBrightness, 5.0, "Evening/Dark should trigger High brightness")
    }

    @Test
    fun `Case2 - Sunny noon and bright ambient then turn off`() {
        val givenAmbientLight = 90.0 // lx Bright
        val givenTimeOfDay = 12.0 // Day
        val actualBrightness = evaluateLighting(ambientLight = givenAmbientLight, timeOfDay = givenTimeOfDay)

        // Should be Off or very close to 0
        assertTrue(actualBrightness < 5, "Bright day should turn off")
    }

    @Test
    fun `Case3 - Early nigh low ambient then log light`() {
        val givenAmbientLight = 5.0 // lx
        val givenTimeOfDay = 3.0
        val actualBrightness = evaluateLighting(ambientLight = givenAmbientLight, timeOfDay = givenTimeOfDay)

        // Should be low
        assertEquals(25.0, actualBrightness, 5.0, "Bright day should turn off")
    }

    @Test
    fun `Case4 - Morning medium ambient then medium light`() {
        val givenAmbientLight = 50.0 // lx Bright
        val givenTimeOfDay = 8.0 // morning
        val actualBrightness = evaluateLighting(ambientLight = givenAmbientLight, timeOfDay = givenTimeOfDay)

        // Should be medium
        assertEquals(60.0, actualBrightness, 5.0, "Bright day should turn off")
    }

}