create table users
(
    id      SERIAL not null
        constraint users_pk
            primary key,
    name    TEXT      not null,
    balance INT    not null
);

create table balance_update_requests
(
    id              SERIAL      not null
        constraint balance_update_requests_pk
            primary key,
    idempotency_key uuid        not null,
    status          VARCHAR(20) not null,
    error           VARCHAR(5000),
    request         TEXT        not null
);

create unique index balance_update_requests__idempotency_key__idx
    on balance_update_requests (idempotency_key);