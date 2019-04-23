create table account (accNumber BIGINT NOT NULL,
balance DECIMAL) ENGINE = MEMORY;
/**
 * First, Second user created for data warming
 */
INSERT INTO account values(1000,89090.73);
