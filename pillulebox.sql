-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 04-11-2024 a las 05:38:54
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
  `mac` varchar(255) DEFAULT NULL,
  `medicine_name` varchar(30) DEFAULT NULL,
  `morning_start_time` time DEFAULT NULL,
  `morning_end_time` time DEFAULT NULL,
  `afternoon_start_time` time DEFAULT NULL,
  `afternoon_end_time` time DEFAULT NULL,
  `night_start_time` time DEFAULT NULL,
  `night_end_time` time DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cell`
--

CREATE TABLE `cell` (
  `id` int(11) NOT NULL,
  `mac_dispenser` char(17) DEFAULT NULL,
  `num_cell` tinyint(4) DEFAULT NULL,
  `current_medicine_date` datetime DEFAULT NULL,
  `single_mode_id` int(11) DEFAULT NULL,
  `sequential_mode_id` int(11) DEFAULT NULL,
  `basic_mode_id` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `cell`
--

INSERT INTO `cell` (`id`, `mac_dispenser`, `num_cell`, `current_medicine_date`, `single_mode_id`, `sequential_mode_id`, `basic_mode_id`) VALUES
(22, '11:11:11:11:11:11', 1, NULL, NULL, NULL, NULL),
(23, '11:11:11:11:11:11', 2, NULL, NULL, NULL, NULL),
(24, '11:11:11:11:11:11', 3, NULL, NULL, NULL, NULL),
(25, '11:11:11:11:11:11', 4, NULL, NULL, NULL, NULL),
(26, '11:11:11:11:11:11', 5, NULL, NULL, NULL, NULL),
(27, '11:11:11:11:11:11', 6, NULL, NULL, NULL, NULL),
(28, '11:11:11:11:11:11', 7, NULL, NULL, NULL, NULL),
(29, '11:11:11:11:11:11', 8, NULL, NULL, NULL, NULL),
(30, '11:11:11:11:11:11', 9, NULL, NULL, NULL, NULL),
(31, '11:11:11:11:11:11', 10, NULL, NULL, NULL, NULL),
(32, '11:11:11:11:11:11', 11, NULL, NULL, NULL, NULL),
(33, '11:11:11:11:11:11', 12, NULL, NULL, NULL, NULL),
(34, '11:11:11:11:11:11', 13, NULL, NULL, NULL, NULL),
(35, '11:11:11:11:11:11', 14, NULL, NULL, NULL, NULL);

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
  `mac` char(17) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `dispenser_name` varchar(30) DEFAULT 'Unnamed',
  `context` tinyint(4) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `dispenser`
--

INSERT INTO `dispenser` (`mac`, `user_id`, `dispenser_name`, `context`) VALUES
('11:11:11:11:11:11', NULL, 'Unnamed', 0),
('30:30:F9:72:22:8C', 14, 'Casa 3', 2),
('A1:B2:C3:D4:E5:F6', 14, 'Casa 1', 2),
('A2:B3:C4:D5:E6:F7', 13, 'Casa 1', 2),
('A3:B4:C5:D6:E7:F8', 13, 'Unnamed', 0),
('A4:B5:C6:D7:E8:F9', 18, 'Unnamed', 0),
('B2:C3:D4:E5:F6:A1', 14, 'Unnamed', 0),
('B3:C4:D5:E6:F7:A2', 14, 'Casa 2', 1),
('B4:C5:D6:E7:F8:A3', 18, 'Unnamed', 0),
('B5:C6:D7:E8:F9:A4', 13, 'Unnamed', 0),
('C3:D4:E5:F6:A1:B2', 13, 'Unnamed', 0),
('C4:D5:E6:F7:A2:B3', 14, 'Unnamed', 2),
('C5:D6:E7:F8:A3:B4', 18, 'Casa 2', 0),
('D4:E5:F6:A1:B2:C3', 18, 'Unnamed', 0),
('D5:E6:F7:A2:B3:C4', 14, 'Unnamed', 0),
('D6:E7:F8:A3:B4:C5', 14, 'Unnamed', 1),
('E5:F6:A1:B2:C3:D4', 13, 'Unnamed', 0),
('E6:F7:A2:B3:C4:D5', 13, 'Unnamed', 0),
('E7:F8:A3:B4:C5:D6', 13, 'Unnamed', 0),
('F6:A1:B2:C3:D4:E5', 13, 'Unnamed', 0),
('F7:A2:B3:C4:D5:E6', NULL, 'Unnamed', 0),
('F8:A3:B4:C5:D6:E7', NULL, 'Unnamed', 0);

--
-- Disparadores `dispenser`
--
DELIMITER $$
CREATE TRIGGER `after_dispenser_insert` AFTER INSERT ON `dispenser` FOR EACH ROW BEGIN
    DECLARE i INT DEFAULT 1;
    
    WHILE i <= 14 DO
        INSERT INTO cell (mac_dispenser, num_cell) VALUES (NEW.mac, i);
        SET i = i + 1;
    END WHILE;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `history`
--

CREATE TABLE `history` (
  `id` int(11) NOT NULL,
  `cell_id` int(11) DEFAULT NULL,
  `consumption_status` tinyint(1) DEFAULT NULL,
  `date_consumption` datetime DEFAULT NULL,
  `reason` varchar(128) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sequential_mode`
