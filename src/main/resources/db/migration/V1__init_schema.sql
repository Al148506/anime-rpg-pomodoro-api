create table if not exists users (
    id varchar(255) primary key,
    username varchar(50) not null unique,
    email varchar(120) not null unique,
    password varchar(255) not null,
    role varchar(255) not null,
    created_at timestamp not null
);

create table if not exists categories (
    id varchar(255) primary key,
    name varchar(80) not null unique,
    slug varchar(80) not null unique,
    color varchar(20),
    emoji varchar(20)
);

create table if not exists tasks (
    id varchar(255) primary key,
    title varchar(120) not null,
    description varchar(1000),
    completed boolean not null,
    priority varchar(255) not null,
    xp_reward integer not null,
    pomodoro_sessions integer not null,
    due_date timestamp,
    category_id varchar(255),
    user_id varchar(255),
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_tasks_category foreign key (category_id) references categories(id),
    constraint fk_tasks_user foreign key (user_id) references users(id)
);

create table if not exists pomodoros (
    id varchar(255) primary key,
    name varchar(120) not null,
    type varchar(30) not null,
    duration_minutes integer not null,
    short_break_minutes integer not null,
    long_break_minutes integer not null,
    sessions_before_long_break integer not null,
    task_id varchar(255),
    user_id varchar(255) not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_pomodoros_task foreign key (task_id) references tasks(id),
    constraint fk_pomodoros_user foreign key (user_id) references users(id)
);

create table if not exists repetition_settings (
    id varchar(255) primary key,
    enabled boolean not null,
    repeat_count integer not null,
    repeat_daily boolean not null,
    pomodoro_id varchar(255) not null unique,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_repetition_settings_pomodoro foreign key (pomodoro_id) references pomodoros(id)
);

create table if not exists pomodoro_sessions (
    id varchar(255) primary key,
    user_id varchar(255) not null,
    pomodoro_id varchar(255),
    task_id varchar(255),
    type varchar(30) not null,
    status varchar(30) not null,
    planned_duration_minutes integer not null,
    actual_duration_minutes integer,
    started_at timestamp not null,
    completed_at timestamp,
    notes varchar(1000),
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_pomodoro_sessions_user foreign key (user_id) references users(id),
    constraint fk_pomodoro_sessions_pomodoro foreign key (pomodoro_id) references pomodoros(id),
    constraint fk_pomodoro_sessions_task foreign key (task_id) references tasks(id)
);

create table if not exists player_progress (
    id varchar(255) primary key,
    user_id varchar(255) not null unique,
    level integer not null,
    total_xp integer not null,
    completed_focus_sessions integer not null,
    current_streak_days integer not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_player_progress_user foreign key (user_id) references users(id)
);

create table if not exists achievements (
    id varchar(255) primary key,
    user_id varchar(255) not null,
    code varchar(80) not null,
    title varchar(120) not null,
    description varchar(1000),
    type varchar(30) not null,
    xp_reward integer not null,
    unlocked_at timestamp not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_achievements_user foreign key (user_id) references users(id),
    constraint uk_achievement_user_code unique (user_id, code)
);

create table if not exists waifus (
    id varchar(255) primary key,
    name varchar(120) not null unique,
    description varchar(1000),
    default_skin_code varchar(80) not null,
    rarity varchar(30) not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table if not exists waifu_skin_unlocks (
    id varchar(255) primary key,
    user_id varchar(255) not null,
    waifu_id varchar(255) not null,
    skin_code varchar(80) not null,
    skin_name varchar(120) not null,
    unlock_source varchar(120) not null,
    unlocked_at timestamp not null,
    created_at timestamp not null,
    updated_at timestamp not null,
    constraint fk_waifu_skin_unlocks_user foreign key (user_id) references users(id),
    constraint fk_waifu_skin_unlocks_waifu foreign key (waifu_id) references waifus(id),
    constraint uk_waifu_skin_unlock_user_waifu_skin unique (user_id, waifu_id, skin_code)
);
