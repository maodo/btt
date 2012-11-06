# Tasks schema
 
# --- !Ups

CREATE TABLE user (
    id integer NOT NULL AUTO_INCREMENT,
    name varchar(255),
    pwd varchar(255),
    admin boolean
);

CREATE TABLE server (
    id integer NOT NULL AUTO_INCREMENT,
    name varchar(255),
    active boolean
);

CREATE TABLE task (
    id integer NOT NULL AUTO_INCREMENT,
    userId integer NOT NULL,
    serverId integer NOT NULL,
    startedAt timestamp NOT NULL,
    duration integer NOT NULL default -1,
    failed boolean NOT NULL default 0
);

alter table task add constraint fk_task_user foreign key (userId) references user (id);
alter table task add constraint fk_task_server foreign key (serverId) references server (id);


# --- !Downs
 
DROP TABLE user;
DROP TABLE server;
DROP TABLE build;
