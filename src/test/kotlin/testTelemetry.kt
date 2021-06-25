import com.intellij.ui.LicensingFacade
import com.segment.analytics.Analytics
import com.segment.analytics.messages.IdentifyMessage
import com.segment.analytics.messages.TrackMessage
import org.jetbrains.annotations.Nullable

class testTelemetry

fun main() {
    var analytics = Analytics.builder("DVku2hfmrgixOIBzcDWwadEqtOMVVxkU").build()
    idUser(analytics)
    trackUser(analytics)
    println(printLicenseID())
}

fun printLicenseID(): @Nullable String? {
    val instance = LicensingFacade.getInstance()
    if (null != instance) {
        return instance.getLicensedToMessage()
    } else return null
}

fun idUser(segmentObj: Analytics) {
    segmentObj.enqueue(
        IdentifyMessage.builder()
            .userId("f4ca124298")
            .traits(
                mapOf(
                    "name" to "Michael Bolton",
                    "email" to "mbolton@example.com"
                )
            )
    )
}

fun trackUser(segmentObj: Analytics) {
    segmentObj.enqueue(
        TrackMessage.builder("Signed Up")
            .userId("f4ca124298")
            .properties(
                mapOf(
                    "plane" to "Enterprise"
                )
            )
    )
}
