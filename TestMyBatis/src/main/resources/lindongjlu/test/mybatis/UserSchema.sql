CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT,
    name VARCHAR(255),
    gender ENUM('M', 'F'),
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO users (id, name, gender) VALUES (NULL, '猪头', 'F');