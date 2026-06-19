-- â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•
--  DEPORTE CONNECT â€” Datos iniciales (H2 y PostgreSQL)
-- â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•â•

-- CatÃ¡logo de deportes (solo si estÃ¡ vacÃ­o)
INSERT INTO deportes (name, icon_key, color)
SELECT * FROM (
  SELECT 'FÃºtbol'     AS name, 'ic_soccer'     AS icon_key, '#22C55E' AS color UNION ALL
  SELECT 'PÃ¡del',                'ic_padel',                '#7C3AED'          UNION ALL
  SELECT 'BÃ¡squetbol',           'ic_basketball',           '#2563EB'          UNION ALL
  SELECT 'Tenis',                'ic_tennis',               '#111111'          UNION ALL
  SELECT 'VÃ³leibol',             'ic_volleyball',           '#3B82F6'          UNION ALL
  SELECT 'Running',              'ic_flash',                '#F97316'
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM deportes);

-- Ubicaciones de ejemplo (Santiago de Chile)
INSERT INTO ubicaciones (name, address, commune, city, latitude, longitude)
SELECT * FROM (
  SELECT 'Complejo La Cisterna' AS name, 'Av. El ParrÃ³n 0938' AS address, 'La Cisterna' AS commune, 'Santiago' AS city, -33.5348 AS latitude, -70.6619 AS longitude UNION ALL
  SELECT 'Club de Polo',               'Av. Kennedy 5413',          'Vitacura',      'Santiago', -33.3987, -70.5840 UNION ALL
  SELECT 'Parque Providencia',         'Av. Pocuro 1515',           'Providencia',   'Santiago', -33.4319, -70.6110 UNION ALL
  SELECT 'Cancha MaipÃº',               'Av. Pajaritos 3030',        'MaipÃº',         'Santiago', -33.5112, -70.7580 UNION ALL
  SELECT 'Centro Deportivo San Miguel','Gran Avenida 4221',         'San Miguel',    'Santiago', -33.4960, -70.6510
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM ubicaciones);

-- Nota: no cargamos usuarios ni actividades de ejemplo.
-- Los usuarios se crean al registrarse vÃ­a POST /auth/register
-- y las actividades vÃ­a POST /actividades