--

CREATE TABLE `sequential_mode` (
  `id` int(11) NOT NULL,
  `mac` varchar(255) DEFAULT NULL,
  `medicine_name` varchar(30) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
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
  `mac` varchar(255) DEFAULT NULL,
  `medicine_name` varchar(30) DEFAULT NULL,
  `dispensing_date` datetime DEFAULT NULL
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
(41, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzAwOTI0ODQsImV4cCI6MTczMDY5NzI4NH0.BmE7uFb5rbMg_ln7cJ9kMBM8iha084oqnrD7eklSfjE', 1730697284),
(42, 18, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik1tbW0iLCJwYXNzd29yZCI6InN5MDZaRXFka1VlSUxKMVR3WHdkNUE9PSIsImlhdCI6MTczMDA5MzkyNywiZXhwIjoxNzMwNjk4NzI3fQ.90bmTiTA-otL84QO9n8nQBWT2gj4t9QzRl_ahySzpIg', 1730698727),
(43, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzAxMzU2NzUsImV4cCI6MTczMDc0MDQ3NX0.XgptBqk1iTZQirzjcOgToQcuoiWRKegXYzlEC_EWvHg', 1730740475),
(44, 18, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik1tbW0iLCJwYXNzd29yZCI6InN5MDZaRXFka1VlSUxKMVR3WHdkNUE9PSIsImlhdCI6MTczMDEzOTU0MSwiZXhwIjoxNzMwNzQ0MzQxfQ.OYGoFU8iU7tXCRY2lM1BhjwnXwXb0tfga6Amf-duwEU', 1730744341),
(45, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzAxNDYzNTgsImV4cCI6MTczMDc1MTE1OH0.WffV4aUxfnBU9aPuA8ENv0gQLFwLyrIxHbhGWQiRlUM', 1730751158),
(46, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzA1MzU5MTcsImV4cCI6MTczMTE0MDcxN30.LNZ2c6tdKpEx9aWtZComupesVWo_yPA1PZOj_Lk6yUQ', 1731140717),
(47, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzA2MDc2MDEsImV4cCI6MTczMTIxMjQwMX0.vam4D4QLyD752FajI-X0QLlO-p1TMYLcC4y-w-VjryA', 1731212401),
(48, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzA2MDc2MTQsImV4cCI6MTczMTIxMjQxNH0.B9o22hnFvd0EYnD1Lg4hQ1ulvy6FxkSG0CcsDEtf4_8', 1731212414),
(49, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzA2MDg0MDAsImV4cCI6MTczMTIxMzIwMH0.bkbs54YDF24u0ppllNAiCSOKf5cyB_uLZnJqtc5OJio', 1731213200),
(50, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzA2MDg3MjQsImV4cCI6MTczMTIxMzUyNH0.YZv96x2-H3X5oDJMHNKEzes372_iaZtfVW7EiBDpQ5I', 1731213524),
(51, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzA2MDkxMDksImV4cCI6MTczMTIxMzkwOX0.LyuiDzgMaQhUZZ-hAmsPf4gn9JTs2WwzL9P_oYLmyqM', 1731213909),
(56, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzA2OTAyMzQsImV4cCI6MTczMTI5NTAzNH0.YZ-wGPZS3abTzXb2lMca0raf76syC03pmcVgPFwcCtw', 1731295034),
(58, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3MzA2OTQ0MTcsImV4cCI6MTczMTI5OTIxN30.sCCu6tqvJjhQ4bDVq9mtZL44HOiHLR8xVRd9zKMwLRU', 1731299217);

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
(13, 'Mario1', 'jqG3VVLew/ni7g6nSAEWAQ==', 'mariobpb27@gmail.com'),
(14, 'M2', 'XvG8TMNcG8N7ekZwbjcYyQ==', 'marioxd2708@gmail.com'),
(18, 'Mmmm', 'sy06ZEqdkUeILJ1TwXwd5A==', 'a20100335@ceti.mx');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `basic_mode`
--
ALTER TABLE `basic_mode`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mac` (`mac`);

--
-- Indices de la tabla `cell`
--
ALTER TABLE `cell`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mac_dispenser` (`mac_dispenser`),
  ADD KEY `fk_single_mode` (`single_mode_id`),
  ADD KEY `fk_sequential_mode` (`sequential_mode_id`),
  ADD KEY `fk_basic_mode` (`basic_mode_id`);

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
  ADD KEY `cell_id` (`cell_id`);

--
-- Indices de la tabla `sequential_mode`
--
ALTER TABLE `sequential_mode`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mac` (`mac`);

--
-- Indices de la tabla `single_mode`
--
ALTER TABLE `single_mode`
  ADD PRIMARY KEY (`id`),
  ADD KEY `mac` (`mac`);

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
-- AUTO_INCREMENT de la tabla `basic_mode`
--
ALTER TABLE `basic_mode`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `cell`
--
ALTER TABLE `cell`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;

--
-- AUTO_INCREMENT de la tabla `codes`
--
ALTER TABLE `codes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=63;

--
-- AUTO_INCREMENT de la tabla `history`
--
ALTER TABLE `history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `sequential_mode`
--
ALTER TABLE `sequential_mode`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `single_mode`
--
ALTER TABLE `single_mode`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `tokens`
--
ALTER TABLE `tokens`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;

--
-- AUTO_INCREMENT de la tabla `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `basic_mode`
--
ALTER TABLE `basic_mode`
  ADD CONSTRAINT `basic_mode_ibfk_1` FOREIGN KEY (`mac`) REFERENCES `dispenser` (`mac`) ON DELETE CASCADE;

--
-- Filtros para la tabla `cell`
--
ALTER TABLE `cell`
  ADD CONSTRAINT `fk_basic_mode` FOREIGN KEY (`basic_mode_id`) REFERENCES `basic_mode` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_sequential_mode` FOREIGN KEY (`sequential_mode_id`) REFERENCES `sequential_mode` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_single_mode` FOREIGN KEY (`single_mode_id`) REFERENCES `single_mode` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `mac_dispenser` FOREIGN KEY (`mac_dispenser`) REFERENCES `dispenser` (`mac`) ON DELETE CASCADE;

--
-- Filtros para la tabla `dispenser`
--
ALTER TABLE `dispenser`
  ADD CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

--
-- Filtros para la tabla `history`
--
ALTER TABLE `history`
  ADD CONSTRAINT `history_ibfk_1` FOREIGN KEY (`cell_id`) REFERENCES `cell` (`id`);

--
-- Filtros para la tabla `sequential_mode`
--
ALTER TABLE `sequential_mode`
  ADD CONSTRAINT `sequential_mode_ibfk_1` FOREIGN KEY (`mac`) REFERENCES `dispenser` (`mac`) ON DELETE CASCADE;

--
-- Filtros para la tabla `single_mode`
--
ALTER TABLE `single_mode`
  ADD CONSTRAINT `single_mode_ibfk_1` FOREIGN KEY (`mac`) REFERENCES `dispenser` (`mac`) ON DELETE CASCADE;

--
-- Filtros para la tabla `tokens`
--
ALTER TABLE `tokens`
  ADD CONSTRAINT `tokens_ibfk_1` FOREIGN KEY (`user`) REFERENCES `user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
