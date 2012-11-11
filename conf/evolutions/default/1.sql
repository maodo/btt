# Tasks schema
 
# --- !Ups

CREATE TABLE users (
    id serial PRIMARY KEY,
    name varchar(255),
    pwd varchar(255),
    admin boolean
);

CREATE TABLE server (
    id serial PRIMARY KEY,
    name varchar(255),
    active boolean
);

CREATE TABLE task (
    id serial PRIMARY KEY,
    userId integer NOT NULL,
    serverId integer NOT NULL,
    startedAt integer NOT NULL,
    duration integer NOT NULL default 0,
    failed boolean NOT NULL default false
);

alter table task add constraint fk_task_user foreign key (userId) references users (id);
alter table task add constraint fk_task_server foreign key (serverId) references server (id);


# --- !Downs
 
DROP TABLE task;
DROP TABLE users;
DROP TABLE server;
