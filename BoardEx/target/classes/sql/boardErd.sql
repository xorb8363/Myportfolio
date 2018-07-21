
/* Drop Tables */

DROP TABLE BOARD CASCADE CONSTRAINTS;

/* Create Tables */

CREATE TABLE BOARD
(
	b_num number NOT NULL,
	b_writer varchar2(20) NOT NULL,
	b_title varchar2(50) NOT NULL,
	b_contents varchar2(1999) NOT NULL,
	b_reg_date timestamp NOT NULL,
	b_read_count number DEFAULT 0 NOT NULL,
	b_image varchar2(1000),
	PRIMARY KEY (b_num)
);

/* Create Sequences */
CREATE sequence board_b_num_seq start with 1 increment by 1 nocache nocycle;