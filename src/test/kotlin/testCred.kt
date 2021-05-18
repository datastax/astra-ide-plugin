import com.jetbrains.rd.util.addUnique
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.json.toJson
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.toml.toToml
import java.io.File
import kotlin.IllegalStateException

class testCred {
}

data class ProfileToken(
    val profile_name: String,
    val token: String
)

object AstraProfileFile: ConfigSpec(){
    val profiles by required<Map<String,String>>()
}

fun main(){
    println(checkFile())
    //verifyToken("AstraCS:slujUFOXpduHKliZZxaijrTw:ba0716b40492ed7be92224a0d6a39f2315291b6d18ab7ee5129306278a1a558f")
    //createConfig()
try {
    loadConfig()
} catch (e: IllegalStateException){
    println("caught illegal state exception")
} catch (e: Exception){
    println("caught other exception")
}

    loadConfig()
}

fun verifyToken(profileToken: String): Boolean{
    var valid = true
    if(profileToken.length==97)
        profileToken.split(":").forEachIndexed{ index, element->
            when (index) {
                0 -> if (element != "AstraCS") valid = false
                1 -> if (!(element.length == 24 || element.all{it.isLetterOrDigit()})) valid = false
                2 -> if (!(element.length == 64 || element.all{it.isLetterOrDigit()})) valid = false
                else -> {valid = false}
            }
        }
    else
        valid = false
    return valid
}

fun checkFile(): Boolean{
    val configPath = File("${System.getProperty("user.home")}/.astra/config")
    return configPath.exists()

}

fun loadConfig(){
    var profilesTest2 = Config{addSpec(AstraProfileFile)}
        .from.toml.file("${System.getProperty("user.home")}/.astra/config")

    println(profilesTest2.toString())
}

fun createConfig(){
    var profilesTest1 = Config{addSpec(AstraProfileFile)}

    var mySet = setOf(
        ProfileToken("profile1","onetoken"),
        ProfileToken("funprofile","anothertoken"),
        ProfileToken("profilefoo","bartoken")
    )
    var mySet1 = mapOf(
        "profile1" to "onetoken",
        "funprofile" to "anothertoken",
        "profilefoo" to "bartoken"
    )

    profilesTest1[AstraProfileFile.profiles] = mySet1
    profilesTest1.toToml.toFile("${System.getProperty("user.home")}/.astra/config1")
}