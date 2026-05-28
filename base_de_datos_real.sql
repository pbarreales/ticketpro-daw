
CREATE DATABASE IF NOT EXISTS `ticketpro`;
USE `ticketpro`;

-- 1. TABLA: usuarios
CREATE TABLE IF NOT EXISTS `usuarios` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rol` VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'cliente',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. TABLA: categorias
CREATE TABLE IF NOT EXISTS `categorias` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. TABLA: incidencias
CREATE TABLE IF NOT EXISTS `incidencias` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `titulo` VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` TEXT COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'Pendiente',
  `fecha_creacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `categoria_id` INT DEFAULT NULL,
  `cliente_id` INT DEFAULT NULL,
  `informatico_id` INT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `categoria_id` (`categoria_id`),
  KEY `cliente_id` (`cliente_id`),
  KEY `informatico_id` (`informatico_id`),
  CONSTRAINT `incidencias_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`) ON DELETE SET NULL,
  CONSTRAINT `incidencias_ibfk_2` FOREIGN KEY (`cliente_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  CONSTRAINT `incidencias_ibfk_3` FOREIGN KEY (`informatico_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;