CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT,
  name VARCHAR(255),
  gender ENUM('M', 'F'),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS user_association (
  user_id INT NOT NULL,
  test_association VARCHAR(255) NOT NULL,
  PRIMARY KEY (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS user_collection (
  user_id INT NOT NULL,
  test_collection VARCHAR(255) NOT NULL,
  PRIMARY KEY (user_id, test_collection)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO users (id, name, gender) VALUES (NULL, '猪头', 'F');
INSERT INTO user_association (user_id, test_association) VALUES(1, 'abc123');
INSERT INTO user_collection (user_id, test_collection) VALUES(1, 'kkk1');
INSERT INTO user_collection (user_id, test_collection) VALUES(1, 'kkk2');