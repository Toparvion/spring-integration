-- Autogenerated: do not edit this file

CREATE TABLE INT_MESSAGE  (
	MESSAGE_ID CHAR(36) NOT NULL PRIMARY KEY,
	REGION VARCHAR(100),
	CREATED_DATE DATETIME NOT NULL,
	MESSAGE_BYTES BLOB
) ENGINE=InnoDB;

CREATE TABLE INT_MESSAGE_GROUP  (
	MESSAGE_ID CHAR(36) NOT NULL,
	GROUP_KEY CHAR(36) NOT NULL,
	REGION VARCHAR(100),
	MARKED BIGINT,
	CREATED_DATE DATETIME NOT NULL,
	UPDATED_DATE DATETIME DEFAULT NULL,
	MESSAGE_BYTES BLOB,
	constraint MESSAGE_GROUP_PK primary key (MESSAGE_ID, GROUP_KEY)
) ENGINE=InnoDB;
