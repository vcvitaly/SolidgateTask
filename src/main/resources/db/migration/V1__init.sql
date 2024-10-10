create table public.users
(
    id      BIGSERIAL not null
        constraint users_pk
            primary key,
    name    TEXT      not null,
    balance BIGINT    not null
);

create table public.balance_update_request
(
    id              SERIAL      not null
        constraint balance_update_request_pk
            primary key,
    idempotency_key uuid        not null,
    status          VARCHAR(20) not null,
    error           VARCHAR(5000),
    request         jsonb       not null
);

create unique index balance_update_request__idempotency_key__idx
    on public.balance_update_request (idempotency_key);