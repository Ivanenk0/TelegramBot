package com.mateacademy.telegrambot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class VacanciesBot extends TelegramLongPollingBot {

    // Unique bot token that was received via Telegram @BotFather
    public VacanciesBot() {
        super("6328282484:AAGlymbZffA0YE6wggt4UO4kaBW9a0YJtJQ");
    }

    // User actions handling method
    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Event received");
    }

    // Bot name that was registered in Telegram @BotFather
    @Override
    public String getBotUsername() {
        return "ma vacancies bot";
    }
}
