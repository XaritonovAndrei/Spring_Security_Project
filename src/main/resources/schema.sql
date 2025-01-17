DROP table if exists authorities;
DROP table if exists users;
--DROP table if exists testTable;


create table users (
    username varchar(50) not null primary key,
    password varchar(250) not null,
     enabled boolean not null
     );

create table authorities (
    username varchar(50) not null,
    authority varchar(50) not null,
    constraint fk_authorities_users foreign key(username) references users(username)
    );

create unique index ix_auth_username on authorities (username,authority);


--create table testTable(ID int not null AUTO_INCREMENT primary key,
--                    username varchar(50) not null,
--                    password varchar(500) not null,
--                    enabled boolean not null);

--create table users(ID int not null AUTO_INCREMENT primary key,
--                    username varchar(50) not null,
--                    password varchar(500) not null,
--                    enabled boolean not null);
--create table authorities (username varchar(50) not null,
--                    authority varchar(50) not null,
--                    constraint fk_authorities_users foreign key(username) references users(ID));
--create unique index ix_auth_username on authorities (username,authority);