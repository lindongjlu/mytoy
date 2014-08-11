package netty.test;

import io.netty.example.http.snoop.HttpSnoopServer;

public class TestHttpServer {

	public static void main(String[] args) throws Exception {
		new HttpSnoopServer(8080).run();
	}

}
