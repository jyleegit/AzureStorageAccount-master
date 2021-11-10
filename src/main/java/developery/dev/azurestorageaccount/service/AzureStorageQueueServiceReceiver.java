package developery.dev.azurestorageaccount.service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.QueueServiceClient;
import com.azure.storage.queue.QueueServiceClientBuilder;
import com.azure.storage.queue.models.PeekedMessageItem;
import com.azure.storage.queue.models.SendMessageResult;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AzureStorageQueueServiceReceiver {
	
	int maxSize=10;
	final String END_POINT = "https://juyounglee.blob.core.windows.net/";
	final String ACCOUNT_END_POINT = "https://juyounglee.queue.core.windows.net";
	final String QUEUE_END_POINT = ACCOUNT_END_POINT + "/demo";
	String SAS_TOKEN= "?sv=2020-08-04&ss=bfqt&srt=sco&sp=rwdlacupitfx&se=2022-02-04T23:53:17Z&st=2021-11-09T15:53:17Z&spr=https&sig=0Eu8pwIQ2MfZKLUivSMYvv5mz2%2FrinyU8DZTPlHXM6o%3D";

	public void queueReceiver() {
		QueueClient queueClient = new QueueClientBuilder()
				.endpoint(QUEUE_END_POINT)
				.sasToken(SAS_TOKEN)
				.buildClient();
		String accessKey = "DefaultEndpointsProtocol=https;AccountName=juyounglee;AccountKey=8pHAqDuBGHfkuED6nGl61br9OBCo1t4eszWvd7vkxwrzl8jW/MVXQfI3wa0pEvtEsa5InhatzPr7mwVWEC9w0w==;EndpointSuffix=core.windows.net";
		
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
			    .endpoint(END_POINT)
			    .connectionString(accessKey)
			    .buildClient();
		BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("containertest1");
		
		while(true) {
			List<String> msgIdList = queueClient.receiveMessages(maxSize).stream()
					.map( message -> {
						
						queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt()); // 삭제
						
						String fileName = message.getMessageId();
						
						FileWriter writer = null;
						try {
							log.info("File name: " + fileName);
							writer = new FileWriter(fileName, true);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							log.info("Message");
							writer.write(message.getMessageText().toString());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							log.info("Close");
							writer.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						BlobClient blobClient = containerClient.getBlobClient(fileName); 
						blobClient.uploadFromFile(fileName);
						
						return message.getMessageId();
					})
					.collect(Collectors.toList());		
					
			}
		
	}
	

	public String insertQueue(String msg) {
		QueueClient queueClient = new QueueClientBuilder()
				.endpoint(QUEUE_END_POINT)
				.sasToken(SAS_TOKEN)
				.buildClient();
		
		SendMessageResult result = queueClient.sendMessage(LocalDateTime.now() + "_" + msg);
		
		System.out.println("msgId: " + result.getMessageId());
		
		return result.getMessageId();
		
	}
	

	public String peekQueue() {
		
		QueueClient queueClient = new QueueClientBuilder()
				.endpoint(QUEUE_END_POINT)
				.sasToken(SAS_TOKEN)
				.buildClient();
		
		PeekedMessageItem item = queueClient.peekMessage();
		
		System.out.println("peek: " + item.getBody().toString() 
				+ " . count: " + item.getDequeueCount() 
				+ " id: " +  item.getMessageId()
				+ " time: " +  item.getInsertionTime());
		
		return item.getMessageId();		
	}
	
	public int getMessageQueue(int count) {
		QueueClient queueClient = new QueueClientBuilder()
				.endpoint(QUEUE_END_POINT)
				.sasToken(SAS_TOKEN)
				.buildClient();

		List<String> msgIdList = queueClient.receiveMessages(count).stream()
				.map( message -> {
			
					System.out.println("messageId: " + message.getMessageId() 
						+ ", msg: " + message.getBody().toString() 
						+ ", popReceipt: " + message.getPopReceipt());
					return message.getMessageId();
				})
				.collect(Collectors.toList());		
		
		return msgIdList.size();
	}
	
//	@Test
	public int getMessageAndDeleteQueue(int count) {

		QueueClient queueClient = new QueueClientBuilder()
				.endpoint(QUEUE_END_POINT)
				.sasToken(SAS_TOKEN)
				.buildClient();

		List<String> msgIdList = queueClient.receiveMessages(count).stream()
				.map( message -> {
			
					System.out.println("messageId: " + message.getMessageId() 
						+ ", msg: " + message.getBody().toString() 
						+ ", popReceipt: " + message.getPopReceipt());
					
					queueClient.deleteMessage(message.getMessageId(), message.getPopReceipt()); // 삭제
					
					return message.getMessageId();
				})
				.collect(Collectors.toList());		
		
		return msgIdList.size();
	
	}
	
	public String listQueue() {
		
		QueueServiceClient queueServiceClient = new QueueServiceClientBuilder()
				.endpoint(ACCOUNT_END_POINT)
		        .sasToken(SAS_TOKEN)
		        .buildClient();

		
		List<String> queueNameList = queueServiceClient.listQueues().stream()
			.map(queue-> {
				System.out.println("queue name: " + queue.getName());
				return queue.getName();
			})
			.collect(Collectors.toList());
		
		return queueNameList.toString();
	}

}
