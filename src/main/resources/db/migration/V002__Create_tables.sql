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


-- gender table
CREATE TABLE genders (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    code VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id UUID REFERENCES genders(id),
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL
);


CREATE TRIGGER insert_timestamp BEFORE INSERT ON genders FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON genders FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE UNIQUE INDEX udx_genders_code  ON genders(code);
CREATE INDEX idx_genders_parent_id  ON genders(parent_id);


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

-- content table
CREATE TABLE content (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    featured BOOLEAN NOT NULL,
    type VARCHAR(50) NOT NULL,
    category_id UUID NOT NULL REFERENCES categories(id),
    name VARCHAR(250) NOT NULL,
    description TEXT,
    gender_id UUID NOT NULL REFERENCES genders(id),
    especialidad_id UUID NOT NULL,
    resource_url TEXT NOT NULL,
    cdn_url TEXT,
    rating DECIMAL(2,1),
    duration VARCHAR(20),
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON content FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON content FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE INDEX idx_content_type  ON content(type);
CREATE INDEX idx_content_featured  ON content(featured);
CREATE INDEX idx_content_category_id  ON content(category_id);
CREATE INDEX idx_content_gender_id  ON content(gender_id);
CREATE INDEX idx_content_especialidad_id  ON content(especialidad_id);

-- groups table
CREATE TABLE groups (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    type VARCHAR(50) NOT NULL,
    category_id UUID,
    featured BOOLEAN NOT NULL,
    name VARCHAR(250) NOT NULL,
    description TEXT,
    gender_id UUID NOT NULL REFERENCES genders(id),
    episodes INTEGER,
    duration VARCHAR(20),
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON groups FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON groups FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE INDEX idx_groups_type  ON groups(type);
CREATE INDEX idx_groups_featured  ON groups(featured);
CREATE INDEX idx_groups_category_id  ON groups(category_id);

-- content_groups (junction)
CREATE TABLE content_groups (
    content_id UUID NOT NULL REFERENCES content(id),
    group_id UUID NOT NULL REFERENCES groups(id),
    sort_order INTEGER NOT NULL,
    disable_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    PRIMARY KEY (content_id, group_id)
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON content_groups FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON content_groups FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

-- content_tags (junction)
CREATE TABLE content_tags (
    tag_id UUID NOT NULL REFERENCES tags(id),
    content_id UUID NOT NULL REFERENCES content(id),
    disable_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    PRIMARY KEY (tag_id, content_id)
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON content_tags FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON content_tags FOR EACH ROW EXECUTE PROCEDURE update_timestamps();


-- series_tags (junction)
CREATE TABLE groups_tags (
    tag_id UUID NOT NULL REFERENCES tags(id),
    group_id UUID NOT NULL REFERENCES groups(id),
    disable_date TIMESTAMP,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    PRIMARY KEY (tag_id, group_id)
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON groups_tags FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON groups_tags FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

-- subtitles table
CREATE TABLE subtitles (
    id UUID PRIMARY KEY DEFAULT public.uuid_generate_v4(),
    content_id UUID NOT NULL REFERENCES content(id),
    language_code VARCHAR(10) NOT NULL,
    create_date TIMESTAMP NOT NULL,
    update_date TIMESTAMP NOT NULL,
    priority INTEGER NOT NULL
);

CREATE TRIGGER insert_timestamp BEFORE INSERT ON subtitles FOR EACH ROW EXECUTE PROCEDURE insert_timestamps();
CREATE TRIGGER update_timestamp BEFORE UPDATE ON subtitles FOR EACH ROW EXECUTE PROCEDURE update_timestamps();

CREATE UNIQUE INDEX udx_subtitles_content_id_language_code  ON subtitles(content_id,language_code);

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
    content_id UUID NOT NULL REFERENCES content(id),
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
