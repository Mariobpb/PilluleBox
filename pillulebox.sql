-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 03-10-2024 a las 08:06:51
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `pillulebox`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `basic_mode`
--

CREATE TABLE `basic_mode` (
  `id` int(11) NOT NULL,
  `cell_id` int(11) DEFAULT NULL,
  `med_name` varchar(30) DEFAULT NULL,
  `morn_range` tinyint(4) DEFAULT NULL,
  `aftn_range` tinyint(4) DEFAULT NULL,
  `night_range` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cell`
--

CREATE TABLE `cell` (
  `id` int(11) NOT NULL,
  `mac_dispenser` char(12) DEFAULT NULL,
  `num_cell` tinyint(4) DEFAULT NULL,
  `status` enum('vacio','disponible','advertencia','requerido') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `codes`
--

CREATE TABLE `codes` (
  `id` int(11) NOT NULL,
  `code` char(5) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `creation_date` char(23) DEFAULT NULL,
  `expiration_date` datetime(3) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `dispenser`
--

CREATE TABLE `dispenser` (
  `mac` char(12) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `dispenser_name` varchar(30) DEFAULT NULL,
  `context` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `history`
--

CREATE TABLE `history` (
  `id` int(11) NOT NULL,
  `single_mode_id` int(11) DEFAULT NULL,
  `sequential_mode_id` int(11) DEFAULT NULL,
  `basic_mode_id` int(11) DEFAULT NULL,
  `consumption_status` tinyint(1) DEFAULT NULL,
  `date_consumption` date DEFAULT NULL,
  `time_consumption` time DEFAULT NULL,
  `reason` varchar(128) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sequential_mode`
--

CREATE TABLE `sequential_mode` (
  `id` int(11) NOT NULL,
  `cell_id` int(11) DEFAULT NULL,
  `med_name` varchar(30) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `start_time` time DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `end_time` time DEFAULT NULL,
  `period` time DEFAULT NULL,
  `limit_times_consumption` tinyint(4) DEFAULT NULL,
  `affected_periods` tinyint(1) DEFAULT NULL,
  `current_times_consumption` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `single_mode`
--

CREATE TABLE `single_mode` (
  `id` int(11) NOT NULL,
  `cell_id` int(11) DEFAULT NULL,
  `med_name` varchar(30) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `time` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tokens`
--

CREATE TABLE `tokens` (
  `id` int(11) NOT NULL,
  `user` int(11) DEFAULT NULL,
  `token` text DEFAULT NULL,
  `token_exp` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tokens`
--

INSERT INTO `tokens` (`id`, `user`, `token`, `token_exp`) VALUES
(1, 12, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik1hcmlvMiIsInBhc3N3b3JkIjoiQ0drKzlDYzlUMDhqSDdyQ3QyUFlnQT09IiwiaWF0IjoxNzE5MTk0ODk0LCJleHAiOjE3MTkxOTQ5NTR9.rfOlTBN8pL5OlIstr-EsmTWkNN0rWaf8QY-phpqyyiA', 1719194954),
(2, 12, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik1hcmlvMiIsInBhc3N3b3JkIjoiQ0drKzlDYzlUMDhqSDdyQ3QyUFlnQT09IiwiaWF0IjoxNzE5MTk0OTg3LCJleHAiOjE3MTkxOTUwNDd9.32M7n7ELHZx8NdXYgKZAJp8wi6YLNuj3hPC7-aRt4FI', 1719195047),
(3, 12, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik1hcmlvMiIsInBhc3N3b3JkIjoiQ0drKzlDYzlUMDhqSDdyQ3QyUFlnQT09IiwiaWF0IjoxNzI3ODA2OTc0LCJleHAiOjE3Mjc4MDcwMzR9.Ul6xAZl1e1EGrWtf3fgamz9wqFlXb64Uq6xoy2191-0', 1727807034),
(4, 12, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik1hcmlvMiIsInBhc3N3b3JkIjoiQ0drKzlDYzlUMDhqSDdyQ3QyUFlnQT09IiwiaWF0IjoxNzI3ODI0NjAwLCJleHAiOjE3Mjc4MjQ2NjB9.iqkAjsUi8xhczLpL_7Q2fARzhUnIxGhG7bFfiJdkoxk', 1727824660),
(5, 13, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik1hcmlvMSIsInBhc3N3b3JkIjoianFHM1ZWTGV3L25pN2c2blNBRVdBUT09IiwiaWF0IjoxNzI3ODg5MzE2LCJleHAiOjE3Mjc4ODkzNzZ9.eqy7BEFvEQxsxOxw5WrMhiQ-JpE-0cfnRJfl_va14Hw', 1727889376),
(6, 13, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Im1hcmlvYnBiMjdAZ21haWwuY29tIiwicGFzc3dvcmQiOiJqcUczVlZMZXcvbmk3ZzZuU0FFV0FRPT0iLCJpYXQiOjE3Mjc4ODkzNjMsImV4cCI6MTcyNzg4OTQyM30.iNOETvfCQJ7vYsvA_ZTnSJmHdV6yc4CXAUcaqOnoJSc', 1727889423),
(7, 13, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik1hcmlvMSIsInBhc3N3b3JkIjoianFHM1ZWTGV3L25pN2c2blNBRVdBUT09IiwiaWF0IjoxNzI3ODg5NTAzLCJleHAiOjE3Mjc4ODk1NjN9.fBHGV9tB6evPwDaSGA7VWJlXxwqSS7ygzwOQd6VmhjU', 1727889563),
(8, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc4ODk2NjIsImV4cCI6MTcyNzg4OTcyMn0.-dDOOjgqsadAML7-Mu07ouUAIl8YB50rNQdkxChZgfI', 1727889722),
(9, 13, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Im1hcmlvYnBiMjdAZ21haWwuY29tIiwicGFzc3dvcmQiOiJqcUczVlZMZXcvbmk3ZzZuU0FFV0FRPT0iLCJpYXQiOjE3Mjc4ODk3NjEsImV4cCI6MTcyNzg4OTgyMX0.3DfefqVkJTcrMvfyGM7t7sIA30Zfx0Rv2vIzWFA9LHo', 1727889821),
(10, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc5Mjc2ODIsImV4cCI6MTcyNzkyNzc0Mn0.lBvH6mEWeao_vbUO0ngqItVEGJ6_r_EScc6m6je-srI', 1727927742),
(11, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc5Mjc4NTgsImV4cCI6MTcyNzkyODQ1OH0.j4bJ9f96uKB3eG5d0sqKRz4Psz9DPns1gsU-yXUiCrM', 1727928458),
(12, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc5MjgyNzUsImV4cCI6MTcyNzkyODg3NX0.7dTh-rRYM_ANy9FXJe_NOSmsbLVeT-OifYGLmH0WbjE', 1727928875),
(13, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc5Mjg0MjEsImV4cCI6MTcyNzkyODU0MX0.OmSpD-meF9LQmyF0lwVhSd4ODu7bWUU97R5lWalMFa4', 1727928541),
(14, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc5MzM0NjIsImV4cCI6MTcyNzkzMzU4Mn0.1i5Ct2keYLINTYvlighkerErHwPLSWVsOwwihg5f2cI', 1727933582),
(15, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc5MzQzMzIsImV4cCI6MTcyNzkzNDQ1Mn0.5WjdlGk_LMZbKfiZqBv8S2czfCnQetHTjwhdTXf5PEw', 1727934452),
(16, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc5MzQ5NjUsImV4cCI6MTcyNzkzNTA4NX0.C-Qxu5wFe9luBWdMNeblSzTo1bFn939mvJ1eOwfOnyQ', 1727935085),
(17, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc5MzUyNTMsImV4cCI6MTcyOTE0NDg1M30.aBMpPGEHmc4pTvUUUpppYpSugjFrq5BCBKZ-iPxehWM', 1729144853),
(18, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc5MzU0NjYsImV4cCI6MTcyOTE0NTA2Nn0.Qpo73t5mQjZQ3v_ZcxLHFTZz6AYkRd9mKe9XNpUZj3g', 1729145066),
(19, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3Mjc5MzU1MDMsImV4cCI6MTcyOTE0NTEwM30.O5pS-tbCtog1eKXgFWwlbpkXYld5CYLmn7L7uEJXkx8', 1729145103);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `user`
--

INSERT INTO `user` (`id`, `username`, `password`, `email`) VALUES
(1, 'Usuario2', '2mWaW52IW2RPNgFOG4lSAnsvZWTcB9PKn1lDhGL8BbddqPWiFk0Baz99SjaW9NfvGFkzhc1+TNih\n20k/X9wyzQ==\n', 'micorreo1234@ceti.mx'),
(3, 'User123', 'ese6yW0mS+ZFjC5hO6Fqzw==\n', 'correo'),
(12, 'Mario2', 'CGk+9Cc9T08jH7rCt2PYgA==', 'a20100335@ceti.mx'),
(13, 'Mario1', 'jqG3VVLew/ni7g6nSAEWAQ==', 'mariobpb27@gmail.com'),
(14, 'M2', 'XvG8TMNcG8N7ekZwbjcYyQ==', 'marioxd2708@gmail.com');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `basic_mode`
--
ALTER TABLE `basic_mode`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cell_id` (`cell_id`);

--
-- Indices de la tabla `cell`
--
ALTER TABLE `cell`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mac_dispenser` (`mac_dispenser`);

--
-- Indices de la tabla `codes`
--
ALTER TABLE `codes`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `dispenser`
--
ALTER TABLE `dispenser`
  ADD PRIMARY KEY (`mac`),
  ADD KEY `user_id` (`user_id`);

--
-- Indices de la tabla `history`
--
ALTER TABLE `history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `single_mode_id` (`single_mode_id`),
  ADD KEY `sequential_mode_id` (`sequential_mode_id`),
  ADD KEY `basic_mode_id` (`basic_mode_id`);

--
-- Indices de la tabla `sequential_mode`
--
ALTER TABLE `sequential_mode`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cell_id` (`cell_id`);

--
-- Indices de la tabla `single_mode`
--
ALTER TABLE `single_mode`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cell_id` (`cell_id`);

--
-- Indices de la tabla `tokens`
--
ALTER TABLE `tokens`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user` (`user`);

--
-- Indices de la tabla `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `codes`
--
ALTER TABLE `codes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT de la tabla `tokens`
--
ALTER TABLE `tokens`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT de la tabla `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `basic_mode`
--
ALTER TABLE `basic_mode`
  ADD CONSTRAINT `basic_mode_ibfk_1` FOREIGN KEY (`cell_id`) REFERENCES `cell` (`id`);

--
-- Filtros para la tabla `cell`
--
ALTER TABLE `cell`
  ADD CONSTRAINT `mac_dispenser` FOREIGN KEY (`mac_dispenser`) REFERENCES `dispenser` (`mac`);

--
-- Filtros para la tabla `dispenser`
--
ALTER TABLE `dispenser`
  ADD CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Filtros para la tabla `history`
--
ALTER TABLE `history`
  ADD CONSTRAINT `history_ibfk_1` FOREIGN KEY (`single_mode_id`) REFERENCES `single_mode` (`id`),
  ADD CONSTRAINT `history_ibfk_2` FOREIGN KEY (`sequential_mode_id`) REFERENCES `sequential_mode` (`id`),
  ADD CONSTRAINT `history_ibfk_3` FOREIGN KEY (`basic_mode_id`) REFERENCES `basic_mode` (`id`);

--
-- Filtros para la tabla `sequential_mode`
--
ALTER TABLE `sequential_mode`
  ADD CONSTRAINT `sequential_mode_ibfk_1` FOREIGN KEY (`cell_id`) REFERENCES `cell` (`id`);

--
-- Filtros para la tabla `single_mode`
--
ALTER TABLE `single_mode`
  ADD CONSTRAINT `single_mode_ibfk_1` FOREIGN KEY (`cell_id`) REFERENCES `cell` (`id`);

--
-- Filtros para la tabla `tokens`
--
ALTER TABLE `tokens`
  ADD CONSTRAINT `tokens_ibfk_1` FOREIGN KEY (`user`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
