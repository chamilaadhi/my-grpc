package com.chamila.grpc.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.chamila.grpc.gen.MyServiceGrpc;
import com.chamila.grpc.gen.MyServiceGrpc.MyServiceStub;
import com.chamila.grpc.gen.Request;
import com.chamila.grpc.gen.Response;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class Client {
	
	public static void main(String[] args) throws InterruptedException {

		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();
		
		//bidirectionalCall(channel);
		clientStream(channel);
		
	}

	private static void clientStream(ManagedChannel channel) throws InterruptedException {
		final CountDownLatch finishLatch = new CountDownLatch(1);
		MyServiceStub stub = MyServiceGrpc.newStub(channel);
		
		
		StreamObserver<Response> responseObserver = new StreamObserver<Response>() {
			
			@Override
			public void onNext(Response value) {
				System.out.println("=====onNext========= " + value.getResponse());
				
			}
			
			@Override
			public void onError(Throwable t) {
				System.out.println("=====onError======== " + t);
				finishLatch.countDown();
			}
			
			@Override
			public void onCompleted() {
				System.out.println("=====onCompleted====");
				finishLatch.countDown();
				
			}
		};
		
		
		
		
		StreamObserver<Request> requestObserver = stub.clientStream(responseObserver);
		Request value = Request.newBuilder().setMessage("request 1").build();
		requestObserver.onNext(value);
		value = Request.newBuilder().setMessage("request 2").build();
		requestObserver.onNext(value);
		
		requestObserver.onCompleted();
		finishLatch.await(1, TimeUnit.MINUTES);
		
	}

	private static void bidirectionalCall(ManagedChannel channel) throws InterruptedException {
		final CountDownLatch finishLatch = new CountDownLatch(1);
		MyServiceStub stub = MyServiceGrpc.newStub(channel);

		StreamObserver<Response> responseObserver =  new StreamObserver<Response>() {

			@Override
			public void onNext(Response value) {
				System.out.println("response " + value.getResponse());

			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				finishLatch.countDown();
			}

			@Override
			public void onCompleted() {
				// TODO Auto-generated method stub
				finishLatch.countDown();

			}
		};
		
		
		StreamObserver<Request> requestObserver = stub.bidirectionalInvoke(responseObserver );
		
		for(int i = 0; i < 100 ; i++) {
			Request value = Request.newBuilder().setMessage("req-" + i).build();
			System.out.println("+++++++ req " + i);
			requestObserver.onNext(value );
			Thread.sleep(5);
		}
		requestObserver.onCompleted();
		finishLatch.await(1, TimeUnit.MINUTES);
		
	}
	
	

}
