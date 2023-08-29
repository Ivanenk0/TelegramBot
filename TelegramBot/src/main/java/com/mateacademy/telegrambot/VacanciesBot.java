package com.mateacademy.telegrambot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Component
public class VacanciesBot extends TelegramLongPollingBot {

    // Unique bot token that was received via Telegram @BotFather
    public VacanciesBot() {
        super("6328282484:AAGlymbZffA0YE6wggt4UO4kaBW9a0YJtJQ");
    }

    // User actions handling method
    @Override
    public void onUpdateReceived(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Welcome to JAVA vacancies bot\nSelect your preferences in proficiency level");
        sendMessage.setReplyMarkup(getStartMenu());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private ReplyKeyboard getStartMenu() {
        List<InlineKeyboardButton> proficiencyLevelButtons = new ArrayList<>();

        InlineKeyboardButton juniorButton = new InlineKeyboardButton();
        juniorButton.setText("JUNIOR");
        juniorButton.setCallbackData("showJuniorVacancies");
        proficiencyLevelButtons.add(juniorButton);

        InlineKeyboardButton middleButton = new InlineKeyboardButton();
        middleButton.setText("MIDDLE");
        middleButton.setCallbackData("showMiddleVacancies");
        proficiencyLevelButtons.add(middleButton);

        InlineKeyboardButton seniorButton = new InlineKeyboardButton();
        seniorButton.setText("SENIOR");
        seniorButton.setCallbackData("showSeniorVacancies");
        proficiencyLevelButtons.add(seniorButton);

        InlineKeyboardMarkup proficiencyLevelKeyboard = new InlineKeyboardMarkup();
        proficiencyLevelKeyboard.setKeyboard(List.of(proficiencyLevelButtons));

        return proficiencyLevelKeyboard;
    }

    // Bot name that was registered in Telegram @BotFather
    @Override
    public String getBotUsername() {
        return "ma vacancies bot";
    }
}
