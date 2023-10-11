DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS ratingMPA (
	id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	rating_name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
	film_id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	film_name varchar(255) NOT NULL,
	description varchar(255),
	release_date timestamp NOT NULL,
	duration_in_minutes integer NOT NULL,
	likes_count integer,
	rating_MPA_id integer REFERENCES ratingMPA (id)
);

CREATE TABLE IF NOT EXISTS genre (
	id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre (
	film_id integer REFERENCES films (film_id) ON DELETE CASCADE,
	genre_id integer REFERENCES genre (id) ON DELETE CASCADE,
	
	PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS viewers (
	viewer_id integer GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
	email varchar(255) NOT NULL,
	login varchar(255) NOT NULL,
	viewer_name varchar(255),
	birthday timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS friendships (
	viewer_id integer NOT NULL REFERENCES viewers (viewer_id) ON DELETE CASCADE,
	friend_id integer NOT NULL REFERENCES viewers (viewer_id) ON DELETE CASCADE,
	CONSTRAINT friendship_pk PRIMARY KEY(viewer_id, friend_id)
);

CREATE TABLE IF NOT EXISTS likes (
    viewer_id integer NOT NULL REFERENCES viewers (viewer_id) ON DELETE CASCADE,
    film_id integer NOT NULL REFERENCES films (film_id) ON DELETE CASCADE,
    CONSTRAINT likes_pk PRIMARY KEY(viewer_id, film_id)
);

ALTER TABLE viewers ADD CONSTRAINT IF NOT EXISTS uq_email UNIQUE (email);

ALTER TABLE viewers ADD CONSTRAINT IF NOT EXISTS uq_login UNIQUE (login);

