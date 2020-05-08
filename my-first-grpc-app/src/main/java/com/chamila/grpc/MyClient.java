package com.chamila.grpc;

import com.chamila.grpc.gen.MyServiceGrpc;
import com.chamila.grpc.gen.MyServiceGrpc.MyServiceBlockingStub;
import com.chamila.grpc.gen.Request;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class MyClient {

	public static void main(String[] args) {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext().build();

		MyServiceBlockingStub stub = MyServiceGrpc.newBlockingStub(channel);

		Request request = Request.newBuilder().setRequest("First Request").build();
		System.out.println(stub.echo(request));

	}
}
