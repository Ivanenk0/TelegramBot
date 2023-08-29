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
        try {
            if (update.getMessage() != null) executeStartCommand(update);
            if (update.getCallbackQuery() != null) {
                String callbackData = update.getCallbackQuery().getData();

                if ("showJuniorVacancies".equals(callbackData)) {
                    this.executeShowJuniorVacanciesCommand(update);
                }
                if (callbackData.startsWith("vacancyId = ")) {
                    String id = callbackData.split("=")[1];
                    this.executeShowVacancyDescription(id, update);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't send message to user", e);
        }
    }

    private void executeShowVacancyDescription(String id, Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setText("Vacancy description template\nID : " + id);
        execute(sendMessage);
    }

    // Show vacancies list for Junior lvl by User request from start menu button
    private void executeShowJuniorVacanciesCommand(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Vacancies list :");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(getJuniorVacanciesList());
        execute(sendMessage);
    }

    // Load possible vacancies and form a list of it
    private ReplyKeyboard getJuniorVacanciesList() {
        List<InlineKeyboardButton> vacanciesListButtons = new ArrayList<>();

        InlineKeyboardButton vacancyTemplate = new InlineKeyboardButton();
        vacancyTemplate.setText("Template Button for Junior Developer Vacancy");
        vacancyTemplate.setCallbackData("vacancyId = 1");
        vacanciesListButtons.add(vacancyTemplate);

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
