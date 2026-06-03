-- ============================================
-- BoKe 数据库表结构
-- ============================================

CREATE TABLE IF NOT EXISTS users (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    avatar_color VARCHAR(100) DEFAULT 'linear-gradient(135deg, #5B8C5A, #7EC8A8)',
    avatar_url VARCHAR(500),
    bio VARCHAR(500),
    role ENUM('admin', 'user') DEFAULT 'user',
    status ENUM('active', 'muted', 'banned') DEFAULT 'active',
    balance INT UNSIGNED NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS posts (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    author_id INT UNSIGNED NOT NULL,
    title VARCHAR(255) NOT NULL,
    summary VARCHAR(500),
    cover VARCHAR(500),
    content LONGTEXT,
    post_type ENUM('post', 'announcement') DEFAULT 'post',
    view_count INT UNSIGNED DEFAULT 0,
    is_published TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS tags (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS post_tags (
    post_id INT UNSIGNED NOT NULL,
    tag_id INT UNSIGNED NOT NULL,
    PRIMARY KEY (post_id, tag_id),
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS feed_items (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    author_id INT UNSIGNED NOT NULL,
    content TEXT NOT NULL,
    topic VARCHAR(100),
    like_count INT UNSIGNED DEFAULT 0,
    comment_count INT UNSIGNED DEFAULT 0,
    repost_count INT UNSIGNED DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS feed_images (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    feed_id INT UNSIGNED NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    sort_order INT UNSIGNED DEFAULT 0,
    FOREIGN KEY (feed_id) REFERENCES feed_items(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS feed_likes (
    feed_id INT UNSIGNED NOT NULL,
    user_id INT UNSIGNED NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (feed_id, user_id),
    FOREIGN KEY (feed_id) REFERENCES feed_items(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS feed_comments (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    feed_id INT UNSIGNED NOT NULL,
    user_id INT UNSIGNED NOT NULL,
    content TEXT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (feed_id) REFERENCES feed_items(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS follows (
    follower_id INT UNSIGNED NOT NULL,
    followed_id INT UNSIGNED NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (follower_id, followed_id),
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (followed_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS messages (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED,
    guest_name VARCHAR(50),
    content TEXT NOT NULL,
    is_visible TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS site_config (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value TEXT,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS site_visits (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    page_path VARCHAR(255),
    visitor_ip VARCHAR(45),
    user_id INT UNSIGNED,
    visited_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS entertainment_items (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    price INT UNSIGNED NOT NULL DEFAULT 0,
    badge VARCHAR(20),
    stock INT UNSIGNED NOT NULL DEFAULT 0,
    wish_count INT UNSIGNED NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    sort_order INT UNSIGNED NOT NULL DEFAULT 0,
    status ENUM('active', 'offline') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_entertainment_items_category (category),
    INDEX idx_entertainment_items_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS entertainment_games (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    short_name VARCHAR(20),
    genre VARCHAR(50),
    description VARCHAR(500),
    tags VARCHAR(255),
    rating VARCHAR(20),
    players INT UNSIGNED NOT NULL DEFAULT 0,
    image_url VARCHAR(500),
    detail_background VARCHAR(700),
    demo_title VARCHAR(100),
    demo_text VARCHAR(500),
    sort_order INT UNSIGNED NOT NULL DEFAULT 0,
    status ENUM('active', 'offline') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_entertainment_games_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS entertainment_game_ranks (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    game_id INT UNSIGNED NOT NULL,
    player_name VARCHAR(50) NOT NULL,
    score INT UNSIGNED NOT NULL DEFAULT 0,
    sort_order INT UNSIGNED NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (game_id) REFERENCES entertainment_games(id) ON DELETE CASCADE,
    INDEX idx_entertainment_game_ranks_game_score (game_id, score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS entertainment_events (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    icon VARCHAR(20),
    description VARCHAR(500),
    reward INT UNSIGNED NOT NULL DEFAULT 0,
    default_done TINYINT(1) DEFAULT 0,
    sort_order INT UNSIGNED NOT NULL DEFAULT 0,
    status ENUM('active', 'offline') DEFAULT 'active',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_entertainment_events_status_sort (status, sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS entertainment_wallets (
    user_id INT UNSIGNED PRIMARY KEY,
    coins INT UNSIGNED NOT NULL DEFAULT 0,
    checked_in_date DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS entertainment_event_records (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    event_id INT UNSIGNED NOT NULL,
    completed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_entertainment_event_user (user_id, event_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES entertainment_events(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS entertainment_game_plays (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED,
    game_id INT UNSIGNED NOT NULL,
    score INT UNSIGNED NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (game_id) REFERENCES entertainment_games(id) ON DELETE CASCADE,
    INDEX idx_entertainment_game_plays_game_score (game_id, score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
