# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table patient (
  id                        integer not null,
  first_name                varchar(255),
  father_name               varchar(255),
  gender                    varchar(255),
  phone                     bigint,
  age                       integer,
  date                      timestamp,
  department                varchar(255),
  doctor_name               varchar(255),
  doctor_id                 integer,
  visit                     integer,
  payment                   integer,
  constraint pk_patient primary key (id))
;

create sequence patient_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists patient;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists patient_seq;

