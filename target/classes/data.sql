-- ─────────────────────────────────────────────────────────────
--  Initial seed data  ·  runs on every startup (INSERT IGNORE)
-- ─────────────────────────────────────────────────────────────

INSERT IGNORE INTO scopes (name, description)
VALUES
    ('user.read',   'Read user data'),
    ('user.write',  'Create and update users'),
    ('user.delete', 'Delete users'),
    ('admin',       'Full administrative access');
