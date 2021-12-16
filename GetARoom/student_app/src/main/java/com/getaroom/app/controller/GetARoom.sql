-- DROP DATABASE IF EXISTS getaroom_mysql;

-- CREATE DATABASE getaroom_mysql;

USE getaroom_db;

DROP TABLE IF EXISTS users;

CREATE TABLE users(
    username VARCHAR(20),
    email VARCHAR(20) PRIMARY KEY,
    password VARCHAR(225),
    role VARCHAR(20)
);

INSERT INTO users
VALUES ("Student" , "student@gmail.com" , SHA2('Password', 512), "student" );

INSERT INTO users
VALUES ("Security", "security@gmail.com", SHA2('Password', 512), "security");

INSERT INTO users
VALUES ("Analyst" , "analyst@gmail.com" , SHA2('Password', 512), "analyst" );

DROP PROCEDURE IF EXISTS loggeIn;

DELIMITER &&
CREATE PROCEDURE loggeIn (IN username VARCHAR(20), IN password VARCHAR(255))
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
            INSERT INTO users VALUES (username, email, SHA2(password,512), role);
            RETURN 1;
        END IF;
    END &&
DELIMITER ; 

-- SELECT register ("Student", "student@gmail", "Password", "Student");
-- SELECT register ("Test", "test@gmail", "Password", "Student");