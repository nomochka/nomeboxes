package com.nome.nomeboxes.backpack;

import java.util.Locale;

public enum BackpackType {

    SMALL("small", "Малый рюкзак", 9,
            "Обычный", "#AAAAAA", "#F0DFC8", "#B07A45"),
    MEDIUM("medium", "Средний рюкзак", 27,
            "Необычный", "#55FF55", "#F0C987", "#96613A"),
    LARGE("large", "Большой рюкзак", 45,
            "Редкий", "#55FFFF", "#F0B26B", "#7A4E2E"),
    SUPER("super", "Супербольшой рюкзак", 54,
            "Легендарный", "#FFAA00", "#FFE29A", "#8A5A33");

    private final String id;
    private final String title;
    private final int slots;
    private final String rarity;
    private final String rarityColor;
    private final String gradientFrom;
    private final String gradientTo;

    BackpackType(String id, String title, int slots,
                 String rarity, String rarityColor,
                 String gradientFrom, String gradientTo) {
        this.id = id;
        this.title = title;
        this.slots = slots;
        this.rarity = rarity;
        this.rarityColor = rarityColor;
        this.gradientFrom = gradientFrom;
        this.gradientTo = gradientTo;
    }

    public String id() {
        return id;
    }

    public String title() {
        return title;
    }

    public int slots() {
        return slots;
    }

    public String rarity() {
        return rarity;
    }

    public String rarityColor() {
        return rarityColor;
    }

    public String gradientFrom() {
        return gradientFrom;
    }

    public String gradientTo() {
        return gradientTo;
    }

    public static BackpackType byId(String value) {
        if (value == null) {
            return null;
        }
        for (BackpackType type : values()) {
            if (type.id.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return switch (value.toLowerCase(Locale.ROOT)) {
            case "малый", "м" -> SMALL;
            case "средний", "с" -> MEDIUM;
            case "большой", "б" -> LARGE;
            case "супер", "супербольшой" -> SUPER;
            default -> null;
        };
    }
}
