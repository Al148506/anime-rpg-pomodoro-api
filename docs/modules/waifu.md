# Waifu Module

## Responsibility

The waifu module manages global waifu catalog data and user-owned skin unlocks.

## Entities

`Waifu` is global catalog data.

`WaifuSkinUnlock` belongs to a user and references a waifu.

## Endpoints

`GET /api/v1/waifus`

`POST /api/v1/waifus`

`GET /api/v1/waifus/skins/unlocks`

`POST /api/v1/waifus/{waifuId}/skins/unlock`

## Business Rules

Waifu names are unique.

Skin code is unique per user and waifu.

Only authenticated users can unlock skins.
