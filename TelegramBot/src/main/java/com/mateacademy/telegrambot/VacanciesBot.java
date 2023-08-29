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

    // Unique vacancy identification value
    private int vacancyId = 0;

    // Unique bot token that was received via Telegram @BotFather
    public VacanciesBot() {
        super("6328282484:AAGlymbZffA0YE6wggt4UO4kaBW9a0YJtJQ");
    }

    // User actions handling method
    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.getMessage() != null) executeStartCommand(update);
            if (update.getCallbackQuery() != null) {
                String callbackData = update.getCallbackQuery().getData();

                if (callbackData.startsWith("vacancyId = ")) {
                    String id = callbackData.split("=")[1];
                    this.executeShowVacancyDescription(id, update);
                } else {
                    switch (callbackData) {
                        case "showJuniorVacancies" -> this.executeShowVacancies(update, ProficiencyLevel.JUNIOR);
                        case "showMiddleVacancies" -> this.executeShowVacancies(update, ProficiencyLevel.MIDDLE);
                        case "showSeniorVacancies" -> this.executeShowVacancies(update, ProficiencyLevel.SENIOR);
                        default -> this.handleUnexpectedError(update);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't send message to user", e);
        }
    }

    // Show vacancies description by User request
    private void executeShowVacancyDescription(String id, Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setText("Vacancy description template\nID : " + id);
        execute(sendMessage);
    }

    // Show vacancies list for Junior lvl by User request from start menu button
    private void executeShowVacancies(Update update, ProficiencyLevel level) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Vacancies list for " + level.toString() + " Developer :");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(getVacanciesList(level));
        execute(sendMessage);
    }

    // Load possible vacancies and form a list of it
    private ReplyKeyboard getVacanciesList(ProficiencyLevel level) {
        List<InlineKeyboardButton> vacanciesListButtons = new ArrayList<>();

        InlineKeyboardButton vacancyTemplate = new InlineKeyboardButton();
        vacancyTemplate.setText("Template Button for " + level.toString() + " Developer Vacancy");
        vacancyTemplate.setCallbackData("vacancyId = " + vacancyId);
        vacanciesListButtons.add(vacancyTemplate);

        // Increase vacancy Id so next one generated will be own a new Id
        vacancyId++;

        InlineKeyboardMarkup vacanciesListKeyboard = new InlineKeyboardMarkup();
        vacanciesListKeyboard.setKeyboard(List.of(vacanciesListButtons));

        return vacanciesListKeyboard;
    }

    // Inform user about unexpected error in system
    private void handleUnexpectedError(Update update) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setText("Unexpected error occurred! Please try again");
        sendMessage.setReplyMarkup(getStartMenu());

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    // Display welcome message & start menu for user
    private void executeStartCommand(Update update) {
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

    // Form start menu with main action buttons.
    private ReplyKeyboard getStartMenu() {
        List<InlineKeyboardButton> proficiencyLevelButtons = new ArrayList<>();

        InlineKeyboardButton juniorButton = new InlineKeyboardButton();
        juniorButton.setText(ProficiencyLevel.JUNIOR.name());
        juniorButton.setCallbackData("showJuniorVacancies");
        proficiencyLevelButtons.add(juniorButton);

        InlineKeyboardButton middleButton = new InlineKeyboardButton();
        middleButton.setText(ProficiencyLevel.MIDDLE.name());
        middleButton.setCallbackData("showMiddleVacancies");
        proficiencyLevelButtons.add(middleButton);

        InlineKeyboardButton seniorButton = new InlineKeyboardButton();
        seniorButton.setText(ProficiencyLevel.SENIOR.name());
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
