INSERT INTO moderators (username, password, is_admin)
VALUES ('admin', 'b45cffe084dd3d20d928bee85e7b0f21', true);


-- seed for members
INSERT INTO eventapp.members (username, email, password, is_email_verified, privacy, status)
VALUES
    ('painfully', '333vahe777@gmail.com', 'b45cffe084dd3d20d928bee85e7b0f21', FALSE, 0, 0),
    ('painfully', '333vahan777@gmail.com', 'b45cffe084dd3d20d928bee85e7b0f21', FALSE, 0, 0);



-- seed for event category
INSERT INTO event_category (id, name)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Концерт'),
    ('22222222-2222-2222-2222-222222222222', 'Спорт'),
    ('33333333-3333-3333-3333-333333333333', 'Образование')
    ON CONFLICT DO NOTHING;

-- seed for organizers
INSERT INTO organizers (id, username, email, password, organization_name, description, accreditation_status, status, sphere_of_activity)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'org1', 'org1@example.com', 'b45cffe084dd3d20d928bee85e7b0f21', 'Org One', 'Организация мероприятий', TRUE, 1, 'Развлечения'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'org2', 'org2@example.com', 'b45cffe084dd3d20d928bee85e7b0f21', 'Org Two', 'Спортивные мероприятия', TRUE, 1, 'Спорт')
    ON CONFLICT DO NOTHING;

-- seed for events
-- seed for events
INSERT INTO events (
    id, title, description, organizer_id, format, location, event_category_id,
    latitude, longitude, start_time, end_time, contact_info,
    moderation_status, status_info, image
) VALUES
      (
          'e1e1e1e1-e1e1-e1e1-e1e1-e1e1e1e1e1e1',
          'Музыкальный фестиваль',
          'Большой open-air музыкальный фестиваль на свежем воздухе.',
          'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
          'OFFLINE',
          'Площадь Республики, Ереван',
          '11111111-1111-1111-1111-111111111111',
          40.1792, 44.4991,
          '2025-07-10 18:00:00',
          '2025-07-10 23:00:00',
          'contact@festival.am',
          1,
          'Событие подтверждено',
          'https://example.com/image1.jpg'
      ),
      (
          'e2e2e2e2-e2e2-e2e2-e2e2-e2e2e2e2e2e2',
          'Бег на 10 км',
          'Спортивное мероприятие для любителей и профессионалов.',
          'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
          'ONLINE',
          'Центральный парк, Ереван',
          '22222222-2222-2222-2222-222222222222',
          40.1811, 44.5122,
          '2025-08-15 07:00:00',
          '2025-08-15 10:00:00',
          'sport@org2.com',
          1,
          'Регистрация открыта',
          'https://example.com/image2.jpg'
      )
    ON CONFLICT DO NOTHING;



--Добавим ещё 25 мероприятий
DO $$
BEGIN
FOR i IN 1..25 LOOP
        INSERT INTO events (
            id, title, description, organizer_id, format, location, event_category_id,
            latitude, longitude, start_time, end_time, contact_info,
            moderation_status, status_info, image
        ) VALUES (
            gen_random_uuid(),
            'Событие #' || i,
            'Описание мероприятия номер ' || i,
            CASE WHEN i % 2 = 0 THEN 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa'::uuid
                 ELSE 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb'::uuid END,
            CASE WHEN i % 2 = 0 THEN 'OFFLINE' ELSE 'ONLINE' END,
            'Улица Арамяна, Ереван',
            CASE
                WHEN i % 3 = 0 THEN '11111111-1111-1111-1111-111111111111'::uuid
                WHEN i % 3 = 1 THEN '22222222-2222-2222-2222-222222222222'::uuid
                ELSE '33333333-3333-3333-3333-333333333333'::uuid
            END,
            40.17 + i * 0.001, 44.51 + i * 0.001,
            now() + (i || ' days')::interval,
            now() + ((i+1) || ' days')::interval,
            'info@sobitie' || i || '.am',
            1,
            'Описание статуса #' || i,
            'https://example.com/image' || i || '.jpg'
        ) ON CONFLICT DO NOTHING;
END LOOP;
END $$;



