-- DROP DATABASE IF EXISTS getaroom_mysql;

CREATE DATABASE IF NOT EXISTS getaroom_db;

USE getaroom_db;

DROP TABLE IF EXISTS users;

CREATE TABLE users(
    email VARCHAR(20) PRIMARY KEY,
    username VARCHAR(20),
    password VARCHAR(225),
    role VARCHAR(20)
);

INSERT INTO users
VALUES ("student@gmail.com" , "Student" , SHA2('Password', 512), "student" );

INSERT INTO users
VALUES ("security@gmail.com", "Security", SHA2('Password', 512), "security");

INSERT INTO users
VALUES ("analyst@gmail.com" , "Analyst" , SHA2('Password', 512), "analyst" );

DROP PROCEDURE IF EXISTS loggeIn;

DELIMITER &&
CREATE PROCEDURE loggeIn (IN username VARCHAR(20), IN password VARCHAR(20))
    BEGIN
        SELECT EXISTS(
            SELECT * FROM users
            WHERE users.username = username
            AND  users.password = SHA2(password,512));
    END &&  
DELIMITER ;  

-- CALL loggeIn("Student", "Password");
-- CALL loggeIn("Student", "Passwor");

DROP FUNCTION IF EXISTS register;

DELIMITER &&
CREATE FUNCTION register (username VARCHAR(20), email VARCHAR(20), password VARCHAR(20), role VARCHAR(20))
    RETURNS INT
    DETERMINISTIC
    BEGIN 
        IF (EXISTS(
            SELECT * FROM users
            WHERE users.username = username
            OR users.email = email
        )) THEN RETURN 0;
        ELSE
            INSERT INTO users VALUES (email, username, SHA2(password,512), role);
            RETURN 1;
        END IF;
    END &&
DELIMITER ; 

-- SELECT register ("Student", "student@gmail", "Password", "Student");
-- SELECT register ("Test", "test@gmail", "Password", "Student");
