-- Credenciales demo:
-- organizador.demo@deporteconnect.cl / Demo1234
-- participante.demo@deporteconnect.cl / Demo1234

DO $$
DECLARE
  demo_password_hash TEXT := '$2a$10$cy3ro0rUh7pwM0q.467aBuYpwPpa628W6jUfU6TDcxKtC/mn00aHq';

  v_sport_id BIGINT;
  v_location_id BIGINT;
  v_organizer_id BIGINT;
  v_participant_id BIGINT;
  v_open_activity_id BIGINT;
  v_finished_activity_id BIGINT;
BEGIN
  INSERT INTO deportes (name, icon_key, color)
  VALUES
    ('Fútbol', 'ic_soccer', '#22C55E'),
    ('Pádel', 'ic_padel', '#7C3AED'),
    ('Básquetbol', 'ic_basketball', '#2563EB')
  ON CONFLICT (name) DO NOTHING;

  INSERT INTO ubicaciones (name, address, commune, city, latitude, longitude)
  SELECT 'Complejo Demo DeporteConnect', 'Av. Demo 1234', 'Providencia', 'Santiago', -33.4319, -70.6110
  WHERE NOT EXISTS (
    SELECT 1 FROM ubicaciones
    WHERE name = 'Complejo Demo DeporteConnect'
      AND address = 'Av. Demo 1234'
  );

  SELECT id INTO v_sport_id
  FROM deportes
  WHERE name = 'Fútbol'
  LIMIT 1;

  SELECT id INTO v_location_id
  FROM ubicaciones
  WHERE name = 'Complejo Demo DeporteConnect'
  LIMIT 1;

  INSERT INTO usuarios (
    email, password_hash, full_name, phone, birth_date, gender,
    avatar_url, bio, skill_level, profile_complete,
    participant_rating, participant_rating_count,
    organizer_rating, organizer_rating_count,
    created_at, updated_at
  )
  VALUES (
    'organizador.demo@deporteconnect.cl',
    demo_password_hash,
    'Organizador Demo',
    '912345678',
    DATE '1995-01-01',
    'HOMBRE',
    'color:#185FA5',
    'Usuario demo para organizar actividades.',
    'INTERMEDIO',
    TRUE,
    0.0,
    0,
    0.0,
    0,
    NOW(),
    NOW()
  )
  ON CONFLICT (email) DO UPDATE SET
    password_hash = EXCLUDED.password_hash,
    full_name = EXCLUDED.full_name,
    phone = EXCLUDED.phone,
    birth_date = EXCLUDED.birth_date,
    gender = EXCLUDED.gender,
    avatar_url = EXCLUDED.avatar_url,
    bio = EXCLUDED.bio,
    skill_level = EXCLUDED.skill_level,
    profile_complete = TRUE,
    updated_at = NOW();

  INSERT INTO usuarios (
    email, password_hash, full_name, phone, birth_date, gender,
    avatar_url, bio, skill_level, profile_complete,
    participant_rating, participant_rating_count,
    organizer_rating, organizer_rating_count,
    created_at, updated_at
  )
  VALUES (
    'participante.demo@deporteconnect.cl',
    demo_password_hash,
    'Participante Demo',
    '923456789',
    DATE '2000-01-01',
    'MUJER',
    'color:#0F6E56',
    'Usuario demo para participar y calificar.',
    'PRINCIPIANTE',
    TRUE,
    0.0,
    0,
    0.0,
    0,
    NOW(),
    NOW()
  )
  ON CONFLICT (email) DO UPDATE SET
    password_hash = EXCLUDED.password_hash,
    full_name = EXCLUDED.full_name,
    phone = EXCLUDED.phone,
    birth_date = EXCLUDED.birth_date,
    gender = EXCLUDED.gender,
    avatar_url = EXCLUDED.avatar_url,
    bio = EXCLUDED.bio,
    skill_level = EXCLUDED.skill_level,
    profile_complete = TRUE,
    updated_at = NOW();

  SELECT id INTO v_organizer_id
  FROM usuarios
  WHERE email = 'organizador.demo@deporteconnect.cl';

  SELECT id INTO v_participant_id
  FROM usuarios
  WHERE email = 'participante.demo@deporteconnect.cl';

  INSERT INTO actividades (
    title, sport_id, location_id, organizer_id, event_at,
    max_participants, price, level, gender, requires_reservation,
    description, status, created_at
  )
  SELECT
    'Demo actividad abierta',
    v_sport_id,
    v_location_id,
    v_organizer_id,
    NOW() + INTERVAL '3 days',
    10,
    0.00,
    'PRINCIPIANTE',
    'MIXTO',
    FALSE,
    'Actividad demo abierta para probar feed, unión y reportes.',
    'OPEN',
    NOW()
  WHERE NOT EXISTS (
    SELECT 1 FROM actividades
    WHERE title = 'Demo actividad abierta'
      AND organizer_id = v_organizer_id
  );

  INSERT INTO actividades (
    title, sport_id, location_id, organizer_id, event_at,
    max_participants, price, level, gender, requires_reservation,
    description, status, created_at
  )
  SELECT
    'Demo actividad finalizada',
    v_sport_id,
    v_location_id,
    v_organizer_id,
    NOW() - INTERVAL '2 days',
    10,
    0.00,
    'INTERMEDIO',
    'MIXTO',
    FALSE,
    'Actividad demo finalizada para probar calificación del organizador.',
    'FINISHED',
    NOW() - INTERVAL '5 days'
  WHERE NOT EXISTS (
    SELECT 1 FROM actividades
    WHERE title = 'Demo actividad finalizada'
      AND organizer_id = v_organizer_id
  );

  SELECT id INTO v_open_activity_id
  FROM actividades
  WHERE title = 'Demo actividad abierta'
    AND organizer_id = v_organizer_id
  LIMIT 1;

  SELECT id INTO v_finished_activity_id
  FROM actividades
  WHERE title = 'Demo actividad finalizada'
    AND organizer_id = v_organizer_id
  LIMIT 1;

  INSERT INTO participaciones (user_id, activity_id, role, status, joined_at)
  VALUES
    (v_organizer_id, v_open_activity_id, 'ORGANIZADOR', 'INSCRITO', NOW()),
    (v_organizer_id, v_finished_activity_id, 'ORGANIZADOR', 'INSCRITO', NOW() - INTERVAL '5 days'),
    (v_participant_id, v_finished_activity_id, 'PARTICIPANTE', 'INSCRITO', NOW() - INTERVAL '5 days')
  ON CONFLICT (user_id, activity_id) DO UPDATE SET
    role = EXCLUDED.role,
    status = EXCLUDED.status;

  INSERT INTO activity_reports (
    activity_id, reporter_id, reason, description, created_at
  )
  VALUES (
    v_open_activity_id,
    v_participant_id,
    'Reporte demo',
    'Reporte de prueba para validar moderación.',
    NOW()
  )
  ON CONFLICT (activity_id, reporter_id) DO NOTHING;

  INSERT INTO organizer_ratings (
    activity_id, organizer_id, rater_id, stars, created_at
  )
  VALUES (
    v_finished_activity_id,
    v_organizer_id,
    v_participant_id,
    5,
    NOW()
  )
  ON CONFLICT (activity_id, rater_id) DO NOTHING;

  UPDATE usuarios u
  SET
    organizer_rating = COALESCE(r.avg_stars, 0.0),
    organizer_rating_count = COALESCE(r.rating_count, 0),
    updated_at = NOW()
  FROM (
    SELECT
      organizer_id,
      ROUND(AVG(stars)::numeric, 1) AS avg_stars,
      COUNT(*)::int AS rating_count
    FROM organizer_ratings
    WHERE organizer_id = v_organizer_id
    GROUP BY organizer_id
  ) r
  WHERE u.id = r.organizer_id;
