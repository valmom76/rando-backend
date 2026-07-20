alter table tenant
  add column if not exists slug varchar(80);

update tenant
set slug = lower(regexp_replace(name, '[^a-zA-Z0-9]+', '-', 'g'))
where slug is null;

alter table tenant
  alter column slug set not null;

create unique index if not exists ux_tenant_slug on tenant (slug);