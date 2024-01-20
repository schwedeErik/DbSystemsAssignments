CREATE OR REPLACE FUNCTION getContainerSize(d1 TEXT) RETURNS
INT AS $$
DECLARE
  d1SizeStr TEXT;
BEGIN
  d1SizeStr=split_part(d1, ' ', 1);
  if (d1SizeStr) = 'WRAP' then return 0; end if;
  if (d1SizeStr) = 'SM' then return 1; end if;
  if (d1SizeStr) = 'MED' then return 2; end if;
  if (d1SizeStr) = 'LG' then return 3; end if;
  if (d1SizeStr) = 'JUMBO' then return 4; end if;
END;
$$ LANGUAGE plpgsql;