--
-- PostgreSQL database dump
--

-- Dumped from database version 17.4
-- Dumped by pg_dump version 17.4

-- Started on 2025-06-14 16:08:42

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 234 (class 1259 OID 26540)
-- Name: documents; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.documents (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    path character varying(1024) NOT NULL,
    uploaded_at timestamp without time zone DEFAULT now()
);


ALTER TABLE public.documents OWNER TO postgres;

--
-- TOC entry 233 (class 1259 OID 26539)
-- Name: documents_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.documents_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.documents_id_seq OWNER TO postgres;

--
-- TOC entry 5030 (class 0 OID 0)
-- Dependencies: 233
-- Name: documents_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.documents_id_seq OWNED BY public.documents.id;


--
-- TOC entry 217 (class 1259 OID 26390)
-- Name: economic_models; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.economic_models (
    id bigint NOT NULL,
    model_type character varying(100) NOT NULL,
    name character varying(255) NOT NULL,
    description text,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL,
    formula text
);


ALTER TABLE public.economic_models OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 26397)
-- Name: economic_models_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.economic_models_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.economic_models_id_seq OWNER TO postgres;

--
-- TOC entry 5031 (class 0 OID 0)
-- Dependencies: 218
-- Name: economic_models_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.economic_models_id_seq OWNED BY public.economic_models.id;


--
-- TOC entry 219 (class 1259 OID 26398)
-- Name: model_parameters; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.model_parameters (
    id bigint NOT NULL,
    model_id bigint NOT NULL,
    param_name character varying(100) NOT NULL,
    param_type character varying(50) NOT NULL,
    param_value text NOT NULL,
    display_name character varying(255),
    description text,
    custom_order integer DEFAULT 0
);


ALTER TABLE public.model_parameters OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 26404)
-- Name: model_parameters_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.model_parameters_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.model_parameters_id_seq OWNER TO postgres;

--
-- TOC entry 5032 (class 0 OID 0)
-- Dependencies: 220
-- Name: model_parameters_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.model_parameters_id_seq OWNED BY public.model_parameters.id;


--
-- TOC entry 221 (class 1259 OID 26405)
-- Name: model_results; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.model_results (
    id bigint NOT NULL,
    model_id bigint NOT NULL,
    result_type character varying(100) NOT NULL,
    result_data text NOT NULL,
    calculated_at timestamp without time zone DEFAULT now() NOT NULL,
    user_id bigint
);


ALTER TABLE public.model_results OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 26411)
-- Name: model_results_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.model_results_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.model_results_id_seq OWNER TO postgres;

--
-- TOC entry 5033 (class 0 OID 0)
-- Dependencies: 222
-- Name: model_results_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.model_results_id_seq OWNED BY public.model_results.id;


--
-- TOC entry 223 (class 1259 OID 26412)
-- Name: password_reset_tokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.password_reset_tokens (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    code character varying(6) NOT NULL,
    expires_at timestamp without time zone NOT NULL
);


ALTER TABLE public.password_reset_tokens OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 26415)
-- Name: password_reset_tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.password_reset_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.password_reset_tokens_id_seq OWNER TO postgres;

--
-- TOC entry 5034 (class 0 OID 0)
-- Dependencies: 224
-- Name: password_reset_tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.password_reset_tokens_id_seq OWNED BY public.password_reset_tokens.id;


