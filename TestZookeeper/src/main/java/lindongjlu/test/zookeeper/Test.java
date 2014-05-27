package lindongjlu.test.zookeeper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class Test {

	final ZooKeeper zk;
	
	public Test() throws Throwable {
		zk = new ZooKeeper("localhost:2181", 30000, new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				System.out.println(event.toString());
			}
		});
		
		System.out.println("test : " + zk.exists("/zoo2", true));
	}
	
	public void ZKOperations() throws IOException,InterruptedException,KeeperException {
		
		System.out.println("\n1. 创建 ZooKeeper 节点 (znode ： zoo2, 数据： myData2 ，权限： OPEN_ACL_UNSAFE ，节点类型： Persistent");
		zk.create("/zoo2","myData2".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		
		TimeUnit.SECONDS.sleep(3);
		
		System.out.println("\n2. 查看是否创建成功： ");
		System.out.println(new String(zk.getData("/zoo2",false,null)));
		
		TimeUnit.SECONDS.sleep(3);
		
		System.out.println("\n3. 修改节点数据 ");
		zk.setData("/zoo2", "shenlan211314".getBytes(), -1);
		
		TimeUnit.SECONDS.sleep(3);
		
		System.out.println("\n4. 查看是否修改成功： ");
		System.out.println(new String(zk.getData("/zoo2", false, null)));
		
		TimeUnit.SECONDS.sleep(3);
		
		System.out.println("\n5. 删除节点 ");
		zk.delete("/zoo2", -1);
		
		TimeUnit.SECONDS.sleep(3);
		
		System.out.println("\n6. 查看节点是否被删除： ");
		System.out.println(" 节点状态： ["+zk.exists("/zoo2", false)+"]");
	}

	public void ZKClose() throws InterruptedException {
		zk.close();
	}
	
	public static void main(String[] args) throws Throwable {
		Test test = new Test();
		test.ZKOperations();
		
		TimeUnit.SECONDS.sleep(3);
		
		test.ZKClose();
	}

}
