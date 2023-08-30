package com.mateacademy.telegrambot;

import com.mateacademy.telegrambot.dto.VacancyDto;
import com.mateacademy.telegrambot.service.VacancyService;
import com.mateacademy.telegrambot.utils.ProficiencyLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class VacanciesBot extends TelegramLongPollingBot {

    @Autowired
    private VacancyService vacancyService;

    private final Map<Long, ProficiencyLevel> userNavigationHistory = new HashMap<>();

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

                if (callbackData.startsWith("vacancyId=")) {
                    String id = callbackData.split("=")[1];
                    this.showVacancyDescription(id, update);
                } else {
                    switch (callbackData) {
                        case "showJuniorVacancies" -> this.showVacancies(update, ProficiencyLevel.JUNIOR);
                        case "showMiddleVacancies" -> this.showVacancies(update, ProficiencyLevel.MIDDLE);
                        case "showSeniorVacancies" -> this.showVacancies(update, ProficiencyLevel.SENIOR);
                        case "backToVacancies" -> this.handlebackToVacanciesCommand(update);
                        case "backToStartMenu" -> this.handleBackToStartCommand(update);
                        default -> this.handleUnexpectedError(update);
                    }
            }
            }
        } catch (Exception e) {
            throw new RuntimeException("Can't send message to user", e);
        }
    }

    private void handleBackToStartCommand(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Choose title");
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        sendMessage.setReplyMarkup(getStartMenu());
        execute(sendMessage);
    }

    private void handlebackToVacanciesCommand(Update update) throws TelegramApiException {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        ProficiencyLevel level = userNavigationHistory.get(chatId);

        if (level.equals(ProficiencyLevel.JUNIOR)) {
            this.showVacancies(update, ProficiencyLevel.JUNIOR);
        } else if (level.equals(ProficiencyLevel.MIDDLE)) {
            this.showVacancies(update, ProficiencyLevel.MIDDLE);
        } else if (level.equals(ProficiencyLevel.SENIOR)) {
            this.showVacancies(update, ProficiencyLevel.SENIOR);
        }
    }

    // Show vacancies description by User request
    private void showVacancyDescription(String id, Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
        VacancyDto vacancy = vacancyService.getVacancy(id);
        sendMessage.setText(setupVacancyDescription(vacancy));
        sendMessage.setReplyMarkup(getBackToVacanciesMenu());
        execute(sendMessage);
    }

    private String setupVacancyDescription(VacancyDto vacancy) {
        return "Title : " + vacancy.getTitle() +
                "\nCompany : " + vacancy.getCompany() +
                "\nShort description : " + vacancy.getShortDescription() +
                "\nDescription : " + vacancy.getLongDescription() +
                "\nSalary : " + vacancy.showSalary(vacancy.getSalary()) +
                "\nLink : " + vacancy.getLink();
    }

    public ReplyKeyboard getBackToVacanciesMenu() {
        List<InlineKeyboardButton> actionButtons = new ArrayList<>();

        InlineKeyboardButton backToVacanciesButton = new InlineKeyboardButton();
        backToVacanciesButton.setText("Back to vacancies");
        backToVacanciesButton.setCallbackData("backToVacancies");
        actionButtons.add(backToVacanciesButton);

        InlineKeyboardButton backToStartMenu = new InlineKeyboardButton();
        backToStartMenu.setText("Back to start menu");
        backToStartMenu.setCallbackData("backToStartMenu");
        actionButtons.add(backToStartMenu);

        InlineKeyboardButton getChatGpt = new InlineKeyboardButton();
        getChatGpt.setText("Generate cover letter");
        getChatGpt.setUrl("https://chat.openai.com/");
        actionButtons.add(getChatGpt);

        return new InlineKeyboardMarkup(List.of(actionButtons));
    }

    // Show vacancies list for Junior lvl by User request from start menu button
    private void showVacancies(Update update, ProficiencyLevel level) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Vacancies list for " + level.toString() + " Developer :");
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(getVacanciesList(level));
        execute(sendMessage);

        userNavigationHistory.put(chatId, level);
    }

    // Load possible vacancies and form a list of it
    private ReplyKeyboard getVacanciesList(ProficiencyLevel level) {
        List<InlineKeyboardButton> vacanciesListButtons = new ArrayList<>();

        List<VacancyDto> vacancies = vacancyService.findVacancies(level);

        for (VacancyDto vacancy : vacancies) {
            InlineKeyboardButton vacancyButton = new InlineKeyboardButton();
            vacancyButton.setText(vacancy.getTitle());
            vacancyButton.setCallbackData("vacancyId=" + vacancy.getId());
            vacanciesListButtons.add(vacancyButton);
        }

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
