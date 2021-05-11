drop table if exists movie_genre;
drop table if exists movie_production_company;
drop table if exists movie;
drop table if exists genre;
drop table if exists production_company;

create table movie (
    movie_id bigint primary key,
    title text,
    budget bigint,
    popularity decimal,
    release_date date,
    revenue bigint
);

create table genre (
    genre_id bigint primary key,
    genre_name text
);

create table production_company (
    production_company_id bigint primary key,
    production_company_name text
);

create table movie_genre (
    movie_genre_id serial primary key,
    movie_id bigint references movie ( movie_id ),
    genre_id bigint references genre ( genre_id )
);

create table movie_production_company (
    movie_production_company_id serial primary key,
    movie_id bigint references movie ( movie_id ),
    production_company_id bigint references production_company ( production_company_id )
);
