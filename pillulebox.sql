-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 25-05-2025 a las 06:38:23
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

--
-- Volcado de datos para la tabla `basic_mode`
--

INSERT INTO `basic_mode` (`id`, `mac`, `medicine_name`, `morning_start_time`, `morning_end_time`, `afternoon_start_time`, `afternoon_end_time`, `night_start_time`, `night_end_time`) VALUES
(6, '30:30:F9:72:22:8C', 'Paracetamol', '06:00:00', '11:00:00', '13:20:00', '15:15:00', '20:00:00', '21:00:00'),
(7, '30:30:F9:72:22:8C', 'Ibuprofeno', '07:30:00', '08:30:00', '12:30:00', '13:30:00', '19:30:00', '20:30:00'),
(8, '30:30:F9:72:22:8C', 'Amoxicilina', '09:00:00', '10:00:00', '14:00:00', '15:00:00', '21:00:00', '22:00:00'),
(9, '30:30:F9:72:22:8C', 'Vitamina C', '06:30:00', '07:30:00', '11:30:00', '12:30:00', '18:30:00', '19:30:00'),
(12, 'F0:9E:9E:22:6E:80', 'Prueba 1 Básico', '04:00:00', '11:59:00', '12:00:00', '19:59:00', '20:00:00', '03:59:00'),
(13, 'F0:9E:9E:22:6E:80', 'Prueba 2 Básico', '05:28:00', '06:57:00', '15:45:00', '16:10:00', '21:02:00', '00:00:00');

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
(50, 'A1:B2:C3:D4:E5:F6', 1, NULL, NULL, NULL, NULL),
(51, 'A1:B2:C3:D4:E5:F6', 2, NULL, NULL, NULL, NULL),
(52, 'A1:B2:C3:D4:E5:F6', 3, NULL, NULL, NULL, NULL),
(53, 'A1:B2:C3:D4:E5:F6', 4, NULL, NULL, NULL, NULL),
(54, 'A1:B2:C3:D4:E5:F6', 5, NULL, NULL, NULL, NULL),
(55, 'A1:B2:C3:D4:E5:F6', 6, NULL, NULL, NULL, NULL),
(56, 'A1:B2:C3:D4:E5:F6', 7, NULL, NULL, NULL, NULL),
(57, 'A1:B2:C3:D4:E5:F6', 8, NULL, NULL, NULL, NULL),
(58, 'A1:B2:C3:D4:E5:F6', 9, NULL, NULL, NULL, NULL),
(59, 'A1:B2:C3:D4:E5:F6', 10, NULL, NULL, NULL, NULL),
(60, 'A1:B2:C3:D4:E5:F6', 11, NULL, NULL, NULL, NULL),
(61, 'A1:B2:C3:D4:E5:F6', 12, NULL, NULL, NULL, NULL),
(62, 'A1:B2:C3:D4:E5:F6', 13, NULL, NULL, NULL, NULL),
(63, 'A1:B2:C3:D4:E5:F6', 14, NULL, NULL, NULL, NULL),
(64, 'B1:C2:D3:E4:F5:A6', 1, NULL, NULL, NULL, NULL),
(65, 'B1:C2:D3:E4:F5:A6', 2, NULL, NULL, NULL, NULL),
(66, 'B1:C2:D3:E4:F5:A6', 3, NULL, NULL, NULL, NULL),
(67, 'B1:C2:D3:E4:F5:A6', 4, NULL, NULL, NULL, NULL),
(68, 'B1:C2:D3:E4:F5:A6', 5, NULL, NULL, NULL, NULL),
(69, 'B1:C2:D3:E4:F5:A6', 6, NULL, NULL, NULL, NULL),
(70, 'B1:C2:D3:E4:F5:A6', 7, NULL, NULL, NULL, NULL),
(71, 'B1:C2:D3:E4:F5:A6', 8, NULL, NULL, NULL, NULL),
(72, 'B1:C2:D3:E4:F5:A6', 9, NULL, NULL, NULL, NULL),
(73, 'B1:C2:D3:E4:F5:A6', 10, NULL, NULL, NULL, NULL),
(74, 'B1:C2:D3:E4:F5:A6', 11, NULL, NULL, NULL, NULL),
(75, 'B1:C2:D3:E4:F5:A6', 12, NULL, NULL, NULL, NULL),
(76, 'B1:C2:D3:E4:F5:A6', 13, NULL, NULL, NULL, NULL),
(77, 'B1:C2:D3:E4:F5:A6', 14, NULL, NULL, NULL, NULL),
(78, 'C1:D2:E3:F4:A5:B6', 1, NULL, NULL, NULL, NULL),
(79, 'C1:D2:E3:F4:A5:B6', 2, NULL, NULL, NULL, NULL),
(80, 'C1:D2:E3:F4:A5:B6', 3, NULL, NULL, NULL, NULL),
(81, 'C1:D2:E3:F4:A5:B6', 4, NULL, NULL, NULL, NULL),
(82, 'C1:D2:E3:F4:A5:B6', 5, NULL, NULL, NULL, NULL),
(83, 'C1:D2:E3:F4:A5:B6', 6, NULL, NULL, NULL, NULL),
(84, 'C1:D2:E3:F4:A5:B6', 7, NULL, NULL, NULL, NULL),
(85, 'C1:D2:E3:F4:A5:B6', 8, NULL, NULL, NULL, NULL),
(86, 'C1:D2:E3:F4:A5:B6', 9, NULL, NULL, NULL, NULL),
(87, 'C1:D2:E3:F4:A5:B6', 10, NULL, NULL, NULL, NULL),
(88, 'C1:D2:E3:F4:A5:B6', 11, NULL, NULL, NULL, NULL),
(89, 'C1:D2:E3:F4:A5:B6', 12, NULL, NULL, NULL, NULL),
(90, 'C1:D2:E3:F4:A5:B6', 13, NULL, NULL, NULL, NULL),
(91, 'C1:D2:E3:F4:A5:B6', 14, NULL, NULL, NULL, NULL),
(92, 'D1:E2:F3:A4:B5:C6', 1, NULL, NULL, NULL, NULL),
(93, 'D1:E2:F3:A4:B5:C6', 2, NULL, NULL, NULL, NULL),
(94, 'D1:E2:F3:A4:B5:C6', 3, NULL, NULL, NULL, NULL),
(95, 'D1:E2:F3:A4:B5:C6', 4, NULL, NULL, NULL, NULL),
(96, 'D1:E2:F3:A4:B5:C6', 5, NULL, NULL, NULL, NULL),
(97, 'D1:E2:F3:A4:B5:C6', 6, NULL, NULL, NULL, NULL),
(98, 'D1:E2:F3:A4:B5:C6', 7, NULL, NULL, NULL, NULL),
(99, 'D1:E2:F3:A4:B5:C6', 8, NULL, NULL, NULL, NULL),
(100, 'D1:E2:F3:A4:B5:C6', 9, NULL, NULL, NULL, NULL),
(101, 'D1:E2:F3:A4:B5:C6', 10, NULL, NULL, NULL, NULL),
(102, 'D1:E2:F3:A4:B5:C6', 11, NULL, NULL, NULL, NULL),
(103, 'D1:E2:F3:A4:B5:C6', 12, NULL, NULL, NULL, NULL),
(104, 'D1:E2:F3:A4:B5:C6', 13, NULL, NULL, NULL, NULL),
(105, 'D1:E2:F3:A4:B5:C6', 14, NULL, NULL, NULL, NULL),
(106, 'E1:F2:A3:B4:C5:D6', 1, NULL, NULL, NULL, NULL),
(107, 'E1:F2:A3:B4:C5:D6', 2, NULL, NULL, NULL, NULL),
(108, 'E1:F2:A3:B4:C5:D6', 3, NULL, NULL, NULL, NULL),
(109, 'E1:F2:A3:B4:C5:D6', 4, NULL, NULL, NULL, NULL),
(110, 'E1:F2:A3:B4:C5:D6', 5, NULL, NULL, NULL, NULL),
(111, 'E1:F2:A3:B4:C5:D6', 6, NULL, NULL, NULL, NULL),
(112, 'E1:F2:A3:B4:C5:D6', 7, NULL, NULL, NULL, NULL),
(113, 'E1:F2:A3:B4:C5:D6', 8, NULL, NULL, NULL, NULL),
(114, 'E1:F2:A3:B4:C5:D6', 9, NULL, NULL, NULL, NULL),
(115, 'E1:F2:A3:B4:C5:D6', 10, NULL, NULL, NULL, NULL),
(116, 'E1:F2:A3:B4:C5:D6', 11, NULL, NULL, NULL, NULL),
(117, 'E1:F2:A3:B4:C5:D6', 12, NULL, NULL, NULL, NULL),
(118, 'E1:F2:A3:B4:C5:D6', 13, NULL, NULL, NULL, NULL),
(119, 'E1:F2:A3:B4:C5:D6', 14, NULL, NULL, NULL, NULL),
(120, '30:30:F9:72:22:8C', 1, NULL, NULL, NULL, NULL),
(121, '30:30:F9:72:22:8C', 2, NULL, NULL, NULL, NULL),
(122, '30:30:F9:72:22:8C', 3, NULL, NULL, NULL, NULL),
(123, '30:30:F9:72:22:8C', 4, NULL, NULL, NULL, NULL),
(124, '30:30:F9:72:22:8C', 5, NULL, NULL, NULL, NULL),
(125, '30:30:F9:72:22:8C', 6, NULL, NULL, NULL, NULL),
(126, '30:30:F9:72:22:8C', 7, NULL, NULL, NULL, NULL),
(127, '30:30:F9:72:22:8C', 8, NULL, NULL, NULL, NULL),
(128, '30:30:F9:72:22:8C', 9, NULL, NULL, NULL, NULL),
(129, '30:30:F9:72:22:8C', 10, NULL, NULL, NULL, NULL),
(130, '30:30:F9:72:22:8C', 11, NULL, NULL, NULL, NULL),
(131, '30:30:F9:72:22:8C', 12, NULL, NULL, NULL, NULL),
(132, '30:30:F9:72:22:8C', 13, NULL, NULL, NULL, NULL),
(133, '30:30:F9:72:22:8C', 14, NULL, NULL, NULL, NULL),
(134, 'A2:B3:C4:D5:E6:F1', 1, NULL, NULL, NULL, NULL),
(135, 'A2:B3:C4:D5:E6:F1', 2, NULL, NULL, NULL, NULL),
(136, 'A2:B3:C4:D5:E6:F1', 3, NULL, NULL, NULL, NULL),
(137, 'A2:B3:C4:D5:E6:F1', 4, NULL, NULL, NULL, NULL),
(138, 'A2:B3:C4:D5:E6:F1', 5, NULL, NULL, NULL, NULL),
(139, 'A2:B3:C4:D5:E6:F1', 6, NULL, NULL, NULL, NULL),
(140, 'A2:B3:C4:D5:E6:F1', 7, NULL, NULL, NULL, NULL),
(141, 'A2:B3:C4:D5:E6:F1', 8, NULL, NULL, NULL, NULL),
(142, 'A2:B3:C4:D5:E6:F1', 9, NULL, NULL, NULL, NULL),
(143, 'A2:B3:C4:D5:E6:F1', 10, NULL, NULL, NULL, NULL),
(144, 'A2:B3:C4:D5:E6:F1', 11, NULL, NULL, NULL, NULL),
(145, 'A2:B3:C4:D5:E6:F1', 12, NULL, NULL, NULL, NULL),
(146, 'A2:B3:C4:D5:E6:F1', 13, NULL, NULL, NULL, NULL),
(147, 'A2:B3:C4:D5:E6:F1', 14, NULL, NULL, NULL, NULL),
(148, 'B2:C3:D4:E5:F6:A1', 1, NULL, NULL, NULL, NULL),
(149, 'B2:C3:D4:E5:F6:A1', 2, NULL, NULL, NULL, NULL),
(150, 'B2:C3:D4:E5:F6:A1', 3, NULL, NULL, NULL, NULL),
(151, 'B2:C3:D4:E5:F6:A1', 4, NULL, NULL, NULL, NULL),
(152, 'B2:C3:D4:E5:F6:A1', 5, NULL, NULL, NULL, NULL),
(153, 'B2:C3:D4:E5:F6:A1', 6, NULL, NULL, NULL, NULL),
(154, 'B2:C3:D4:E5:F6:A1', 7, NULL, NULL, NULL, NULL),
(155, 'B2:C3:D4:E5:F6:A1', 8, NULL, NULL, NULL, NULL),
(156, 'B2:C3:D4:E5:F6:A1', 9, NULL, NULL, NULL, NULL),
(157, 'B2:C3:D4:E5:F6:A1', 10, NULL, NULL, NULL, NULL),
(158, 'B2:C3:D4:E5:F6:A1', 11, NULL, NULL, NULL, NULL),
(159, 'B2:C3:D4:E5:F6:A1', 12, NULL, NULL, NULL, NULL),
(160, 'B2:C3:D4:E5:F6:A1', 13, NULL, NULL, NULL, NULL),
(161, 'B2:C3:D4:E5:F6:A1', 14, NULL, NULL, NULL, NULL),
(162, 'C2:D3:E4:F5:A6:B1', 1, NULL, NULL, NULL, NULL),
(163, 'C2:D3:E4:F5:A6:B1', 2, NULL, NULL, NULL, NULL),
(164, 'C2:D3:E4:F5:A6:B1', 3, NULL, NULL, NULL, NULL),
(165, 'C2:D3:E4:F5:A6:B1', 4, NULL, NULL, NULL, NULL),
(166, 'C2:D3:E4:F5:A6:B1', 5, NULL, NULL, NULL, NULL),
(167, 'C2:D3:E4:F5:A6:B1', 6, NULL, NULL, NULL, NULL),
(168, 'C2:D3:E4:F5:A6:B1', 7, NULL, NULL, NULL, NULL),
(169, 'C2:D3:E4:F5:A6:B1', 8, NULL, NULL, NULL, NULL),
(170, 'C2:D3:E4:F5:A6:B1', 9, NULL, NULL, NULL, NULL),
(171, 'C2:D3:E4:F5:A6:B1', 10, NULL, NULL, NULL, NULL),
(172, 'C2:D3:E4:F5:A6:B1', 11, NULL, NULL, NULL, NULL),
(173, 'C2:D3:E4:F5:A6:B1', 12, NULL, NULL, NULL, NULL),
(174, 'C2:D3:E4:F5:A6:B1', 13, NULL, NULL, NULL, NULL),
(175, 'C2:D3:E4:F5:A6:B1', 14, NULL, NULL, NULL, NULL),
(176, 'D2:E3:F4:A5:B6:C1', 1, NULL, NULL, NULL, NULL),
(177, 'D2:E3:F4:A5:B6:C1', 2, NULL, NULL, NULL, NULL),
(178, 'D2:E3:F4:A5:B6:C1', 3, NULL, NULL, NULL, NULL),
(179, 'D2:E3:F4:A5:B6:C1', 4, NULL, NULL, NULL, NULL),
(180, 'D2:E3:F4:A5:B6:C1', 5, NULL, NULL, NULL, NULL),
(181, 'D2:E3:F4:A5:B6:C1', 6, NULL, NULL, NULL, NULL),
(182, 'D2:E3:F4:A5:B6:C1', 7, NULL, NULL, NULL, NULL),
(183, 'D2:E3:F4:A5:B6:C1', 8, NULL, NULL, NULL, NULL),
(184, 'D2:E3:F4:A5:B6:C1', 9, NULL, NULL, NULL, NULL),
(185, 'D2:E3:F4:A5:B6:C1', 10, NULL, NULL, NULL, NULL),
(186, 'D2:E3:F4:A5:B6:C1', 11, NULL, NULL, NULL, NULL),
(187, 'D2:E3:F4:A5:B6:C1', 12, NULL, NULL, NULL, NULL),
(188, 'D2:E3:F4:A5:B6:C1', 13, NULL, NULL, NULL, NULL),
(189, 'D2:E3:F4:A5:B6:C1', 14, NULL, NULL, NULL, NULL),
(190, 'E2:F3:A4:B5:C6:D1', 1, NULL, NULL, NULL, NULL),
(191, 'E2:F3:A4:B5:C6:D1', 2, NULL, NULL, NULL, NULL),
(192, 'E2:F3:A4:B5:C6:D1', 3, NULL, NULL, NULL, NULL),
(193, 'E2:F3:A4:B5:C6:D1', 4, NULL, NULL, NULL, NULL),
(194, 'E2:F3:A4:B5:C6:D1', 5, NULL, NULL, NULL, NULL),
(195, 'E2:F3:A4:B5:C6:D1', 6, NULL, NULL, NULL, NULL),
(196, 'E2:F3:A4:B5:C6:D1', 7, NULL, NULL, NULL, NULL),
(197, 'E2:F3:A4:B5:C6:D1', 8, NULL, NULL, NULL, NULL),
(198, 'E2:F3:A4:B5:C6:D1', 9, NULL, NULL, NULL, NULL),
(199, 'E2:F3:A4:B5:C6:D1', 10, NULL, NULL, NULL, NULL),
(200, 'E2:F3:A4:B5:C6:D1', 11, NULL, NULL, NULL, NULL),
(201, 'E2:F3:A4:B5:C6:D1', 12, NULL, NULL, NULL, NULL),
(202, 'E2:F3:A4:B5:C6:D1', 13, NULL, NULL, NULL, NULL),
(203, 'E2:F3:A4:B5:C6:D1', 14, NULL, NULL, NULL, NULL),
(204, 'F2:A3:B4:C5:D6:E1', 1, NULL, NULL, NULL, NULL),
(205, 'F2:A3:B4:C5:D6:E1', 2, NULL, NULL, NULL, NULL),
(206, 'F2:A3:B4:C5:D6:E1', 3, NULL, NULL, NULL, NULL),
(207, 'F2:A3:B4:C5:D6:E1', 4, NULL, NULL, NULL, NULL),
(208, 'F2:A3:B4:C5:D6:E1', 5, NULL, NULL, NULL, NULL),
(209, 'F2:A3:B4:C5:D6:E1', 6, NULL, NULL, NULL, NULL),
(210, 'F2:A3:B4:C5:D6:E1', 7, NULL, NULL, NULL, NULL),
(211, 'F2:A3:B4:C5:D6:E1', 8, NULL, NULL, NULL, NULL),
(212, 'F2:A3:B4:C5:D6:E1', 9, NULL, NULL, NULL, NULL),
(213, 'F2:A3:B4:C5:D6:E1', 10, NULL, NULL, NULL, NULL),
(214, 'F2:A3:B4:C5:D6:E1', 11, NULL, NULL, NULL, NULL),
(215, 'F2:A3:B4:C5:D6:E1', 12, NULL, NULL, NULL, NULL),
(216, 'F2:A3:B4:C5:D6:E1', 13, NULL, NULL, NULL, NULL),
(217, 'F2:A3:B4:C5:D6:E1', 14, NULL, NULL, NULL, NULL),
(218, 'A3:B4:C5:D6:E1:F2', 1, NULL, NULL, NULL, NULL),
(219, 'A3:B4:C5:D6:E1:F2', 2, NULL, NULL, NULL, NULL),
(220, 'A3:B4:C5:D6:E1:F2', 3, NULL, NULL, NULL, NULL),
(221, 'A3:B4:C5:D6:E1:F2', 4, NULL, NULL, NULL, NULL),
(222, 'A3:B4:C5:D6:E1:F2', 5, NULL, NULL, NULL, NULL),
(223, 'A3:B4:C5:D6:E1:F2', 6, NULL, NULL, NULL, NULL),
(224, 'A3:B4:C5:D6:E1:F2', 7, NULL, NULL, NULL, NULL),
(225, 'A3:B4:C5:D6:E1:F2', 8, NULL, NULL, NULL, NULL),
(226, 'A3:B4:C5:D6:E1:F2', 9, NULL, NULL, NULL, NULL),
(227, 'A3:B4:C5:D6:E1:F2', 10, NULL, NULL, NULL, NULL),
(228, 'A3:B4:C5:D6:E1:F2', 11, NULL, NULL, NULL, NULL),
(229, 'A3:B4:C5:D6:E1:F2', 12, NULL, NULL, NULL, NULL),
(230, 'A3:B4:C5:D6:E1:F2', 13, NULL, NULL, NULL, NULL),
(231, 'A3:B4:C5:D6:E1:F2', 14, NULL, NULL, NULL, NULL),
(232, 'B3:C4:D5:E6:F1:A2', 1, NULL, NULL, NULL, NULL),
(233, 'B3:C4:D5:E6:F1:A2', 2, NULL, NULL, NULL, NULL),
(234, 'B3:C4:D5:E6:F1:A2', 3, NULL, NULL, NULL, NULL),
(235, 'B3:C4:D5:E6:F1:A2', 4, NULL, NULL, NULL, NULL),
(236, 'B3:C4:D5:E6:F1:A2', 5, NULL, NULL, NULL, NULL),
(237, 'B3:C4:D5:E6:F1:A2', 6, NULL, NULL, NULL, NULL),
(238, 'B3:C4:D5:E6:F1:A2', 7, NULL, NULL, NULL, NULL),
(239, 'B3:C4:D5:E6:F1:A2', 8, NULL, NULL, NULL, NULL),
(240, 'B3:C4:D5:E6:F1:A2', 9, NULL, NULL, NULL, NULL),
(241, 'B3:C4:D5:E6:F1:A2', 10, NULL, NULL, NULL, NULL),
(242, 'B3:C4:D5:E6:F1:A2', 11, NULL, NULL, NULL, NULL),
(243, 'B3:C4:D5:E6:F1:A2', 12, NULL, NULL, NULL, NULL),
(244, 'B3:C4:D5:E6:F1:A2', 13, NULL, NULL, NULL, NULL),
(245, 'B3:C4:D5:E6:F1:A2', 14, NULL, NULL, NULL, NULL),
(246, 'C3:D4:E5:F6:A1:B2', 1, NULL, NULL, NULL, NULL),
(247, 'C3:D4:E5:F6:A1:B2', 2, NULL, NULL, NULL, NULL),
(248, 'C3:D4:E5:F6:A1:B2', 3, NULL, NULL, NULL, NULL),
(249, 'C3:D4:E5:F6:A1:B2', 4, NULL, NULL, NULL, NULL),
(250, 'C3:D4:E5:F6:A1:B2', 5, NULL, NULL, NULL, NULL),
(251, 'C3:D4:E5:F6:A1:B2', 6, NULL, NULL, NULL, NULL),
(252, 'C3:D4:E5:F6:A1:B2', 7, NULL, NULL, NULL, NULL),
(253, 'C3:D4:E5:F6:A1:B2', 8, NULL, NULL, NULL, NULL),
(254, 'C3:D4:E5:F6:A1:B2', 9, NULL, NULL, NULL, NULL),
(255, 'C3:D4:E5:F6:A1:B2', 10, NULL, NULL, NULL, NULL),
(256, 'C3:D4:E5:F6:A1:B2', 11, NULL, NULL, NULL, NULL),
(257, 'C3:D4:E5:F6:A1:B2', 12, NULL, NULL, NULL, NULL),
(258, 'C3:D4:E5:F6:A1:B2', 13, NULL, NULL, NULL, NULL),
(259, 'C3:D4:E5:F6:A1:B2', 14, NULL, NULL, NULL, NULL),
(260, 'D3:E4:F5:A6:B1:C2', 1, NULL, NULL, NULL, NULL),
(261, 'D3:E4:F5:A6:B1:C2', 2, NULL, NULL, NULL, NULL),
(262, 'D3:E4:F5:A6:B1:C2', 3, NULL, NULL, NULL, NULL),
(263, 'D3:E4:F5:A6:B1:C2', 4, NULL, NULL, NULL, NULL),
(264, 'D3:E4:F5:A6:B1:C2', 5, NULL, NULL, NULL, NULL),
(265, 'D3:E4:F5:A6:B1:C2', 6, NULL, NULL, NULL, NULL),
(266, 'D3:E4:F5:A6:B1:C2', 7, NULL, NULL, NULL, NULL),
(267, 'D3:E4:F5:A6:B1:C2', 8, NULL, NULL, NULL, NULL),
(268, 'D3:E4:F5:A6:B1:C2', 9, NULL, NULL, NULL, NULL),
(269, 'D3:E4:F5:A6:B1:C2', 10, NULL, NULL, NULL, NULL),
(270, 'D3:E4:F5:A6:B1:C2', 11, NULL, NULL, NULL, NULL),
(271, 'D3:E4:F5:A6:B1:C2', 12, NULL, NULL, NULL, NULL),
(272, 'D3:E4:F5:A6:B1:C2', 13, NULL, NULL, NULL, NULL),
(273, 'D3:E4:F5:A6:B1:C2', 14, NULL, NULL, NULL, NULL),
(274, 'E3:F4:A5:B6:C1:D2', 1, NULL, NULL, NULL, NULL),
(275, 'E3:F4:A5:B6:C1:D2', 2, NULL, NULL, NULL, NULL),
(276, 'E3:F4:A5:B6:C1:D2', 3, NULL, NULL, NULL, NULL),
(277, 'E3:F4:A5:B6:C1:D2', 4, NULL, NULL, NULL, NULL),
(278, 'E3:F4:A5:B6:C1:D2', 5, NULL, NULL, NULL, NULL),
(279, 'E3:F4:A5:B6:C1:D2', 6, NULL, NULL, NULL, NULL),
(280, 'E3:F4:A5:B6:C1:D2', 7, NULL, NULL, NULL, NULL),
(281, 'E3:F4:A5:B6:C1:D2', 8, NULL, NULL, NULL, NULL),
(282, 'E3:F4:A5:B6:C1:D2', 9, NULL, NULL, NULL, NULL),
(283, 'E3:F4:A5:B6:C1:D2', 10, NULL, NULL, NULL, NULL),
(284, 'E3:F4:A5:B6:C1:D2', 11, NULL, NULL, NULL, NULL),
(285, 'E3:F4:A5:B6:C1:D2', 12, NULL, NULL, NULL, NULL),
(286, 'E3:F4:A5:B6:C1:D2', 13, NULL, NULL, NULL, NULL),
(287, 'E3:F4:A5:B6:C1:D2', 14, NULL, NULL, NULL, NULL),
(288, 'F3:A4:B5:C6:D1:E2', 1, NULL, NULL, NULL, NULL),
(289, 'F3:A4:B5:C6:D1:E2', 2, NULL, NULL, NULL, NULL),
(290, 'F3:A4:B5:C6:D1:E2', 3, NULL, NULL, NULL, NULL),
(291, 'F3:A4:B5:C6:D1:E2', 4, NULL, NULL, NULL, NULL),
(292, 'F3:A4:B5:C6:D1:E2', 5, NULL, NULL, NULL, NULL),
(293, 'F3:A4:B5:C6:D1:E2', 6, NULL, NULL, NULL, NULL),
(294, 'F3:A4:B5:C6:D1:E2', 7, NULL, NULL, NULL, NULL),
(295, 'F3:A4:B5:C6:D1:E2', 8, NULL, NULL, NULL, NULL),
(296, 'F3:A4:B5:C6:D1:E2', 9, NULL, NULL, NULL, NULL),
(297, 'F3:A4:B5:C6:D1:E2', 10, NULL, NULL, NULL, NULL),
(298, 'F3:A4:B5:C6:D1:E2', 11, NULL, NULL, NULL, NULL),
(299, 'F3:A4:B5:C6:D1:E2', 12, NULL, NULL, NULL, NULL),
(300, 'F3:A4:B5:C6:D1:E2', 13, NULL, NULL, NULL, NULL),
(301, 'F3:A4:B5:C6:D1:E2', 14, NULL, NULL, NULL, NULL),
(302, 'A4:B5:C6:D1:E2:F3', 1, NULL, NULL, NULL, NULL),
(303, 'A4:B5:C6:D1:E2:F3', 2, NULL, NULL, NULL, NULL),
(304, 'A4:B5:C6:D1:E2:F3', 3, NULL, NULL, NULL, NULL),
(305, 'A4:B5:C6:D1:E2:F3', 4, NULL, NULL, NULL, NULL),
(306, 'A4:B5:C6:D1:E2:F3', 5, NULL, NULL, NULL, NULL),
(307, 'A4:B5:C6:D1:E2:F3', 6, NULL, NULL, NULL, NULL),
(308, 'A4:B5:C6:D1:E2:F3', 7, NULL, NULL, NULL, NULL),
(309, 'A4:B5:C6:D1:E2:F3', 8, NULL, NULL, NULL, NULL),
(310, 'A4:B5:C6:D1:E2:F3', 9, NULL, NULL, NULL, NULL),
(311, 'A4:B5:C6:D1:E2:F3', 10, NULL, NULL, NULL, NULL),
(312, 'A4:B5:C6:D1:E2:F3', 11, NULL, NULL, NULL, NULL),
(313, 'A4:B5:C6:D1:E2:F3', 12, NULL, NULL, NULL, NULL),
(314, 'A4:B5:C6:D1:E2:F3', 13, NULL, NULL, NULL, NULL),
(315, 'A4:B5:C6:D1:E2:F3', 14, NULL, NULL, NULL, NULL),
(316, 'B4:C5:D6:E1:F2:A3', 1, NULL, NULL, NULL, NULL),
(317, 'B4:C5:D6:E1:F2:A3', 2, NULL, NULL, NULL, NULL),
(318, 'B4:C5:D6:E1:F2:A3', 3, NULL, NULL, NULL, NULL),
(319, 'B4:C5:D6:E1:F2:A3', 4, NULL, NULL, NULL, NULL),
(320, 'B4:C5:D6:E1:F2:A3', 5, NULL, NULL, NULL, NULL),
(321, 'B4:C5:D6:E1:F2:A3', 6, NULL, NULL, NULL, NULL),
(322, 'B4:C5:D6:E1:F2:A3', 7, NULL, NULL, NULL, NULL),
(323, 'B4:C5:D6:E1:F2:A3', 8, NULL, NULL, NULL, NULL),
(324, 'B4:C5:D6:E1:F2:A3', 9, NULL, NULL, NULL, NULL),
(325, 'B4:C5:D6:E1:F2:A3', 10, NULL, NULL, NULL, NULL),
(326, 'B4:C5:D6:E1:F2:A3', 11, NULL, NULL, NULL, NULL),
(327, 'B4:C5:D6:E1:F2:A3', 12, NULL, NULL, NULL, NULL),
(328, 'B4:C5:D6:E1:F2:A3', 13, NULL, NULL, NULL, NULL),
(329, 'B4:C5:D6:E1:F2:A3', 14, NULL, NULL, NULL, NULL),
(330, 'F0:9E:9E:22:6E:80', 1, NULL, NULL, NULL, NULL),
(331, 'F0:9E:9E:22:6E:80', 2, NULL, NULL, NULL, NULL),
(332, 'F0:9E:9E:22:6E:80', 3, NULL, NULL, NULL, NULL),
(333, 'F0:9E:9E:22:6E:80', 4, NULL, NULL, NULL, NULL),
(334, 'F0:9E:9E:22:6E:80', 5, NULL, NULL, NULL, NULL),
(335, 'F0:9E:9E:22:6E:80', 6, NULL, NULL, NULL, NULL),
(336, 'F0:9E:9E:22:6E:80', 7, NULL, NULL, NULL, NULL),
(337, 'F0:9E:9E:22:6E:80', 8, NULL, NULL, NULL, NULL),
(338, 'F0:9E:9E:22:6E:80', 9, NULL, NULL, NULL, NULL),
(339, 'F0:9E:9E:22:6E:80', 10, NULL, NULL, NULL, NULL),
(340, 'F0:9E:9E:22:6E:80', 11, NULL, NULL, NULL, NULL),
(341, 'F0:9E:9E:22:6E:80', 12, NULL, NULL, NULL, 12),
(342, 'F0:9E:9E:22:6E:80', 13, NULL, NULL, 14, NULL),
(343, 'F0:9E:9E:22:6E:80', 14, '2025-05-14 09:28:55', 14, NULL, NULL);

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
('30:30:F9:72:22:8C', 14, 'Casa 1', 3),
('A1:B2:C3:D4:E5:F6', 13, 'Unnamed', 0),
('A2:B3:C4:D5:E6:F1', 18, 'Residencia Rivera', 0),
('A3:B4:C5:D6:E1:F2', 13, 'Hospitalito Central', 0),
('A4:B5:C6:D1:E2:F3', 18, 'Centro Asistencial', 0),
('B1:C2:D3:E4:F5:A6', 14, 'Casa del Abuelo', 0),
('B2:C3:D4:E5:F6:A1', NULL, 'Centro de Día', 0),
('B3:C4:D5:E6:F1:A2', 14, 'Centro de Salud', 2),
('B4:C5:D6:E1:F2:A3', NULL, 'Asilo Los Ángeles', 0),
('C1:D2:E3:F4:A5:B6', 18, 'Clínica Central', 0),
('C2:D3:E4:F5:A6:B1', 13, 'Unnamed', 0),
('C3:D4:E5:F6:A1:B2', 18, 'Unnamed', 0),
('D1:E2:F3:A4:B5:C6', NULL, 'Hospitalito', 0),
('D2:E3:F4:A5:B6:C1', 14, 'Clínica Familiar', 0),
('D3:E4:F5:A6:B1:C2', NULL, 'Casa Aurora', 0),
('E1:F2:A3:B4:C5:D6', 13, 'Asilo Esperanza', 0),
('E2:F3:A4:B5:C6:D1', 18, 'Asilo San José', 0),
('E3:F4:A5:B6:C1:D2', 13, 'Residencia Norte', 0),
('F0:9E:9E:22:6E:80', 14, 'Unnamed', 3),
('F2:A3:B4:C5:D6:E1', NULL, 'Casa de los Abuelos', 0),
('F3:A4:B5:C6:D1:E2', 14, 'Clínica Nueva Vida', 4);

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
(8, '30:30:F9:72:22:8C', 'Amoxi', '2024-11-05 00:44:00', '2024-11-16 20:00:00', '12:49:00', 5, 0, 1),
(9, '30:30:F9:72:22:8C', 'Vitamina C', '2024-11-08 08:00:00', '2024-11-20 20:00:00', '24:00:00', 1, 0, 0),
(10, '30:30:F9:72:22:8C', 'Antihistamínico', '2024-11-09 09:00:00', '2024-11-19 21:00:00', '12:00:00', 4, 1, 2),
(13, 'F0:9E:9E:22:6E:80', 'Prueba 1 Secuencial', '2025-05-01 07:15:00', '2025-05-01 08:00:00', '23:59:00', 8, 1, 0),
(14, 'F0:9E:9E:22:6E:80', 'Prueba 2 Secuencial', '2025-05-14 18:05:28', '2025-05-17 00:00:28', '05:00:00', 2, 0, 0);

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

