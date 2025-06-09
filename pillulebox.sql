-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 09-06-2025 a las 14:07:10
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
(344, 'F0:9E:9E:22:6E:80', 1, NULL, NULL, NULL, NULL),
(345, 'F0:9E:9E:22:6E:80', 2, NULL, NULL, NULL, NULL),
(346, 'F0:9E:9E:22:6E:80', 3, NULL, NULL, NULL, NULL),
(347, 'F0:9E:9E:22:6E:80', 4, NULL, NULL, NULL, NULL),
(348, 'F0:9E:9E:22:6E:80', 5, NULL, NULL, NULL, NULL),
(349, 'F0:9E:9E:22:6E:80', 6, NULL, NULL, NULL, NULL),
(350, 'F0:9E:9E:22:6E:80', 7, NULL, NULL, NULL, NULL),
(351, 'F0:9E:9E:22:6E:80', 8, NULL, NULL, NULL, NULL),
(352, 'F0:9E:9E:22:6E:80', 9, NULL, NULL, NULL, NULL),
(353, 'F0:9E:9E:22:6E:80', 10, NULL, NULL, NULL, NULL),
(354, 'F0:9E:9E:22:6E:80', 11, NULL, NULL, NULL, NULL),
(355, 'F0:9E:9E:22:6E:80', 12, NULL, NULL, NULL, NULL),
(356, 'F0:9E:9E:22:6E:80', 13, NULL, NULL, 15, NULL),
(357, 'F0:9E:9E:22:6E:80', 14, NULL, NULL, NULL, NULL);

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
('F0:9E:9E:22:6E:80', 19, 'Ceti Dispenser', 3);

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
  `mac_dispenser` char(17) DEFAULT NULL,
  `medicine_name` varchar(30) DEFAULT NULL,
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

--
-- Volcado de datos para la tabla `sequential_mode`
--

INSERT INTO `sequential_mode` (`id`, `mac`, `medicine_name`, `start_date`, `end_date`, `period`, `limit_times_consumption`, `affected_periods`, `current_times_consumption`) VALUES
(15, 'F0:9E:9E:22:6E:80', 'AF', '2025-06-09 07:01:21', '2025-06-09 07:01:21', '07:01:00', 6, 0, 0),
(16, 'F0:9E:9E:22:6E:80', 'GGV', '2025-06-13 07:01:41', '2025-06-26 07:01:41', '05:01:00', 3, 0, 0);

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
(101, 19, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik1hcmlvYnBiIiwicGFzc3dvcmQiOiJJQkY5ZXd2empneUR0NlFvakYyUDhRPT0iLCJpYXQiOjE3NDk0NTIxMzgsImV4cCI6MTc1MDA1NjkzOH0.PsQu1lepOezxJlSGh5vWzhemm2ag-whTJkd9kWaGXhQ', 1750056938),
(102, 19, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik1hcmlvYnBiIiwicGFzc3dvcmQiOiJJQkY5ZXd2empneUR0NlFvakYyUDhRPT0iLCJpYXQiOjE3NDk0NTI0MTMsImV4cCI6MTc1MDA1NzIxM30.IegKErFHptbUP9DjvtFZF-kGMHcxJnI4kEpFfgxz9V8', 1750057213);

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
(19, 'Mariobpb', 'IBF9ewvzjgyDt6QojF2P8Q==', 'mariobpb27@gmail.com');

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
  ADD KEY `fk_mac_dispenser` (`mac_dispenser`);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `cell`
--
ALTER TABLE `cell`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=358;

--
-- AUTO_INCREMENT de la tabla `codes`
--
ALTER TABLE `codes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=65;

--
-- AUTO_INCREMENT de la tabla `history`
--
ALTER TABLE `history`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=43;

--
-- AUTO_INCREMENT de la tabla `sequential_mode`
--
ALTER TABLE `sequential_mode`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `single_mode`
--
ALTER TABLE `single_mode`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `tokens`
--
ALTER TABLE `tokens`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=103;

--
-- AUTO_INCREMENT de la tabla `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

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
  ADD CONSTRAINT `fk_mac_dispenser` FOREIGN KEY (`mac_dispenser`) REFERENCES `dispenser` (`mac`);

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
