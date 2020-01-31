package at.htl.mirrorhome.speech

class Intent(
    val intentType: IntentType = IntentType.HelpIntent,
    val payload: Any? // but at most String?
)