package at.htl.mirrorhome.user

import at.htl.mirrorhome.MirrorMasterServiceApplication
import at.htl.mirrorhome.utilities.InetHelper
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.net.InetAddress
import java.time.Instant

@RestController
@RequestMapping("api/register")
class RegistrationController(
    val userRepository: UserRepository,
    val messagingTemplate: SimpMessagingTemplate
) {
    private val log = LoggerFactory.getLogger(RegistrationController::class.java)
    private val restTemplate: RestTemplate = RestTemplate()

    @Value("\${face.api}")
    val faceApi: String = ""

    @PostMapping("updateRegisterModel")
    fun updateRegisterModel(@RequestBody registerModel: RegisterModel): String {
        MirrorMasterServiceApplication.mainContext.registrationModel.update(registerModel)
        return "Ok! RegisterModel changed to: ${MirrorMasterServiceApplication.mainContext.registrationModel}"
    }

    @GetMapping("qr-code")
    fun generateQrCode(): String {

        var inetHelper: InetHelper = InetHelper()
        var ip: InetAddress = inetHelper.getLANAddress()
        var parts: List<String> = ip.hostAddress.split(".")

        var defaultGateway = parts.get(0) + "." + parts.get(1) + "." + parts.get(2) + "." + "1";
        log.info("Default Gateway is probably: ${defaultGateway}")

        var response: JSONObject? = JSONObject(restTemplate.getForEntity("http://${defaultGateway}:4564", String::class.java).body)
        var keySet: List<String?>? = response?.keySet()?.toList()
        log.info("Mapped response to Any::class.java ${response}")

        var lastKey: String? = keySet?.get(keySet.size.minus(1))
        var lastAttribute: Any? = response?.get(lastKey)
        //var firstKey: String? = response?.keys()?.iterator()?.next()
        //var firstAttribute: Any? = response?.get(firstKey)

        log.info("Response from IPService: ${response} with last attribute ${lastAttribute}")

        return "" + lastAttribute // Add token/session ID to response header later on!
    }

    @PostMapping()
    fun register(@RequestBody registerModel: RegisterModel): User? {
        MirrorMasterServiceApplication.mainContext.registrationState = RegistrationState.PROCESSING_REGISTRATION
        messagingTemplate.convertAndSend("/topic/command", RegistrationState.PROCESSING_REGISTRATION)

        var trimmedMd5PictureHash: String = MirrorMasterServiceApplication.mainContext.registrationModel.md5Picture
                .replace("[^A-Za-z0-9 ]".toRegex(), "")
        var registerUser: User = User(-1, -1, registerModel.username, "", registerModel.password,
                trimmedMd5PictureHash, UserRole.USER, registerModel.firstname, "", Instant.now(), UserSettings())
        userRepository.save(registerUser)

        MirrorMasterServiceApplication.mainContext.registrationState = RegistrationState.USER_CREATED
        messagingTemplate.convertAndSend("/topic/command", RegistrationState.USER_CREATED)

        // restart face detection
        restTemplate.getForObject<Any>("${faceApi}/start")

        return userRepository.findAll().filter { it -> it.username.equals(registerModel.username) }.getOrNull(0)
    }

    @PostMapping("unverifiedQRCodeScan")
    fun qrCodeScanned() {
        MirrorMasterServiceApplication.mainContext.registrationState = RegistrationState.QR_CODE_SCANNED
        messagingTemplate.convertAndSend("/topic/command", RegistrationState.QR_CODE_SCANNED)
    }

    @PostMapping("abortRegistration")
    fun abortRegistration() : String {
        MirrorMasterServiceApplication.mainContext.registrationState = RegistrationState.REGISTRATION_ABORTED
        messagingTemplate.convertAndSend("/topic/command", RegistrationState.REGISTRATION_ABORTED)
        return "+OK, Registration has been aborted"
    }

    @PostMapping("takeProfilPicture")
    fun takeProfilePicture() {
        var md5AvatarIdentifier: String?
        md5AvatarIdentifier = restTemplate.getForEntity("${faceApi}/register", String::class.java).body!!

        if (!md5AvatarIdentifier.isNullOrBlank()) {
            // takes picture and returns md5 identifier -> move to context -> store and leave revertable
            var trimmedMd5AvatarIdentifier: String? = md5AvatarIdentifier.replace("[^A-Za-z0-9 ]".toRegex(), "")
            if (trimmedMd5AvatarIdentifier.equals("NOFACE")) {
                MirrorMasterServiceApplication.mainContext.registrationState = RegistrationState.PROFILPIC_INVALID
                messagingTemplate.convertAndSend("/topic/command", RegistrationState.PROFILPIC_INVALID)
            } else {                
                log.info("trimmed md5 hash for new user picture before storing in context ${trimmedMd5AvatarIdentifier}")
                MirrorMasterServiceApplication.mainContext.registrationModel.md5Picture = trimmedMd5AvatarIdentifier!!

                MirrorMasterServiceApplication.mainContext.registrationState = RegistrationState.PROFILPIC_STORED
                messagingTemplate.convertAndSend("/topic/command", RegistrationState.PROFILPIC_STORED)
            }
            // restart face detection -- DO NOT DO THAT HERE - Mark
            // restTemplate.getForObject<Any>("${faceApi}/start")
        } else if (md5AvatarIdentifier.isNullOrBlank()) {
            MirrorMasterServiceApplication.mainContext.registrationState = RegistrationState.REGISTRATION_ABORTED
            messagingTemplate.convertAndSend("/topic/command", RegistrationState.REGISTRATION_ABORTED)
        }
    }
}