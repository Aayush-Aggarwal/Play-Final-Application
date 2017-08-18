
# --- !Ups
CREATE TABLE if NOT EXISTS "data"(

id serial NOT NULL,
firstname varchar(20) NOT NULL,
middlename varchar(20)  ,
lastname varchar(20) NOT NULL,
age int NOT NULL,
email varchar(30) NOT NULL,
password varchar(30) NOT NULL,
street varchar(20) NOT NULL,
streetno int NOT NULL,
city varchar(20) NOT NULL ,
gender varchar(20) NOT NULL,
phoneno bigint NOT NULL ,
status varchar(20) NOT NULL,
PRIMARY KEY(id)
);


CREATE TABLE IF NOT EXISTS hobbytable (
id serial NOT NULL,
hobby VARCHAR(30),
PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS usertohobbyid (
id serial NOT NULL,
userid INT NOT NULL,
hobby_id INT NOT NULL,
PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS assignment (
id serial NOT NULL,
title VARCHAR(30) NOT NULL,
description VARCHAR(100) NOT NULL,
PRIMARY KEY(id)
);

INSERT INTO usertable VALUES
(1, 'Rahul', 'Chandra', 'Sharma', 9999819878, 'rahul@knoldus.com', 'password', 'male', 23, true, true);

INSERT INTO hobbytable VALUES
(1, 'Programming'),
(2, 'Reading'),
(3, 'Sports'),
(4, 'Writing'),
(5, 'Swimming');

# --- !Downs

DROP TABLE assignment;
DROP TABLE usertohobbyid;
DROP TABLE hobbytable;
DROP TABLE usertable;
