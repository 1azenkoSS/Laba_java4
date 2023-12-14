package org.sto.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sto.config.BotConfig;
import org.sto.entity.CarOrder;
import org.sto.entity.User;
import org.sto.entity.enumerable.Status;
import org.sto.repository.CarOrderRepository;
import org.sto.repository.UserRepository;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private final UserRepository userRepository;
    //private final UserService userService;
    private final BotConfig config;
    private final CarOrderRepository carOrderRepository;
    private static final String HELP_TEXT = "Цей бот створений для отримки статусу готовності вашого автомобіля. \n\n" +
            "Ви можете використовувати будь-які команди з меню або писати їх вручну. \n\n" +
            "Напишіть /start для початкового діалогу з ботом\n\n" +
            "Напишіть /mycar для отримання статусу готовності вашого автомобіля\n\n" +
            "Напишіть /help щоб побачити це повідомлення знову";

    private ReplyKeyboardMarkup createContactButton() {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();

        markup.setKeyboard(new ArrayList<>());

        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardButton requestContactButton = new KeyboardButton("Share Contact");
        requestContactButton.setRequestContact(true);
        keyboardRow.add(requestContactButton);

        markup.getKeyboard().add(keyboardRow);
        markup.setOneTimeKeyboard(true);

        return markup;
    }


    public TelegramBot(BotConfig config, CarOrderRepository carOrderRepository, UserRepository userRepository) {
        this.config = config;
        this.carOrderRepository = carOrderRepository;
        this.userRepository = userRepository;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/mycar", "your vehicle's processing status"));
        listOfCommands.add(new BotCommand("/help", "how to use this bot"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.getMessage();
        }
    }

    private void StartCommandReceived(long chatId, String name) {
        String answer = "Привіт, " + name + ", раді бачити вас у нашому СТО!";

        sendMessage(chatId, answer);
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendContactRequest(long chatId, Message msg) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Please share your contact information:");
        message.setReplyMarkup(createContactButton());

        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();

            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();

                switch (messageText) {
                    case "/start":
                        StartCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        sendMessage(chatId, HELP_TEXT);
                        break;
                    case "/mycar":
                        handleContact(update.getMessage());
                        break;
                    default:
                        sendMessage(chatId, "Sorry, command was not recognized");
                }
            } else if (update.getMessage().getContact() != null) {
                sendContactRequest(chatId,update.getMessage());
            }
        }
    }

    private void registeredUser(final Message msg, final Optional<User> user) {
        if (user.isEmpty()) {

          User userByPhoneNumber = userRepository.findByPhoneNumber(msg.getContact().getPhoneNumber()).orElseThrow();

            var chatId = msg.getChatId();
            userByPhoneNumber.setChatId(chatId);
            userByPhoneNumber.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(userByPhoneNumber);
        }
    }

    public void handleContact(final Message message) {
        Contact contact = message.getContact();
        long chatId = message.getChatId();
        Optional<User> user = userRepository.findByChatId(chatId);
        if (contact != null && user.isEmpty()) {
            sendContactRequest(chatId,message);
            registeredUser(message, user);
        } else if (user.isPresent()) {
            getCarStatusMessage(chatId, user);
        } else {
            sendContactRequest(chatId, message);
        }
    }

    private void getCarStatusMessage(long chatId, Optional<User> userOptional) {
        try {
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                CarOrder carOrder = carOrderRepository.findByUser(user).orElseThrow();
                String message = (carOrder.getStatus() == Status.COMPLETED) ? "завершено" : "в процесі";
                sendMessage(chatId, "Обслуговування вашого автомобіля " + message);
            } else {
                sendMessage(chatId, "Користувача не знайдено.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "An error occurred while processing your request.");
        }
    }

}