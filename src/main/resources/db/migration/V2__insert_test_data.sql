INSERT INTO moderators (username, password, is_admin)
VALUES ('admin', 'b45cffe084dd3d20d928bee85e7b0f21', true);


-- seed for members
INSERT INTO eventapp.members (username, email, password, is_email_verified, privacy, status)
VALUES
    ('painfully', '333vahe777@gmail.com', 'b45cffe084dd3d20d928bee85e7b0f21', FALSE, 0, 0),
    ('painfully', '333vahan777@gmail.com', 'b45cffe084dd3d20d928bee85e7b0f21', FALSE, 0, 0)
    ON CONFLICT (email) DO NOTHING;


-- seed for event category
INSERT INTO event_category (id, name)
VALUES
    ('11111111-1111-1111-1111-111111111111', 'Концерт'),
    ('22222222-2222-2222-2222-222222222222', 'Спорт'),
    ('33333333-3333-3333-3333-333333333333', 'Образование')
    ON CONFLICT (id) DO NOTHING;

-- seed for organizers
INSERT INTO organizers (id, username, email, password, organization_name, description, is_email_verified, accreditation_status, status, sphere_of_activity)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'org1', 'org1@example.com', 'b45cffe084dd3d20d928bee85e7b0f21', 'Org One', 'Организация мероприятий', false, TRUE, 1, 'Развлечения'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'org2', 'org2@example.com', 'b45cffe084dd3d20d928bee85e7b0f21', 'Org Two', 'Спортивные мероприятия', true, TRUE, 1, 'Спорт')
    ON CONFLICT (email) DO NOTHING;


INSERT INTO event_tags (id, name)
VALUES
    ('11111111-1111-1111-1111-111111111112', 'Музыка'),
    ('22222222-2222-2222-2222-222222222221', 'Спорт'),
    ('33333333-3333-3333-3333-333333333331', 'Кино'),
    ('44444444-4444-4444-4444-444444444444', 'Образование'),
    ('55555555-5555-5555-5555-555555555555', 'Технологии'),
    ('66666666-6666-6666-6666-666666666666', 'Искусство'),
    ('77777777-7777-7777-7777-777777777777', 'Театр'),
    ('88888888-8888-8888-8888-888888888888', 'Кулинария');





-- seed for events
INSERT INTO events (
    id, title, description, organizer_id, format, location, event_category_id,
    latitude, longitude, start_time, end_time, contact_info,
    moderation_status, status_info, image, public_id
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
          'https://example.com/image1.jpg',
          gen_random_uuid()
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
          'https://example.com/image2.jpg',
          gen_random_uuid()
      )
    ON CONFLICT DO NOTHING;




DO $$
BEGIN
FOR i IN 1..25 LOOP
    INSERT INTO events (
        id, title, description, organizer_id, format, location, event_category_id,
        latitude, longitude, start_time, end_time, contact_info,
        moderation_status, status_info, image, public_id
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
        'https://example.com/image' || i || '.jpg',
        gen_random_uuid()
    ) ON CONFLICT DO NOTHING;
END LOOP;
END $$;