create table usersData
(
	id serial not null primary key,
	first_name varchar(50) not null,
	last_name varchar(50) not null,
	birth_date date not null,
	phone_no char(9)
			unique,
);
