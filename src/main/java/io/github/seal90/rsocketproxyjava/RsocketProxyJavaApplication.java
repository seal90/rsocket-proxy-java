package io.github.seal90.rsocketproxyjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class RsocketProxyJavaApplication {

	public static void main(String[] args) {
		ApplicationContext ctx = SpringApplication.run(RsocketProxyJavaApplication.class, args);

		final CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				closeLatch.countDown();
			}
		});
		try {
			closeLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Bean
	public CountDownLatch closeLatch() {
		return new CountDownLatch(1);
	}

}