END $$;

SELECT
  id,
  email,
  full_name,
  profile_complete,
  organizer_rating,
  organizer_rating_count
FROM usuarios
WHERE email IN (
  'organizador.demo@deporteconnect.cl',
  'participante.demo@deporteconnect.cl'
)
ORDER BY email;

SELECT
  a.id,
  a.title,
  a.status,
  a.event_at,
  u.email AS organizer_email,
  a.current_participants
FROM (
  SELECT
    act.*,
    COUNT(p.id)::int AS current_participants
  FROM actividades act
  LEFT JOIN participaciones p ON p.activity_id = act.id
  WHERE act.title IN ('Demo actividad abierta', 'Demo actividad finalizada')
  GROUP BY act.id
) a
JOIN usuarios u ON u.id = a.organizer_id
ORDER BY a.title;

SELECT
  ar.id,
  ar.activity_id,
  a.title,
  u.email AS reporter_email,
  ar.reason,
  ar.created_at
FROM activity_reports ar
JOIN actividades a ON a.id = ar.activity_id
JOIN usuarios u ON u.id = ar.reporter_id
WHERE a.title = 'Demo actividad abierta';

SELECT
  r.id,
  r.activity_id,
  a.title,
  org.email AS organizer_email,
  rat.email AS rater_email,
  r.stars,
  r.created_at
FROM organizer_ratings r
JOIN actividades a ON a.id = r.activity_id
JOIN usuarios org ON org.id = r.organizer_id
JOIN usuarios rat ON rat.id = r.rater_id
WHERE a.title = 'Demo actividad finalizada';
