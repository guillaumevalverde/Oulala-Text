# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table post (
  _type                     integer(31) not null,
  id                        bigint auto_increment not null,
  sending_statut            integer,
  timestamp                 bigint,
  id_on_phone               varchar(255),
  creator_id                bigint,
  url                       varchar(255),
  message                   varchar(255),
  constraint pk_post primary key (id))
;

create table user (
  id                        bigint auto_increment not null,
  user_id                   varchar(255),
  password                  varchar(255),
  join_pwd                  varchar(255),
  url_pic                   varchar(255),
  partner_id                varchar(255),
  salt                      varchar(255),
  registration_id           varchar(255),
  constraint pk_user primary key (id))
;

alter table post add constraint fk_post_creator_1 foreign key (creator_id) references user (id) on delete restrict on update restrict;
create index ix_post_creator_1 on post (creator_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table post;

drop table user;

SET FOREIGN_KEY_CHECKS=1;

