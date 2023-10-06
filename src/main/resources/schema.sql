CREATE TABLE IF NOT EXISTS ratingMPA (
	id serial NOT NULL PRIMARY KEY,
	rating_name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
	film_id serial NOT NULL PRIMARY KEY,
	film_name varchar(255) NOT NULL,
	description varchar(255),
	release_date timestamp NOT NULL,
	duration_in_minutes integer NOT NULL,
	likes_count integer,
	rating_MPA_id integer REFERENCES ratingMPA (id)
);

CREATE TABLE IF NOT EXISTS genre (
	genre_id serial NOT NULL PRIMARY KEY,
	genre_name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre (
	film_id integer REFERENCES films (film_id),
	genre_id integer REFERENCES genre (genre_id),
	
	PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS viewers (
	viewer_id serial NOT NULL PRIMARY KEY,
	email varchar(255) NOT NULL,
	login varchar(255) NOT NULL,
	viewer_name varchar(255),
	birthday timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS viewer_friends (
	viewer_id integer NOT NULL REFERENCES viewers (viewer_id),
	friend_id integer NOT NULL REFERENCES viewers (viewer_id),
	is_approved boolean DEFAULT false,
	CONSTRAINT friendship_pk PRIMARY KEY(viewer_id, friend_id)
);

ALTER TABLE viewers ADD CONSTRAINT uq_email UNIQUE (email);

ALTER TABLE viewers ADD CONSTRAINT uq_login UNIQUE (login);