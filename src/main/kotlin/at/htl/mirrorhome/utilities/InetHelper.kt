package at.htl.mirrorhome.utilities

import java.net.InetAddress
import java.net.NetworkInterface
import java.net.UnknownHostException

class InetHelper {
    // val localHostLANAddress: InetAddress
    @Throws(UnknownHostException::class)
    fun getLANAddress(): InetAddress{
        try {
            var candidateAddress: InetAddress? = null
            val ifaces = NetworkInterface.getNetworkInterfaces()
            while (ifaces.hasMoreElements()) {
                val iface = ifaces.nextElement() as NetworkInterface
                val inetAddrs = iface.getInetAddresses()
                while (inetAddrs.hasMoreElements()) {
                    val inetAddr = inetAddrs.nextElement() as InetAddress
                    if (!inetAddr.isLoopbackAddress()) {
                        if (inetAddr.isSiteLocalAddress()) {
                            return inetAddr
                        } else if (candidateAddress == null) {
                            candidateAddress = inetAddr
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress
            }
            val jdkSuppliedAddress = InetAddress.getLocalHost()
            if (jdkSuppliedAddress == null) {
                throw UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.")
            }
            return jdkSuppliedAddress
        } catch (e: Exception) {
            val unknownHostException = UnknownHostException("Failed to determine LAN address: " + e)
            unknownHostException.initCause(e)
            throw unknownHostException
        }
    }
}