--
-- TOC entry 225 (class 1259 OID 26416)
-- Name: refresh_tokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.refresh_tokens (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    token character varying(512) NOT NULL,
    expiry_date timestamp with time zone NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.refresh_tokens OWNER TO postgres;

--
-- TOC entry 226 (class 1259 OID 26422)
-- Name: refresh_tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.refresh_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.refresh_tokens_id_seq OWNER TO postgres;

--
-- TOC entry 5035 (class 0 OID 0)
-- Dependencies: 226
-- Name: refresh_tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.refresh_tokens_id_seq OWNED BY public.refresh_tokens.id;


--
-- TOC entry 236 (class 1259 OID 26576)
-- Name: reports; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reports (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    model_name character varying(255) NOT NULL,
    path character varying(255) NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    language character varying(10) NOT NULL,
    params text NOT NULL,
    result text NOT NULL,
    llm_messages text
);


ALTER TABLE public.reports OWNER TO postgres;

--
-- TOC entry 235 (class 1259 OID 26575)
-- Name: reports_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.reports_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.reports_id_seq OWNER TO postgres;

--
-- TOC entry 5036 (class 0 OID 0)
-- Dependencies: 235
-- Name: reports_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.reports_id_seq OWNED BY public.reports.id;


--
-- TOC entry 227 (class 1259 OID 26423)
-- Name: user_model_parameter; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.user_model_parameter (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    model_id bigint NOT NULL,
    parameter_id bigint NOT NULL,
    value character varying(255) NOT NULL
);


ALTER TABLE public.user_model_parameter OWNER TO postgres;

--
-- TOC entry 228 (class 1259 OID 26426)
-- Name: user_model_parameter_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_model_parameter_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.user_model_parameter_id_seq OWNER TO postgres;

--
-- TOC entry 5037 (class 0 OID 0)
-- Dependencies: 228
-- Name: user_model_parameter_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.user_model_parameter_id_seq OWNED BY public.user_model_parameter.id;


--
-- TOC entry 229 (class 1259 OID 26427)
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    username character varying(50) NOT NULL,
    email character varying(100) NOT NULL,
    password_hash character varying(255) NOT NULL,
    enabled boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT now() NOT NULL,
    updated_at timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.users OWNER TO postgres;

--
-- TOC entry 230 (class 1259 OID 26433)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.users_id_seq OWNER TO postgres;

--
-- TOC entry 5038 (class 0 OID 0)
-- Dependencies: 230
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 231 (class 1259 OID 26434)
-- Name: verification_tokens; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.verification_tokens (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    code character varying(6) NOT NULL,
    expires_at timestamp without time zone NOT NULL
);


ALTER TABLE public.verification_tokens OWNER TO postgres;

--
-- TOC entry 232 (class 1259 OID 26437)
-- Name: verification_tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.verification_tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.verification_tokens_id_seq OWNER TO postgres;

--
-- TOC entry 5039 (class 0 OID 0)
-- Dependencies: 232
-- Name: verification_tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE public.verification_tokens_id_seq OWNED BY public.verification_tokens.id;


--
-- TOC entry 4803 (class 2604 OID 26555)
-- Name: documents id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documents ALTER COLUMN id SET DEFAULT nextval('public.documents_id_seq'::regclass);


--
-- TOC entry 4787 (class 2604 OID 26438)
-- Name: economic_models id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.economic_models ALTER COLUMN id SET DEFAULT nextval('public.economic_models_id_seq'::regclass);


--
-- TOC entry 4790 (class 2604 OID 26439)
-- Name: model_parameters id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.model_parameters ALTER COLUMN id SET DEFAULT nextval('public.model_parameters_id_seq'::regclass);


--
-- TOC entry 4792 (class 2604 OID 26440)
-- Name: model_results id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.model_results ALTER COLUMN id SET DEFAULT nextval('public.model_results_id_seq'::regclass);


--
-- TOC entry 4794 (class 2604 OID 26441)
-- Name: password_reset_tokens id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_reset_tokens ALTER COLUMN id SET DEFAULT nextval('public.password_reset_tokens_id_seq'::regclass);


--
-- TOC entry 4795 (class 2604 OID 26442)
-- Name: refresh_tokens id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_tokens ALTER COLUMN id SET DEFAULT nextval('public.refresh_tokens_id_seq'::regclass);


--
-- TOC entry 4805 (class 2604 OID 26579)
-- Name: reports id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reports ALTER COLUMN id SET DEFAULT nextval('public.reports_id_seq'::regclass);


--
-- TOC entry 4797 (class 2604 OID 26518)
-- Name: user_model_parameter id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_model_parameter ALTER COLUMN id SET DEFAULT nextval('public.user_model_parameter_id_seq'::regclass);


--
-- TOC entry 4798 (class 2604 OID 26444)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 4802 (class 2604 OID 26445)
-- Name: verification_tokens id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_tokens ALTER COLUMN id SET DEFAULT nextval('public.verification_tokens_id_seq'::regclass);


--
-- TOC entry 5022 (class 0 OID 26540)
-- Dependencies: 234
-- Data for Name: documents; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.documents (id, user_id, name, path, uploaded_at) FROM stdin;
\.


--
-- TOC entry 5005 (class 0 OID 26390)
-- Dependencies: 217
-- Data for Name: economic_models; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.economic_models (id, model_type, name, description, created_at, updated_at, formula) FROM stdin;
1	DemandSupply	Спрос и предложение^Supply and demand	Графическое отображение рыночного спроса и предложения с равновесием и излишками.^Graphical representation of market supply and demand with equilibrium and surpluses\n	2025-05-31 17:28:57.474194	2025-05-31 17:28:57.474194	Qd = a - bP; Qs = c + dP
2	Elasticity	Эластичность спроса^Price elasticity of demand	Анализ эластичности спроса по цене с визуализацией для разных категорий товаров.^Analysis of price elasticity of demand with visualization for different product categories\n	2025-05-31 17:28:57.474194	2025-05-31 17:28:57.474194	E = (ΔQ / Q) / (ΔP / P)
6	SolowGrowth	Модель роста Солоу^Solow growth model	Экономическая модель долгосрочного роста с фазовой диаграммой и устойчивым состоянием.^Long-term economic growth model with phase diagram and steady state\n	2025-05-31 17:30:49.787608	2025-05-31 17:30:49.787608	Y = AK^αL^(1-α)
5	ISLM	IS-LM модель^IS-LM model	Динамическая модель равновесия на товарном и денежном рынках.^Dynamic equilibrium model for goods and money markets\n	2025-05-31 17:30:49.787608	2025-05-31 17:30:49.787608	IS: Y = C(Y - T) + I(r) + G; LM: M/P = L(r, Y)
9	CAPM	CAPM^CAPM	Модель ценообразования капитальных активов, построение SML и эффективной границы.^Capital Asset Pricing Model (CAPM) with SML and efficient frontier construction\n	2025-05-31 17:31:53.881438	2025-05-31 17:31:53.881438	E(R_i) = R_f + β_i(E(R_m) - R_f)
10	BlackScholes	Модель Блэка-Шоулза^Black-Scholes model	Оценка цены опционов на основе модели Блэка-Шоулза, анализ греков.^Option pricing based on the Black-Scholes model, analysis of Greeks\n	2025-05-31 17:31:53.881438	2025-05-31 17:31:53.881438	C = S(N(d_1)) - Xe^{-rT}N(d_2)
3	CompetitionVsMonopoly	Совершенная конкуренция vs Монополия^Perfect competition vs Monopoly	Сравнение двух рыночных структур: совершенная конкуренция и монополия.^Comparison of two market structures: perfect competition and monopoly\n	2025-05-31 17:28:57.474194	2025-05-31 17:28:57.474194	MR = MC; P = MC (совершенная конкуренция); P > MC (монополия)
7	PhillipsCurve	Кривая Филлипса^Phillips curve	Анализ взаимосвязи между инфляцией и безработицей, построение краткосрочной и долгосрочной кривых.^Analysis of the relationship between inflation and unemployment, construction of short-run and long-run curves\n	2025-05-31 17:30:49.787608	2025-05-31 17:30:49.787608	U = U(Q1, Q2); P1Q1 + P2Q2 = I
8	ADAS	Модель AD-AS^AD-AS model	Модель совокупного спроса и предложения с анализом инфляционных и рецессионных разрывов.^Aggregate demand and supply model with analysis of inflationary and recessionary gaps\n	2025-05-31 17:31:53.881438	2025-05-31 17:31:53.881438	AD: Y = C + I + G + NX; AS: Зависит от уровня цен
4	ConsumerChoice	Теория потребительского выбора^Consumer choice theory	Визуализация кривых безразличия, бюджетных ограничений и эффектов дохода/замещения.^Visualization of indifference curves, budget constraints, and income/substitution effects\n	2025-05-31 17:30:49.787608	2025-05-31 17:30:49.787608	U = U(Q1, Q2); P1Q1 + P2Q2 = I
\.


--
-- TOC entry 5007 (class 0 OID 26398)
-- Dependencies: 219
-- Data for Name: model_parameters; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.model_parameters (id, model_id, param_name, param_type, param_value, display_name, description, custom_order) FROM stdin;
19	4	I	double	100.0	Доход потребителя^Consumer income	Сумма бюджета потребителя^Total consumer budget	1
7	2	Q0	double	159	Исходный спрос^Initial demand	Спрос до изменения цены^Demand before price change	1
12	3	a	double	100.0	Коэффициент спроса a^Demand coefficient a	Свободный член в функции спроса: Qd = a - bP^Intercept in demand function: Qd = a - bP	1
45	7	pi_e	double	2.0	Ожидаемая инфляция (πₑ)^Expected inflation (πₑ)	Инфляционные ожидания^Expected inflation	5
42	7	u0	double	6.0	Начальный уровень безработицы (u₀)^Initial unemployment rate (u₀)	Исходное значение безработицы (%)^Initial unemployment rate (%)	2
40	6	L0	double	100.0	Начальная численность рабочей силы (L0)^Initial labor force (L0)	Начальное количество работников^Initial number of workers	7
11	2	category	string	Обычный товар, Необычный товар, третий товар	Категория товара^Product category	Категория для сравнения эластичности^Category for elasticity comparison	5
6	1	P_max	double	50.0	50.0^50.0	Максимальное значение цены на графике^Maximum price value on the chart	6
28	5	G	double	30.0	Государственные расходы (G)^Government spending (G)	Объем государственных расходов^Government expenditure	5
53	9	beta	double	1.0	Бета портфеля (β)^Portfolio beta (β)	Коэффициент чувствительности к рынку^Market sensitivity coefficient (beta)	3
47	8	P0	double	100.0	Исходный уровень цен (P₀)^Initial price level (P₀)	Начальный уровень цен^Initial price level	2
48	8	AD_slope	double	-0.5	Наклон AD^Slope of AD	Наклон кривой совокупного спроса^Slope of aggregate demand curve	3
33	5	l2	double	0.5	Чувствительность спроса на деньги к ставке (l2)^Interest rate sensitivity of money demand (l2)	Параметр LM-кривой^LM curve parameter	10
27	5	b	double	5.0	Чувствительность инвестиций к ставке (b)^Interest rate sensitivity of investment (b)	Параметр в инвестиционной функции^Investment function parameter	4
52	9	Rm	double	0.10	Доходность рынка (Rm)^Market return (Rm)	Ожидаемая доходность рыночного портфеля^Expected return of the market portfolio	2
55	9	alpha	double	0.0	Альфа портфеля (α)^Portfolio alpha (α)	Абсолютная доходность относительно SML^Absolute return relative to SML	5
15	3	d	double	1.5	Коэффициент предложения d^Supply coefficient d	Чувствительность предложения к цене^Price elasticity of supply	4
37	6	alpha	double	0.3	Коэффициент капитала (α)^Capital coefficient (α)	Доля капитала в производственной функции^Capital share in production function	4
10	2	P1	double	12.0	Новая цена^New price	Цена после изменения^Price after change	4
49	8	AS_slope	double	0.7	Наклон AS^Slope of AS	Наклон кривой совокупного предложения^Slope of aggregate supply curve	4
34	6	s	double	0.25	Ставка сбережений (s)^Savings rate (s)	Доля сбережений в ВВП^Savings rate in GDP	1
32	5	l1	double	0.2	Чувствительность спроса на деньги к доходу (l1)^Income sensitivity of money demand (l1)	Параметр LM-кривой^LM curve parameter	9
60	10	sigma	double	0.20	Волатильность (σ)^Volatility (σ)	Годовая волатильность базового актива^Annual volatility of underlying asset	5
44	7	u_n	double	5.0	Естественный уровень безработицы (uⁿ)^Natural rate of unemployment (uⁿ)	Долгосрочное значение безработицы^Long-run unemployment rate	4
14	3	c	double	20.0	Коэффициент предложения c^Supply coefficient c	Свободный член в функции предложения: Qs = c + dP^Intercept in supply function: Qs = c + dP	3
16	3	MC	double	10.0	Маржинальные издержки (MC)^Marginal cost (MC)	Постоянные маржинальные издержки для монополии^Constant marginal cost for monopoly	5
30	5	Ms	double	100.0	Денежная масса (Ms)^Money supply (Ms)	Общее количество денег в экономике^Total money supply	7
8	2	P0	double	10.0	Исходная цена^Initial price	Цена до изменения^Price before change	2
39	6	K0	double	50.0	Начальный капитал (K0)^Initial capital (K0)	Начальное значение запаса капитала^Initial capital stock	6
46	8	Y_pot	double	100.0	Потенциальный ВВП (Yₚ)^Potential GDP (Yₚ)	Долгосрочное равновесное значение ВВП^Long-run equilibrium GDP	1
36	6	delta	double	0.05	Норма амортизации (δ)^Depreciation rate (δ)	Годовая амортизация капитала^Annual capital depreciation	3
1	1	a	double	50.0	50.0^50.0	Свободный член в функции спроса: Qd = a - bP^Intercept in demand function: Qd = a - bP	1
57	10	K	double	100.0	Цена исполнения опциона (K)^Option strike price (K)	Цена страйк (исполнения)^Strike price (exercise price)	2
2	1	b	double	2.0	2.0^2.0	Чувствительность спроса к цене в функции спроса^Demand price sensitivity in demand function	2
22	4	alpha	double	0.5	Коэффициент предпочтений (α)^Preference coefficient (α)	Коэффициент в функции полезности U = X^α * Y^(1-α)^Utility function coefficient in U = X^α * Y^(1-α)	4
24	5	C0	double	20.0	Автономное потребление (C0)^Autonomous consumption (C0)	Потребление при нулевом доходе^Consumption at zero income	1
29	5	T	double	25.0	Налоги (T)^Taxes (T)	Суммарные налоги^Total taxes	6
21	4	Py	double	20.0	Цена товара Y^Price of good Y	Цена за единицу товара Y^Price per unit of good Y	3
51	9	Rf	double	0.05	Безрисковая ставка (Rf)^Risk-free rate (Rf)	Процентная ставка по безрисковым активам^Risk-free interest rate	1
58	10	T	double	1.0	Срок до экспирации (T)^Time to expiration (T)	Время до экспирации в годах^Time to expiration (years)	3
54	9	sigma	double	0.15	Стандартное отклонение доходности (σ)^Standard deviation of return (σ)	Риск портфеля^Portfolio risk	4
4	1	d	double	2	4^4	Чувствительность предложения к цене^Supply price sensitivity	4
25	5	c1	double	0.8	Предельная склонность к потреблению (c1)^Marginal propensity to consume (c1)	Доля дохода	2
5	1	P_min	double	1	0.0^0.0	Минимальное значение цены на графике^Minimum price value on the chart	5
41	7	pi0	double	2.0	Начальный уровень инфляции (π₀)^Initial inflation rate (π₀)	Исходное значение инфляции (%)^Initial inflation rate (%)	1
20	4	Px	double	10.0	Цена товара X^Price of good X	Цена за единицу товара X^Price per unit of good X	2
61	10	option_type	string	call	Тип опциона^Option type	call (покупка) или put (продажа)^Option type: call (buy) or put (sell)	6
23	4	U	double	50.0	Уровень полезности^Utility level	Требуемый уровень полезности^Required utility level	5
13	3	b	double	3.0	Коэффициент спроса b^Demand coefficient b	Чувствительность спроса к цене^Price elasticity of demand	2
38	6	A	double	1.0	Технологический коэффициент (A)^Technology coefficient (A)	Начальный уровень технологии^Initial technology level	5
3	1	c	double	10.0	10.0^10.0	Свободный член в функции предложения: Qs = c + dP^Intercept in supply function: Qs = c + dP	3
26	5	I0	double	15.0	Автономные инвестиции (I0)^Autonomous investment (I0)	Инвестиции	3
9	2	Q1	double	90.0	Новый спрос^New demand	Спрос после изменения цены^Demand after price change	3
18	3	market_type	string	competition	Тип рынка^Market type	competition (совершенная конкуренция) или monopoly (монополия)^Market structure: competition (perfect competition) or monopoly	7
59	10	r	double	0.05	Безрисковая ставка (r)^Risk-free rate (r)	Годовая безрисковая ставка^Annual risk-free rate	4
43	7	alpha	double	-0.5	Наклон кривой (α)^Slope (α)	Чувствительность инфляции к безработице (π = πₑ - α(u-uⁿ))^Inflation sensitivity to unemployment (π = πₑ - α(u-uⁿ))	3
31	5	L0	double	0.5	Автономный спрос на деньги (L0)^Autonomous money demand (L0)	Спрос на деньги при нулевом доходе^Money demand at zero income	8
17	3	FC	double	50.0	Постоянные издержки (FC)^Fixed cost (FC)	Постоянные издержки для обоих структур^Fixed cost for both structures	6
35	6	n	double	0.02	Темп прироста населения (n)^Population growth rate (n)	Годовой прирост населения^Annual population growth	2
56	10	S	double	100.0	Текущая цена актива (S)^Current asset price (S)	Рыночная цена базового актива^Market price of underlying asset	1
50	8	shock	double	0.0	Внешний шок^External shock	Величина внешнего шока (спрос/предложение)^Magnitude of external shock (demand/supply)	5
\.


--
-- TOC entry 5009 (class 0 OID 26405)
-- Dependencies: 221
-- Data for Name: model_results; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.model_results (id, model_id, result_type, result_data, calculated_at, user_id) FROM stdin;
\.


--
-- TOC entry 5011 (class 0 OID 26412)
-- Dependencies: 223
-- Data for Name: password_reset_tokens; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.password_reset_tokens (id, user_id, code, expires_at) FROM stdin;
\.


--
-- TOC entry 5013 (class 0 OID 26416)
-- Dependencies: 225
-- Data for Name: refresh_tokens; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.refresh_tokens (id, user_id, token, expiry_date, created_at) FROM stdin;
\.


--
-- TOC entry 5024 (class 0 OID 26576)
-- Dependencies: 236
-- Data for Name: reports; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.reports (id, user_id, model_id, name, model_name, path, created_at, language, params, result, llm_messages) FROM stdin;
\.


--
-- TOC entry 5015 (class 0 OID 26423)
-- Dependencies: 227
-- Data for Name: user_model_parameter; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.user_model_parameter (id, user_id, model_id, parameter_id, value) FROM stdin;
\.


--
-- TOC entry 5017 (class 0 OID 26427)
-- Dependencies: 229
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.users (id, username, email, password_hash, enabled, created_at, updated_at) FROM stdin;
33	rein	kekandrek25@gmail.com	$2a$10$XrIQv.wnODGAqyuvMfWAh.zOUKp77yMHOucjviA9z5dfrD50qR9eS	t	2025-05-26 09:48:58.047753	2025-05-26 09:48:58.047753
\.


--
-- TOC entry 5019 (class 0 OID 26434)
-- Dependencies: 231
-- Data for Name: verification_tokens; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY public.verification_tokens (id, user_id, code, expires_at) FROM stdin;
\.


--
-- TOC entry 5040 (class 0 OID 0)
-- Dependencies: 233
-- Name: documents_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.documents_id_seq', 14, true);


--
-- TOC entry 5041 (class 0 OID 0)
-- Dependencies: 218
-- Name: economic_models_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.economic_models_id_seq', 1, false);


--
-- TOC entry 5042 (class 0 OID 0)
-- Dependencies: 220
-- Name: model_parameters_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.model_parameters_id_seq', 61, true);


--
-- TOC entry 5043 (class 0 OID 0)
-- Dependencies: 222
-- Name: model_results_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.model_results_id_seq', 135, true);


--
-- TOC entry 5044 (class 0 OID 0)
-- Dependencies: 224
-- Name: password_reset_tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.password_reset_tokens_id_seq', 39, true);


--
-- TOC entry 5045 (class 0 OID 0)
-- Dependencies: 226
-- Name: refresh_tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.refresh_tokens_id_seq', 269, true);


--
-- TOC entry 5046 (class 0 OID 0)
-- Dependencies: 235
-- Name: reports_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.reports_id_seq', 39, true);


--
-- TOC entry 5047 (class 0 OID 0)
-- Dependencies: 228
-- Name: user_model_parameter_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.user_model_parameter_id_seq', 123, true);


--
-- TOC entry 5048 (class 0 OID 0)
-- Dependencies: 230
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.users_id_seq', 33, true);


--
-- TOC entry 5049 (class 0 OID 0)
-- Dependencies: 232
-- Name: verification_tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.verification_tokens_id_seq', 28, true);


--
-- TOC entry 4841 (class 2606 OID 26557)
-- Name: documents documents_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documents
    ADD CONSTRAINT documents_pkey PRIMARY KEY (id);


--
-- TOC entry 4808 (class 2606 OID 26447)
-- Name: economic_models economic_models_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.economic_models
    ADD CONSTRAINT economic_models_pkey PRIMARY KEY (id);


--
-- TOC entry 4810 (class 2606 OID 26449)
-- Name: model_parameters model_parameters_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.model_parameters
    ADD CONSTRAINT model_parameters_pkey PRIMARY KEY (id);


--
-- TOC entry 4812 (class 2606 OID 26451)
-- Name: model_results model_results_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.model_results
    ADD CONSTRAINT model_results_pkey PRIMARY KEY (id);


--
-- TOC entry 4816 (class 2606 OID 26453)
-- Name: password_reset_tokens password_reset_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT password_reset_tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 4818 (class 2606 OID 26455)
-- Name: password_reset_tokens password_reset_tokens_user_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT password_reset_tokens_user_id_key UNIQUE (user_id);


--
-- TOC entry 4820 (class 2606 OID 26457)
-- Name: refresh_tokens refresh_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 4822 (class 2606 OID 26459)
-- Name: refresh_tokens refresh_tokens_token_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_token_key UNIQUE (token);


--
-- TOC entry 4847 (class 2606 OID 26584)
-- Name: reports reports_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (id);


--
-- TOC entry 4824 (class 2606 OID 26520)
-- Name: user_model_parameter user_model_parameter_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_model_parameter
    ADD CONSTRAINT user_model_parameter_pkey PRIMARY KEY (id);


--
-- TOC entry 4826 (class 2606 OID 26463)
-- Name: user_model_parameter user_model_parameter_user_id_parameter_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_model_parameter
    ADD CONSTRAINT user_model_parameter_user_id_parameter_id_key UNIQUE (user_id, parameter_id);


--
-- TOC entry 4830 (class 2606 OID 26465)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 4832 (class 2606 OID 26467)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4834 (class 2606 OID 26469)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 4837 (class 2606 OID 26471)
-- Name: verification_tokens verification_tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_tokens
    ADD CONSTRAINT verification_tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 4839 (class 2606 OID 26473)
-- Name: verification_tokens verification_tokens_user_id_key; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_tokens
    ADD CONSTRAINT verification_tokens_user_id_key UNIQUE (user_id);


--
-- TOC entry 4842 (class 1259 OID 26554)
-- Name: idx_documents_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_documents_user_id ON public.documents USING btree (user_id);


--
-- TOC entry 4843 (class 1259 OID 26597)
-- Name: idx_reports_created_at; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_reports_created_at ON public.reports USING btree (created_at);


--
-- TOC entry 4844 (class 1259 OID 26596)
-- Name: idx_reports_model_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_reports_model_id ON public.reports USING btree (model_id);


--
-- TOC entry 4845 (class 1259 OID 26595)
-- Name: idx_reports_user_id; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_reports_user_id ON public.reports USING btree (user_id);


--
-- TOC entry 4814 (class 1259 OID 26474)
-- Name: idx_reset_expires; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_reset_expires ON public.password_reset_tokens USING btree (expires_at);


--
-- TOC entry 4827 (class 1259 OID 26475)
-- Name: idx_users_email; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_users_email ON public.users USING btree (email);


--
-- TOC entry 4828 (class 1259 OID 26476)
-- Name: idx_users_username; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_users_username ON public.users USING btree (username);


--
-- TOC entry 4835 (class 1259 OID 26477)
-- Name: idx_verify_expires; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx_verify_expires ON public.verification_tokens USING btree (expires_at);


--
-- TOC entry 4813 (class 1259 OID 26531)
-- Name: uniq_model_result_user_model; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX uniq_model_result_user_model ON public.model_results USING btree (user_id, model_id);


--
-- TOC entry 4857 (class 2606 OID 26549)
-- Name: documents documents_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.documents
    ADD CONSTRAINT documents_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4848 (class 2606 OID 26478)
-- Name: model_parameters model_parameters_model_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.model_parameters
    ADD CONSTRAINT model_parameters_model_id_fkey FOREIGN KEY (model_id) REFERENCES public.economic_models(id) ON DELETE CASCADE;


--
-- TOC entry 4849 (class 2606 OID 26483)
-- Name: model_results model_results_model_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.model_results
    ADD CONSTRAINT model_results_model_id_fkey FOREIGN KEY (model_id) REFERENCES public.economic_models(id) ON DELETE CASCADE;


--
-- TOC entry 4850 (class 2606 OID 26526)
-- Name: model_results model_results_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.model_results
    ADD CONSTRAINT model_results_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 4851 (class 2606 OID 26488)
-- Name: password_reset_tokens password_reset_tokens_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.password_reset_tokens
    ADD CONSTRAINT password_reset_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4852 (class 2606 OID 26493)
-- Name: refresh_tokens refresh_tokens_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.refresh_tokens
    ADD CONSTRAINT refresh_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4858 (class 2606 OID 26590)
-- Name: reports reports_model_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_model_id_fkey FOREIGN KEY (model_id) REFERENCES public.economic_models(id);


--
-- TOC entry 4859 (class 2606 OID 26585)
-- Name: reports reports_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- TOC entry 4853 (class 2606 OID 26498)
-- Name: user_model_parameter user_model_parameter_model_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_model_parameter
    ADD CONSTRAINT user_model_parameter_model_id_fkey FOREIGN KEY (model_id) REFERENCES public.economic_models(id) ON DELETE CASCADE;


--
-- TOC entry 4854 (class 2606 OID 26503)
-- Name: user_model_parameter user_model_parameter_parameter_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_model_parameter
    ADD CONSTRAINT user_model_parameter_parameter_id_fkey FOREIGN KEY (parameter_id) REFERENCES public.model_parameters(id) ON DELETE CASCADE;


--
-- TOC entry 4855 (class 2606 OID 26508)
-- Name: user_model_parameter user_model_parameter_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.user_model_parameter
    ADD CONSTRAINT user_model_parameter_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- TOC entry 4856 (class 2606 OID 26513)
-- Name: verification_tokens verification_tokens_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.verification_tokens
    ADD CONSTRAINT verification_tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


-- Completed on 2025-06-14 16:08:43

--
-- PostgreSQL database dump complete
--

