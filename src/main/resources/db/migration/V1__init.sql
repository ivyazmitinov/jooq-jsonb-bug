CREATE TABLE metainfo_cache
(
    cache_part_name text NOT NULL,
    tenant_id       VARCHAR,
    value           jsonb,
    CONSTRAINT metainfo_cache_pk PRIMARY KEY (cache_part_name, tenant_id)
);