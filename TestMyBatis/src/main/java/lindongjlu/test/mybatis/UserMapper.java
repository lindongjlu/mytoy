package lindongjlu.test.mybatis;

public interface UserMapper {

	User getUser(int userId);
	
	boolean insertUser(User user);
	
	boolean updateUser(User user);
	
	boolean deleteUser(int userId);
}
