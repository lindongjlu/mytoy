package lindongjlu.test.mybatis;

import java.util.List;

public class User {
	
	public static enum Gender {
		M, F;
	}
	
	public static class TestAssociation {
		private String testAssociation;

		public String getTestAssociation() {
			return testAssociation;
		}
		public void setTestAssociation(String testAssociation) {
			this.testAssociation = testAssociation;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("TestAssociation [testAssociation=");
			builder.append(testAssociation);
			builder.append("]");
			return builder.toString();
		}
	}
	
	public static class TestCollection {
		private String testCollection;

		public String getTestCollection() {
			return testCollection;
		}
		public void setTestCollection(String testCollection) {
			this.testCollection = testCollection;
		}
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("TestCollection [testCollection=");
			builder.append(testCollection);
			builder.append("]");
			return builder.toString();
		}
	}
	
	private final int id;
	private String name;
	private Gender gender;
	
	private TestAssociation testAssociation;
	private List<TestCollection> testCollection;
	
	public User(Integer id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	public TestAssociation getTestAssociation() {
		return testAssociation;
	}
	public void setTestAssociation(TestAssociation testAssociation) {
		this.testAssociation = testAssociation;
	}
	public List<TestCollection> getTestCollection() {
		return testCollection;
	}
	public void setTestCollection(List<TestCollection> testCollection) {
		this.testCollection = testCollection;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("User [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", gender=");
		builder.append(gender);
		builder.append(", testAssociation=");
		builder.append(testAssociation);
		builder.append(", testCollection=");
		builder.append(testCollection);
		builder.append("]");
		return builder.toString();
	}
	
}
