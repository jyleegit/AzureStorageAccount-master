package developery.dev.azurestorageaccount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import developery.dev.azurestorageaccount.service.AzureStorageQueueServiceReceiver;

@SpringBootApplication
public class AzureStorageAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(AzureStorageAccountApplication.class, args);
		AzureStorageQueueServiceReceiver run = new AzureStorageQueueServiceReceiver();
		run.queueReceiver();
	}

}
