package com.chamila.grpc.server;

import com.chamila.grpc.gen.Response;

import java.io.IOException;
import java.util.Random;

import com.chamila.grpc.gen.PubSubServiceGrpc.PubSubServiceImplBase;
import com.google.protobuf.Empty;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class Service {

	private static int PORT = 50051;
	private static Server server;
	private static Random rand = new Random();
	public static void main(String[] args) {
		 try {
			server = ServerBuilder.forPort(PORT).addService(new Publisher()).build().start();
			System.out.println("Server started on port " + PORT);
			server.awaitTermination();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static class Publisher extends PubSubServiceImplBase {
		@Override
		public void listen(Empty request, StreamObserver<Response> responseObserver) {
			Response value;
			int num;
			while(true) {
				num = rand.nextInt(10) + 1;
				value = Response.newBuilder().setResponse(Integer.toString(num)).build();
				responseObserver.onNext(value);
				try {
					System.out.println("Waiting for " + num + "s");
					Thread.sleep(num * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}

	}
}
