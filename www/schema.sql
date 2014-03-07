-- phpMyAdmin SQL Dump
-- version 4.0.5
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Mar 06, 2014 at 11:38 PM
-- Server version: 5.0.96-community-log
-- PHP Version: 5.3.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `munchfsu_munch`
--

-- --------------------------------------------------------

--
-- Table structure for table `ingredients`
--

CREATE TABLE IF NOT EXISTS `ingredients` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(256) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4451 ;

-- --------------------------------------------------------

--
-- Table structure for table `recipe_ingredients`
--

CREATE TABLE IF NOT EXISTS `recipe_ingredients` (
  `recipe` int(11) NOT NULL,
  `ingredient` int(11) NOT NULL,
  `amt` tinytext NOT NULL,
  `info` tinytext NOT NULL,
  KEY `recipe` (`recipe`),
  KEY `ingredient` (`ingredient`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `recipes`
--

CREATE TABLE IF NOT EXISTS `recipes` (
  `id` int(11) NOT NULL,
  `name` tinytext NOT NULL,
  `yield` int(11) NOT NULL,
  `instructions` text NOT NULL,
  `cost_per_recipe` float NOT NULL,
  `cost_per_serving` float NOT NULL,
  `source` tinytext NOT NULL,
  `notes` text NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user_auth_tokens`
--

CREATE TABLE IF NOT EXISTS `user_auth_tokens` (
  `uid` varchar(23) NOT NULL,
  `token` varchar(40) NOT NULL,
  `created` int(11) NOT NULL,
  PRIMARY KEY  (`token`),
  KEY `created` (`created`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `user_ingredient_list`
--

CREATE TABLE IF NOT EXISTS `user_ingredient_list` (
  `type` enum('shopping','inventory') NOT NULL,
  `user` varchar(23) NOT NULL,
  `id` int(11) NOT NULL auto_increment,
  `ingredient` int(11) NOT NULL,
  `created` int(11) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `user` (`user`,`type`,`ingredient`),
  UNIQUE KEY `type` (`type`,`user`,`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=4 ;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `uid` int(11) NOT NULL auto_increment,
  `unique_id` varchar(23) NOT NULL,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `encrypted_password` varchar(80) NOT NULL,
  `salt` varchar(10) NOT NULL,
  `created_at` datetime default NULL,
  `updated_at` datetime default NULL,
  PRIMARY KEY  (`uid`),
  UNIQUE KEY `unique_id` (`unique_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=19 ;
