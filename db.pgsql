--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.5
-- Dumped by pg_dump version 9.6.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: battery; Type: TABLE; Schema: public; Owner: jack
--

CREATE TABLE battery (
    id integer NOT NULL,
    level double precision,
    "timestamp" bigint,
    user_id integer
);


ALTER TABLE battery OWNER TO jack;

--
-- Name: battery_id_seq; Type: SEQUENCE; Schema: public; Owner: jack
--

CREATE SEQUENCE battery_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE battery_id_seq OWNER TO jack;

--
-- Name: battery_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jack
--

ALTER SEQUENCE battery_id_seq OWNED BY battery.id;


--
-- Name: gps; Type: TABLE; Schema: public; Owner: jack
--

CREATE TABLE gps (
    id integer NOT NULL,
    lat double precision,
    lon double precision,
    "timestamp" bigint,
    user_id integer
);


ALTER TABLE gps OWNER TO jack;

--
-- Name: gps_id_seq; Type: SEQUENCE; Schema: public; Owner: jack
--

CREATE SEQUENCE gps_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE gps_id_seq OWNER TO jack;

--
-- Name: gps_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jack
--

ALTER SEQUENCE gps_id_seq OWNED BY gps.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: jack
--

CREATE TABLE users (
    id integer NOT NULL,
    uuid character varying(36)
);


ALTER TABLE users OWNER TO jack;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: jack
--

CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE users_id_seq OWNER TO jack;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: jack
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- Name: battery id; Type: DEFAULT; Schema: public; Owner: jack
--

ALTER TABLE ONLY battery ALTER COLUMN id SET DEFAULT nextval('battery_id_seq'::regclass);


--
-- Name: gps id; Type: DEFAULT; Schema: public; Owner: jack
--

ALTER TABLE ONLY gps ALTER COLUMN id SET DEFAULT nextval('gps_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: jack
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- Data for Name: battery; Type: TABLE DATA; Schema: public; Owner: jack
--

COPY battery (id, level, "timestamp", user_id) FROM stdin;
4	4	1509830738221	\N
5	4	1509830738221	\N
6	4	1509830738221	\N
7	4	1509830738221	\N
8	4	1509830738221	\N
9	2	1	\N
10	4	0	16
11	4	0	16
12	4	0	17
13	4	0	17
14	4	0	17
15	4	0	17
16	4	0	18
17	4	0	18
\.


--
-- Name: battery_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jack
--

SELECT pg_catalog.setval('battery_id_seq', 17, true);


--
-- Data for Name: gps; Type: TABLE DATA; Schema: public; Owner: jack
--

COPY gps (id, lat, lon, "timestamp", user_id) FROM stdin;
1	4	4	0	17
2	4	4	0	17
3	4	4	0	17
4	4	4	0	18
5	4	4	0	18
6	4	4	0	18
\.


--
-- Name: gps_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jack
--

SELECT pg_catalog.setval('gps_id_seq', 6, true);


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: jack
--

COPY users (id, uuid) FROM stdin;
18	2a45b56b-7f38-4791-94d1-3f47e5f3e7f7
\.


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: jack
--

SELECT pg_catalog.setval('users_id_seq', 18, true);


--
-- Name: battery battery_pkey; Type: CONSTRAINT; Schema: public; Owner: jack
--

ALTER TABLE ONLY battery
    ADD CONSTRAINT battery_pkey PRIMARY KEY (id);


--
-- Name: gps gps_pkey; Type: CONSTRAINT; Schema: public; Owner: jack
--

ALTER TABLE ONLY gps
    ADD CONSTRAINT gps_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: jack
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- PostgreSQL database dump complete
--

