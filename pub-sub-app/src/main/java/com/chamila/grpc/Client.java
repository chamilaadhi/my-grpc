package com.chamila.grpc;

import java.util.concurrent.CountDownLatch;
import com.chamila.grpc.gen.PubSubServiceGrpc;
import com.chamila.grpc.gen.PubSubServiceGrpc.PubSubServiceStub;
import com.chamila.grpc.gen.Response;
import com.google.protobuf.Empty;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class Client implements Runnable {

	public static void main(String[] args) {

		int port = 50051;
		String host = "localhost";
		Thread t = new Thread(new Client(host, port, 10));
		t.start();

	}

	private ManagedChannel channel;
	private PubSubServiceStub asyncStub;
	private int maxRetry = 0;
	private int attempt = 0;
	private boolean failed = false;

	public Client(String host, int port, int retry) {
		channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
		asyncStub = PubSubServiceGrpc.newStub(channel);
		this.maxRetry = retry;
	}

	public void listen() {
		final CountDownLatch finishLatch = new CountDownLatch(1);
		StreamObserver<Response> responseObserver = new StreamObserver<Response>() {

			@Override
			public void onNext(Response value) {
				attempt = 0; //reset counter
				System.out.println("Response " + value.getResponse());

			}

			@Override
			public void onError(Throwable t) {
				System.out.println("Error while listing: " + t);
				attempt++;
				//maxRetry--;
				failed = true;
				finishLatch.countDown();
			}

			@Override
			public void onCompleted() {
				System.out.println("Completed listing");
				attempt = maxRetry;
				finishLatch.countDown();
			}
		};
		Empty request = Empty.newBuilder().build();
		asyncStub.listen(request, responseObserver);
		try {
			finishLatch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		System.out.println("Client started listing...");
		//int attempt = 0;
		int wait = 15;
		do {
			failed = false;
			System.out.println("Trying to connetect: attempt " + attempt);
			listen();
			if (failed) {
				
				System.out.println("Connection failed. Available attempts " + (maxRetry - attempt));
				System.out.println("Wating for " + wait +"s before re-attempting." );
				try {
					Thread.sleep(wait * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} while (attempt <= maxRetry);

	}

}
