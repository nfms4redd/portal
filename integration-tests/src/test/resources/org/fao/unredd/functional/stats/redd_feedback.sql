CREATE TABLE redd_feedback (
	id serial,
	geometry geometry('GEOMETRY', 900913),
	comment varchar NOT NULL,
	layer_name varchar NOT NULL,
	layer_date varchar,
	date timestamp NOT NULL,
	email varchar NOT NULL,
	verification_code varchar,
	language varchar,
	state int
);
								
								