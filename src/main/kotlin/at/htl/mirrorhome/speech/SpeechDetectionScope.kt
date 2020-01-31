package at.htl.mirrorhome.speech

import com.fasterxml.jackson.annotation.JsonFormat

@JsonFormat(shape=JsonFormat.Shape.OBJECT)
enum class SpeechDetectionScope {
    CONFIRM_DENY_SCOPE,
    LISTEN_FOR_COMMANDS_WITH_KEYWORD,
    LISTEN_FOR_COMMANDS_WITHOUT_KEYWORD
}