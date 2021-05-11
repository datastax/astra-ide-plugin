import com.jetbrains.rd.util.addUnique
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.ConfigSpec
import com.uchuhimo.konf.source.json.toJson
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.toml.toToml

class testCred {
}


data class Profile(
    val profile_name: String,
    val token_collection: Map<String,String>
)

object AstraProfileFile: ConfigSpec(){
    val profiles by required<Set<Profile>>()
}

fun main(){
    var profilesTest1 = Config{addSpec(AstraProfileFile)}

    var mySet = mapOf(
        "token1" to "blahblah",
        "token2" to "foobar",
        "token3" to "someshit"
    )
    var mySet2 = mapOf(
        "token4" to "blahblah",
        "token5" to "foobar",
        "token6" to "someshit"
    )
    var profile1 = Profile("Profile1",mySet)
    var profile2 = Profile("Profile2",mySet2)


    var profileSet = setOf(profile1,profile2)


    profilesTest1[AstraProfileFile.profiles] = profileSet
    profilesTest1.toJson.toFile("${System.getProperty("user.home")}/.astra/config1")

    var profilesTest2 = Config{addSpec(AstraProfileFile)}
        .from.json.file("${System.getProperty("user.home")}/.astra/config1")

    println(profilesTest2.toString())
}