--
-- Volcado de datos para la tabla `single_mode`
--

INSERT INTO `single_mode` (`id`, `mac`, `medicine_name`, `dispensing_date`) VALUES
(7, '30:30:F9:72:22:8C', 'Ibuprofeno', '2024-11-11 04:54:00'),
(8, '30:30:F9:72:22:8C', 'Amoxicilina', '2024-11-07 07:15:00'),
(9, '30:30:F9:72:22:8C', 'Vitamina C', '2024-11-08 12:00:00'),
(10, '30:30:F9:72:22:8C', 'Antihistamínico', '2024-11-09 20:45:00'),
(14, 'F0:9E:9E:22:6E:80', 'Pastilla #1', '2025-05-24 20:33:00');

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
(94, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3NDc4ODMzNjYsImV4cCI6MTc0ODQ4ODE2Nn0.I_WQwkvFGLHtrTBar07D40ExV6Ld84Jzypcuv1Y43hU', 1748488166),
(95, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3NDgwNjM3MzksImV4cCI6MTc0ODY2ODUzOX0.slotMDl9n5le6OB25rcnnOxxrCKIFKBT1NOgr8cxTWI', 1748668539),
(96, 14, 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZV9lbWFpbCI6Ik0yIiwicGFzc3dvcmQiOiJYdkc4VE1OY0c4Tjdla1p3YmpjWXlRPT0iLCJpYXQiOjE3NDgxNDA5ODQsImV4cCI6MTc0ODc0NTc4NH0.gFSPmZK7P1E9cpCWKqi40TAOec9ry0VnsMRM_2bidZU', 1748745784);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `cell`
--
ALTER TABLE `cell`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=344;

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `single_mode`
--
ALTER TABLE `single_mode`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `tokens`
--
ALTER TABLE `tokens`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=97;

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
