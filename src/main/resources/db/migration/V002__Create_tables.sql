-- category table
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    type VARCHAR(50) NOT NULL,
    parent_id UUID REFERENCES categories(id),
    description TEXT,
    code VARCHAR(100) NOT NULL,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON categories FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON categories FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE UNIQUE INDEX udx_categories_code  ON categories(code);
CREATE INDEX idx_categories_parent_id  ON categories(parent_id);
CREATE INDEX idx_categories_type  ON categories(type);


-- genres table
CREATE TABLE genres (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    code VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id UUID REFERENCES genres(id),
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL
);


CREATE TRIGGER insert_timestamp BEFORE INSERT ON genres FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON genres FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE UNIQUE INDEX udx_genres_code  ON genres(code);
CREATE INDEX idx_genres_parent_id  ON genres(parent_id);


-- tags table
CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    code VARCHAR(100) NOT NULL,
    description TEXT,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON tags FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON tags FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE UNIQUE INDEX udx_tags_code  ON tags(code);

-- tag_translation table
CREATE TABLE tag_translations (
    tag_id UUID NOT NULL,
    language_code VARCHAR(10) NOT NULL,
    value VARCHAR(255),
    disable_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    PRIMARY KEY (tag_id, language_code),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON tag_translations FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON tag_translations FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

-- contents table
CREATE TABLE contents (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    genre_id UUID NOT NULL REFERENCES genres(id),
    speciality_id UUID NOT NULL,
    type VARCHAR(45),
    structure_type VARCHAR(45),
    name TEXT,
    description TEXT,
    featured BOOLEAN NOT NULL,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON contents FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON contents FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE INDEX idx_contents_type  ON contents(type);
CREATE INDEX idx_contents_featured  ON contents(featured);
CREATE INDEX idx_contents_genre_id  ON contents(genre_id);
CREATE INDEX idx_contents_speciality_id  ON contents(speciality_id);

-- content_info
CREATE TABLE content_info (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    content_id UUID NOT NULL,
    type VARCHAR(45),
    value TEXT,
    disabled_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    FOREIGN KEY (content_id) REFERENCES contents(id)
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON content_info FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON content_info FOR EACH ROW EXECUTE PROCEDURE update_timestamps();


-- content_groups (junction)
CREATE TABLE content_groups (
    parent_content_id UUID NOT NULL REFERENCES contents(id),
    content_id UUID NOT NULL REFERENCES contents(id),
    sort_order INTEGER NOT NULL,
    disabled_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    PRIMARY KEY (parent_content_id, content_id)
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON content_groups FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON content_groups FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

-- content_tags (junction)
CREATE TABLE content_tags (
    tag_id UUID NOT NULL REFERENCES tags(id),
    content_id UUID NOT NULL REFERENCES contents(id),
    disabled_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    PRIMARY KEY (tag_id, content_id)
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON content_tags FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON content_tags FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

-- subtitles table
CREATE TABLE subtitles (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    content_id UUID NOT NULL REFERENCES contents(id),
    language_code VARCHAR(10) NOT NULL,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    priority INTEGER NOT NULL
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON subtitles FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON subtitles FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE UNIQUE INDEX udx_subtitles_content_id_language_code  ON subtitles(content_id,language_code);

-- content_categories table
CREATE TABLE content_categories (
    content_id UUID NOT NULL,
    category_id UUID NOT NULL,
    disabled_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    PRIMARY KEY (content_id, category_id),
    FOREIGN KEY (content_id) REFERENCES contents(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE TRIGGER insert_timestamp BEFORE INSERT ON content_categories FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON content_categories FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

-- content_sections table
CREATE TABLE content_sections (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    type VARCHAR(100) NOT NULL,
    title TEXT,
    title_code VARCHAR(50) NOT NULL,
    number_of_elements INTEGER NOT NULL,
    sort_type VARCHAR(50) NOT NULL,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON content_sections FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON content_sections FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

-- subscription_plan table
CREATE TABLE subscription_plan (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    vat_percentage DECIMAL(6,2) NOT NULL,
    currency_code VARCHAR(5) NOT NULL,
    country_code VARCHAR(5) NOT NULL,
    disable_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON subscription_plan FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON subscription_plan FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE INDEX idx_subscription_plan_country_code  ON subscription_plan(country_code);


-- subscription_plan_content (junction)
CREATE TABLE subscription_plan_content (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    subscription_plan_id UUID NOT NULL REFERENCES subscription_plan(id),
    content_id UUID NOT NULL REFERENCES contents(id),
    disable_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON subscription_plan_content FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON subscription_plan_content FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE UNIQUE INDEX udx_subscription_plan_content_subscription_plan_id_content_id  ON subscription_plan_content(subscription_plan_id,content_id);

-- subscription_plan_users (junction)
CREATE TABLE subscription_plan_users (
    subscription_plan_id UUID NOT NULL REFERENCES subscription_plan(id),
    user_id UUID NOT NULL,
    disable_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    PRIMARY KEY (subscription_plan_id, user_id)
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON subscription_plan_users FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON subscription_plan_users FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE INDEX idx_subscription_plan_users_user_id  ON subscription_plan_users(user_id);
