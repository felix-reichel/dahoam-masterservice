package at.htl.mirrorhome.mqtt

data class ThingSummary (
    var actuators: MutableList<Actuator>,
    var sensors: MutableList<Sensor>
) {}