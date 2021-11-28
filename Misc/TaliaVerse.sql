-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 28, 2021 at 07:28 PM
-- Server version: 10.4.21-MariaDB
-- PHP Version: 8.0.11

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `test`
--

-- --------------------------------------------------------

--
-- Table structure for table `filedata`
--

CREATE TABLE `filedata` (
  `Username` text CHARACTER SET utf16 COLLATE utf16_turkish_ci NOT NULL,
  `Filename` text CHARACTER SET utf16 COLLATE utf16_turkish_ci NOT NULL,
  `bitmap_filename` text NOT NULL,
  `Type` enum('image','video') NOT NULL DEFAULT 'image'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `filedata`
--

INSERT INTO `filedata` (`Username`, `Filename`, `bitmap_filename`, `Type`) VALUES
('Deniz', 'temp-images\\3403155379.jpeg', 'temp-images\\Thumbnails\\3403155379.jpeg.bmp', 'image'),
('Deniz', 'temp-images\\1910764209.jpeg', 'temp-images\\Thumbnails\\1910764209.jpeg.bmp', 'image'),
('Deniz', 'temp-images\\2064164039.jpeg', 'temp-images\\Thumbnails\\2064164039.jpeg.bmp', 'image'),
('Deniz', 'temp-images\\1716102461.jpeg', 'temp-images\\Thumbnails\\1716102461.jpeg.bmp', 'image'),
('Deniz', 'temp-images\\Deniz\\1354579004.tli', 'temp-images\\Thumbnails\\54579004.tlb', 'image');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(120) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`) VALUES
(7, 'Test', '132'),
(8, 'Deniz', '');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
