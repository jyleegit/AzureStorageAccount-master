package developery.dev.azurestorageaccount.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobProperties;

import developery.dev.azurestorageaccount.common.CommonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AzureStorageService {
	
	final String END_POINT = "https://juyounglee.blob.core.windows.net/";
	
	
	
	public String readBlobBySasCredential(String containerName, String blobName) throws IOException {
		
		String sasToken = "?sv=2020-08-04&ss=bfqt&srt=sco&sp=rwdlacupitfx&se=2022-02-04T23:53:17Z&st=2021-11-09T15:53:17Z&spr=https&sig=0Eu8pwIQ2MfZKLUivSMYvv5mz2%2FrinyU8DZTPlHXM6o%3D";
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
			    .endpoint(END_POINT)			    
			    .sasToken(sasToken)
			    .buildClient();
		
		return readBlob(blobServiceClient, containerName, blobName);		
	}
	
	public String readBlobByServicePrincipalCredential(String containerName, String blobName) throws IOException {
		
		String clientId = "476ab5db-bbad-467c-a8c4-de5346587dcf";
		String clientSecret = "G567Q~KkJOgTEL.kPSHNcmjC6SNnqQHJOPnHW";
		String tenantId = "4784a18d-d34d-47d9-afb0-87b086bf01a8";
		
		ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
				  .clientId(clientId)
				  .clientSecret(clientSecret)
				  .tenantId(tenantId)
				  .build();
		
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
			    .endpoint(END_POINT)
			    .credential(clientSecretCredential)
			    .buildClient();
		
		return readBlob(blobServiceClient, containerName, blobName);	
		
	}
	
	private String readBlob(BlobServiceClient blobServiceClient, String containerName, String blobName) throws IOException {
		BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("containertest1");
		
		BlobClient blobClient = containerClient.getBlobClient(blobName);
		
		CommonUtils.deleteIfExists("test.txt");
		
		BlobProperties pro = blobClient.downloadToFile("test.txt");
		
		log.info("blobSize: " + pro.getBlobSize());
		String textInFile = CommonUtils.readStringFromFile(blobName);
		
		return textInFile;
	}

	
	public String readBlobByAccessKeyCredential(String containerName, String blobName) throws IOException {
		
		String accessKey = "DefaultEndpointsProtocol=https;AccountName=juyounglee;AccountKey=8pHAqDuBGHfkuED6nGl61br9OBCo1t4eszWvd7vkxwrzl8jW/MVXQfI3wa0pEvtEsa5InhatzPr7mwVWEC9w0w==;EndpointSuffix=core.windows.net";
//		String accessKey = "BlobEndpoint=https://juyounglee.blob.core.windows.net/;QueueEndpoint=https://juyounglee.queue.core.windows.net/;FileEndpoint=https://juyounglee.file.core.windows.net/;TableEndpoint=https://juyounglee.table.core.windows.net/;SharedAccessSignature=sv=2020-08-04&ss=bfqt&srt=sco&sp=rwdlacupitfx&se=2022-02-04T23:53:17Z&st=2021-11-09T15:53:17Z&spr=https&sig=0Eu8pwIQ2MfZKLUivSMYvv5mz2%2FrinyU8DZTPlHXM6o%3D";
		
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
			    .endpoint(END_POINT)
			    .connectionString(accessKey)
			    .buildClient();
		
		return readBlob(blobServiceClient, containerName, blobName);		
	}
	
	public String readBlobBySystemManagedIdCredential(String containerName, String blobName) throws IOException {
		
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
			    .endpoint(END_POINT)
			    .credential(new DefaultAzureCredentialBuilder().build())
			    .buildClient();
		
		return readBlob(blobServiceClient, containerName, blobName);		
	}

}
