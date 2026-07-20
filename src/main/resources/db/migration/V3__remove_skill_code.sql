alter table skill
  remove column code;

update tenant
set slug = lower(regexp_replace(name, '[^a-zA-Z0-9]+', '-', 'g'))
where slug is null;

alter table tenant
  alter column slug set not null;

create unique index if not exists ux_tenant_slug on tenant (slug);