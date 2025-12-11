import net.sourceforge.jFuzzyLogic.FIS
import kotlin.math.max

class CruiseControlSimulator(
    private val fis: FIS,
    var speedKmPerHour: Double,
    var distanceMeters: Double
) {
    private fun getSpeedMetersPerSecond(): Double = speedKmPerHour / 3.6
    fun getSpeed() = speedKmPerHour
    fun getDistance() = distanceMeters
    private val MAX_ACCELERATION = 10.0 // m/s^2

    /**
     * Simulation of 1 second
     * 1. Feeds current state to Fuzzy Controller.
     * 2. Gets throttle/brake output.
     * 3. Updates speed and distance based on kinematics.
     */
    fun nextSecond() {
        fis.setVariable("speed", speedKmPerHour)
        fis.setVariable("distance", distanceMeters)
        fis.evaluate()
        val throttlePercent = fis.getVariable("throttle").value

        val a = throttlePercent / 100 * MAX_ACCELERATION
        val v0 = getSpeedMetersPerSecond()
        val t = 1.0  // simulation time, one second

        var distanceTraveled = (v0 * t) + (0.5 * a * t * t) // (d = v0*t + 1/2*a*t^2)
        distanceTraveled = distanceTraveled.takeIf { it > 0.0 } ?: 0.0 // car cannot drive back, only stop
        distanceMeters -= distanceTraveled
        var v1 = v0 + (a * t) // (v = v0 + at)
        v1 = max(0.0, v1) // car cannoot drive back
        speedKmPerHour = v1 * 3.6
    }
}