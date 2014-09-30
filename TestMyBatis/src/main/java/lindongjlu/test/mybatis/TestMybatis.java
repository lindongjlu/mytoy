package lindongjlu.test.mybatis;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMybatis {

	private SqlSessionFactory sqlSessionFactory;
	
	@Before 
	public void createSqlSessionFactory() throws IOException {
		SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
		sqlSessionFactory = builder.build(Resources.getResourceAsReader("mybatis-config.xml"));
	}
	
	@After
	public void destroySqlSessionFactory() {
		sqlSessionFactory = null;
	}
	
	@Test
	public void testGet() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			User user = mapper.getUser(1);
			
			assertEquals(user.getName(), "猪头");
			assertEquals(user.getGender(), User.Gender.F);
			
			System.out.println(user.toString());
			
		} finally {
			sqlSession.close();
		}
	}
	
	@Test
	public void testInsert() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);

			User user = new User(0);
			user.setName("哈哈大猪头");
			user.setGender(User.Gender.M);
			
			boolean succ = mapper.insertUser(user);
			
			assertTrue(succ && user.getId() > 0);
			
			// System.out.println(user.getId());
			
			if (succ) {
				sqlSession.commit(true);
			}
			
		} finally {
			sqlSession.close();
		}
	}
	
	@Test
	public void testDelete() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);
			
			boolean succ = mapper.deleteUser(13);
			
			assertTrue(succ);

			if (succ) {
				sqlSession.commit(true);
			}
			
		} finally {
			sqlSession.close();
		}
	}
	
// 参数化测试编写流程如下：
//	a.为参数化测试类用@RunWith注释指定特殊的运行器：Parameterized.class；
//	b.在测试类中声明几个变量，分别用于存储期望值和测试用的数据，并创建一个使用者几个参数的构造函数；
//	c.创建一个静态（static）测试数据供给（feed）方法，其返回类型为Collection，并用@Parameter注释以修饰；
//	d.编写测试方法（用@Test注释）。
}
