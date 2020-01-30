CREATE TABLE IF NOT EXISTS USERS (
    ID   VARCHAR(255)     NOT NULL,
    USERNAME VARCHAR(255) NOT NULL,
    PASSWORD VARCHAR(255) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS NOTES (
    ID   VARCHAR(255)     NOT NULL,
    USER_ID VARCHAR(255) NOT NULL,
    NOTE VARCHAR(500) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS TAGS (
    ID   VARCHAR(255)     NOT NULL,
    USER_ID VARCHAR(255) NOT NULL,
    TAG VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
);

CREATE TABLE IF NOT EXISTS NOTES_TAGS (
    NOTE_ID VARCHAR(255)  NOT NULL,
    TAG_ID VARCHAR(255) NOT NULL
);