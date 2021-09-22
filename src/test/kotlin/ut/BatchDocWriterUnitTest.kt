package ut

import com.intellij.testFramework.ProjectRule
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class BatchDocWriterUnitTest {
    class TransferUtilsIntegrationTest {
        private val s3Client = S3Client.builder()
            .httpClient(ApacheHttpClient.builder().build())
            .region(Region.US_WEST_2)
            .serviceConfiguration { it.pathStyleAccessEnabled(true) }
            .build()
        //Build Client
        @JvmField
        @Rule
        val folder = TemporaryFolder()
        //Create/Load JSON file
        @JvmField
        @Rule
        val projectRule = ProjectRule()
        //Define rules
        @JvmField
        @Rule
        val bucketRule = S3TemporaryBucketRule(s3Client)

        @Test
        fun canDoUploadAndDownload() {
            val bucket = bucketRule.createBucket()
            val bigString = "hello world".repeat(1000)
            //Create Collection
            val sourceFile = folder.newFile()
            sourceFile.writeText(bigString)
            //Upload JSON
            s3Client.upload(projectRule.project, sourceFile.toPath(), bucket, "file", message = "uploading").value

            //Check Collection for JSON
            val destinationFile = folder.newFile()
            s3Client.download(projectRule.project, bucket, "file", null, destinationFile.toPath(), message = "downloading").value

            //Assert download JSON is the same as the uploaded (do necessary GSON transformations)
            assertThat(destinationFile).hasSameTextualContentAs(sourceFile)
        }
    }

    //TODO:
    // Make sure client sends proper requests for with a JSON field ID and without (Mock server)
    // Make sure all functions of unit behave properly

}