package lindongjlu.test.redis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class TestJedis {

	private JedisPool pool;
	
	@Before
	public void createJedisPool() {
		pool = new JedisPool(new JedisPoolConfig(), "localhost");
	}
	
	@After
	public void destroyJedisPool() {
		pool.destroy();
	}
	
	@Test
	public void testSetGet() {
		Jedis jedis = pool.getResource();
		try {
			
			String ret = jedis.set("123", "abc");
			
			System.out.println("set:" + ret);
			
			String result = jedis.get("123");
			
			assertEquals(result, "abc");
			
		} finally {
			jedis.close();
		}
	}
	
	@Test
	public void testSetGetBinary() {
		Jedis jedis = pool.getResource();
		try {
			
			byte[] key = "2sfddsf".getBytes();
			byte[] value = "dddsfsdf".getBytes();
			
			String ret = jedis.set(key, value);
			
			System.out.println("set:" + ret);
			
			byte[] result = jedis.get(key);
			
			assertArrayEquals(result, value);
			
		} finally {
			jedis.close();
		}
	}
}
