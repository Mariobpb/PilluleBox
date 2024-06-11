-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 11-06-2024 a las 15:31:04
-- Versión del servidor: 10.4.24-MariaDB
-- Versión de PHP: 8.1.6

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cell`
--

CREATE TABLE `cell` (
  `id` int(11) NOT NULL,
  `mac_dispenser` char(12) DEFAULT NULL,
  `num_cell` tinyint(4) DEFAULT NULL,
  `status` enum('vacio','disponible','advertencia','requerido') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `dispenser`
--

CREATE TABLE `dispenser` (
  `mac` char(12) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `dispenser_name` varchar(30) DEFAULT NULL,
  `context` tinyint(4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `user`
--

CREATE TABLE `user` (
  `id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `user`
--

INSERT INTO `user` (`id`, `username`, `password`, `email`) VALUES
(1, 'Mariobpb', 'passsssssword', 'mariobpb27@gmail.com'),
(4, 'Mario', 'Contra123$', 'mariobpb@gmail.com');

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
-- Indices de la tabla `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

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
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
