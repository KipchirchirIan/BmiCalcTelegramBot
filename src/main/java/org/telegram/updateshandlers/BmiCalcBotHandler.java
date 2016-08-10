/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.telegram.updateshandlers;

import com.bitrev.structure.BmiCalculator;
import java.text.DecimalFormat;
import org.json.JSONObject;
import org.telegram.BotConfig;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.telegrambots.updateshandlers.SentCallback;

/**
 *
 * @author Ian Kipchirchir <potterke4@gmail.com>
 */
public class BmiCalcBotHandler extends TelegramLongPollingBot {
    
    private static final String LOGTAG = "BMICALCHANDLERS";
    
    private static final String UNDERWEIGHT = "Underweight";
    private static final String NORMAL = "Normal";
    private static final String OVERWEIGHT = "Overweight";
    private static final String OBESE = "Obese";
    
    private static boolean WAITING_HEIGHT_STATUS = true;
    private static boolean WAITING_WEIGHT_STATUS = true;
    
    BmiCalculator bmiCalc = new BmiCalculator();
  
    @Override
    public String getBotUsername() {
        return BotConfig.BMICALC_USER;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        try {
            handleBmiCalculations(update);
        } catch (Exception e) {
            BotLogger.error(LOGTAG, e);
        }
    } //end onUpdateReceived()
    
    @Override
    public String getBotToken() {
        return BotConfig.BMICALC_TOKEN;
    }
    
    private void handleBmiCalculations(Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasText()) {
            if (message.getText().startsWith("/start")) {
                onStartCommand(message);
                WAITING_HEIGHT_STATUS = true;
                WAITING_WEIGHT_STATUS = true;
            } else if (WAITING_HEIGHT_STATUS) {
                onHeightReceived(message);
            } else if (WAITING_WEIGHT_STATUS) {
                onWeightReceived(message);
            } else if (WAITING_HEIGHT_STATUS && WAITING_WEIGHT_STATUS) {
                SendMessage sendMessageRequest = new SendMessage();
                sendMessageRequest.setText("Please provide us with the required data.");
                sendMessageRequest.setChatId(message.getChatId().toString());
                try {
                    sendMessage(sendMessageRequest);
                } catch (TelegramApiException e) {
                    BotLogger.error(LOGTAG, e);
                }
            }
        }
    }
    
    private void onStartCommand(Message message) {
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(message.getChatId().toString());
        sendMessageRequest.setReplyToMessageId(message.getMessageId());
        sendMessageRequest.setText("Please reply to this message with your height in meters.");

        try {
            sendMessageAsync(sendMessageRequest, new SentCallback<Message>() {
                @Override
                public void onResult(BotApiMethod<Message> method, JSONObject jsonObject) {
                    Message sentMessage = method.deserializeResponse(jsonObject);
                    if (sentMessage != null) {
                        //do nothing here
                    }
                }

                @Override
                public void onError(BotApiMethod<Message> botApiMethod, JSONObject jsonObject) {
                }

                @Override
                public void onException(BotApiMethod<Message> botApiMethod, Exception e) {
                }
            });
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }

    }
    
    private void onHeightReceived(Message message) {
       SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(message.getChatId().toString());
        sendMessageRequest.setReplyToMessageId(message.getMessageId());
        sendMessageRequest.setText("Please reply to this message with your weight in kilograms.");

        try {
            sendMessageAsync(sendMessageRequest, new SentCallback<Message>() {
                @Override
                public void onResult(BotApiMethod<Message> method, JSONObject jsonObject) {
                    Message sentMessage = method.deserializeResponse(jsonObject);
                    if (sentMessage != null) {
                        double height = convertStringToDouble(message.getText());
                        bmiCalc.setHeight(height);
                        WAITING_HEIGHT_STATUS = false;
                    }
                }

                @Override
                public void onError(BotApiMethod<Message> botApiMethod, JSONObject jsonObject) {
                }

                @Override
                public void onException(BotApiMethod<Message> botApiMethod, Exception e) {
                }
            });
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
    
    private void onWeightReceived(Message message) {
        
        double weight = convertStringToDouble(message.getText());
        bmiCalc.setWeight(weight);
        WAITING_WEIGHT_STATUS = false;
        
        String responseToUser = performBmiCalculations();
        
        SendMessage sendMessageRequest = new SendMessage();
        sendMessageRequest.setChatId(message.getChatId().toString());
        sendMessageRequest.setReplyToMessageId(message.getMessageId());
        sendMessageRequest.setText(responseToUser);

        try {
            sendMessageAsync(sendMessageRequest, new SentCallback<Message>() {
                @Override
                public void onResult(BotApiMethod<Message> method, JSONObject jsonObject) {
                    Message sentMessage = method.deserializeResponse(jsonObject);
                    if (sentMessage != null) {
                        // do nothing
                    }
                }

                @Override
                public void onError(BotApiMethod<Message> botApiMethod, JSONObject jsonObject) {
                }

                @Override
                public void onException(BotApiMethod<Message> botApiMethod, Exception e) {
                }
            });
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        }
    }
    
    private String performBmiCalculations() {
        StringBuilder sb = new StringBuilder();
        String bmiCategory = "None";
        DecimalFormat df2 = new DecimalFormat(".##");
        
        double result = bmiCalc.getWeight() / (bmiCalc.getHeight() * bmiCalc.getHeight());
        
        if (result < 18.5)
            bmiCategory = UNDERWEIGHT;
        if (result >= 18.5 && result <= 24.9)
            bmiCategory = NORMAL;
        if (result >= 25.0 && result <= 29.9)
            bmiCategory = OVERWEIGHT;
        if (result >= 30.0)
            bmiCategory = OBESE;
        
        sb.append("Your BMI is: ");
        sb.append(String.valueOf(df2.format(result)));
        sb.append(".BMI category - ");
        sb.append(bmiCategory);
            
        return sb.toString();
    }

    private double convertStringToDouble(String updateStr) {
        double updateDbl = 0.0d;
        try {
            updateDbl = Double.parseDouble(updateStr);

        } catch (NumberFormatException nfe) {
            BotLogger.error(LOGTAG, nfe);
        }

        return updateDbl;
    }
}
