package com.betul.model;

public class Word {
    private int id;
    private int userId;
    private String wordText;
    private String translationText;
    private int difficulty;
    private int mastery;
    private String category;

    public Word() {}

    public Word(int id, int userId, String wordText, String translationText, int difficulty, int mastery, String category) {
        this.id = id;
        this.userId = userId;
        this.wordText = wordText;
        this.translationText = translationText;
        this.difficulty = difficulty;
        this.mastery = mastery;
        this.category = category;
    }

    // --- GETTER VE SETTER METOTLARI ---
    // JavaFX TableView'un (PropertyValueFactory) veriyi okuyabilmesi için bu isimler kritik!
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getWordText() { return wordText; }
    public void setWordText(String wordText) { this.wordText = wordText; }

    public String getTranslationText() { return translationText; }
    public void setTranslationText(String translationText) { this.translationText = translationText; }

    public int getDifficulty() { return difficulty; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }

    public int getMastery() { return mastery; }
    public void setMastery(int mastery) { this.mastery = mastery; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}