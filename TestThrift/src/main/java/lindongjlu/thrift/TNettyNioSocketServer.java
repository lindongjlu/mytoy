package lindongjlu.thrift;

import io.netty.channel.nio.NioEventLoopGroup;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.thrift.protocol.TProtocolFactory;

import com.google.common.util.concurrent.Service;

public class TNettyNioSocketServer extends TAbstractService {

	private final NioEventLoopGroup bossGroup;
	private final NioEventLoopGroup workerGroup;
	
	private final String host;
	private final int port;
	
	public TNettyNioSocketServer(
			final TProtocolFactory inputProtocolFactory,
			final TProtocolFactory outputProtocolFactory,
			NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup, 
			String host, int port) {
		this.bossGroup = bossGroup;
		this.workerGroup = workerGroup;
		
		this.host = host;
		this.port = port;
		
	}
	
	@Override
	public TNettyNioSocketServer startAsync() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public State state() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Service stopAsync() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void awaitRunning() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitRunning(long timeout, TimeUnit unit)
			throws TimeoutException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitTerminated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void awaitTerminated(long timeout, TimeUnit unit)
			throws TimeoutException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Throwable failureCause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListener(Listener listener, Executor executor) {
		// TODO Auto-generated method stub
		
	}

}
