package com.chamila.grpc.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.chamila.grpc.gen.MyServiceGrpc.MyServiceImplBase;
import com.chamila.grpc.gen.Request;
import com.chamila.grpc.gen.Response;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class AppServer extends MyServiceImplBase {

	private static io.grpc.Server server;
	private static List<String> repo = new ArrayList<String>();

	public static void main(String[] args) {

		try {
			server = ServerBuilder.forPort(50051).addService(new AppServer()).build().start();
			System.out.println("Server Started on port 50051");
			server.awaitTermination();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public StreamObserver<Request> clientStream(final StreamObserver<Response> responseObserver) {
		return new StreamObserver<Request>() {
			
			@Override
			public void onNext(Request value) {
				System.out.println("== request incoming==");
				repo.add(value.getMessage());
			}
			
			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCompleted() {
				System.out.println("======complete==========");
				Response response = Response.newBuilder().setResponse("Request items: " + String.join(",", repo)).build();
				repo.clear();
				responseObserver.onNext(response);
				responseObserver.onCompleted();
			}
		};
	}
	
	@Override
	public StreamObserver<Request> bidirectionalInvoke(StreamObserver<Response> responseObserver) {
		return new StreamObserver<Request>() {
			
			@Override
			public void onNext(Request value) {
				Response response = Response.newBuilder().setResponse("Response : " + value.getMessage()).build();
				responseObserver.onNext(response);			
			}
			
			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onCompleted() {
				responseObserver.onCompleted();
			}
		};
	}
}
