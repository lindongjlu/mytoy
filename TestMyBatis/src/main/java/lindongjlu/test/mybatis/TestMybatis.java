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
			
		} finally {
			sqlSession.close();
		}
	}
	
	@Test
	public void testInsert() {
		SqlSession sqlSession = sqlSessionFactory.openSession();
		try {
			UserMapper mapper = sqlSession.getMapper(UserMapper.class);

			User user = new User();
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
}
