package org.sto.service.impl;

import jakarta.validation.Valid;
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
    private final BotConfig config;
    private final CarOrderRepository carOrderRepository;
    private static final String HELP_TEXT = "This bot is created to get the status of your car.\n\n" +
            "You can use any commands from the menu or type them manually.\n\n" +
            "Type /start for an initial dialogue with the bot\n\n" +
            "Type /mycar to get the status of your vehicle\n\n" +
            "Type /help to see this message again";

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

    public TelegramBot(@Valid BotConfig config, @Valid CarOrderRepository carOrderRepository, @Valid UserRepository userRepository) {
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

    private void startCommandReceived(final long chatId, final String name) {
        final String answer = "Hello, " + name + ", nice to see you at our car service station!";
        sendMessage(chatId, answer);
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private void sendMessage(final long chatId, final String textToSend) {
        final SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendContactRequest(final long chatId, final Message msg) {
        final SendMessage message = new SendMessage();
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
    public void onUpdateReceived(final Update update) {
        if (update.hasMessage()) {
            final long chatId = update.getMessage().getChatId();

            if (update.getMessage().hasText()) {
                final String messageText = update.getMessage().getText();

                switch (messageText) {
                    case "/start":
                        startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                        break;
                    case "/help":
                        sendMessage(chatId, HELP_TEXT);
                        break;
                    case "/mycar":
                        handleContact(update.getMessage());
                        break;
                    default:
                        sendMessage(chatId, "Sorry, the command was not recognized");
                }
            } else if (update.getMessage().getContact() != null) {
                sendContactRequest(chatId, update.getMessage());
            }
        }
    }

    private void registeredUser(final Message msg, final Optional<User> user) {
        if (user.isEmpty()) {
            final User userByPhoneNumber = userRepository.findByPhoneNumber(msg.getContact().getPhoneNumber()).orElseThrow();
            final var chatId = msg.getChatId();
            userByPhoneNumber.setChatId(chatId);
            userByPhoneNumber.setRegisteredAt(new Timestamp(System.currentTimeMillis()));
            userRepository.save(userByPhoneNumber);
        }
    }

    public void handleContact(final Message message) {
        final Contact contact = message.getContact();
        final long chatId = message.getChatId();
        final Optional<User> user = userRepository.findByChatId(chatId);
        if (contact != null && user.isEmpty()) {
            sendContactRequest(chatId, message);
            registeredUser(message, user);
        } else if (user.isPresent()) {
            getCarStatusMessage(chatId, user);
        } else {
            sendContactRequest(chatId, message);
        }
    }

    private void getCarStatusMessage(final long chatId, final Optional<User> userOptional) {
        try {
            if (userOptional.isPresent()) {
                final User user = userOptional.get();
                final CarOrder carOrder = carOrderRepository.findByUser(user).orElseThrow();
                final String message = (carOrder.getStatus() == Status.COMPLETED) ? "completed" : "in progress";
                sendMessage(chatId, "Service for your car is " + message);
            } else {
                sendMessage(chatId, "User not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(chatId, "An error occurred while processing your request.");
        }
    }
}