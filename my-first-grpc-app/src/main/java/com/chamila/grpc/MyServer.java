package com.chamila.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.chamila.grpc.gen.MyServiceGrpc;
import com.chamila.grpc.gen.Request;
import com.chamila.grpc.gen.Response;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class MyServer {

	private static final Logger log = Logger.getLogger(MyServer.class.getName());
	private static Server server;

	public static void main(String[] args) {

		try {
			server = ServerBuilder.forPort(50051).addService(new MyService()).build().start();
			log.info("Server started, listening on 50051");
		} catch (IOException e) {
			log.severe("Error while starting " + e);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				// Use stderr here since the logger may have been reset by its JVM shutdown
				// hook.
				System.err.println("*** shutting down gRPC server since JVM is shutting down");
				try {
					if (server != null) {
						server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
					}
				} catch (InterruptedException e) {
					e.printStackTrace(System.err);
				}
				System.err.println("*** server shut down");
			}
		});
		
		try {
			server.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static class MyService extends MyServiceGrpc.MyServiceImplBase{
		@Override
		public void echo(Request request, StreamObserver<Response> responseObserver) {
			Response resp = Response.newBuilder()
					.setResponse("Eco Response : " + request.getRequest())
					.build();
			responseObserver.onNext(resp);
			responseObserver.onCompleted();
		}

	}
}
