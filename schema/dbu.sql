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