/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bitrev.bmicalctelegrambot;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.logging.BotLogger;
import org.telegram.updateshandlers.BmiCalcBotHandler;

/**
 *
 * @author Ian Kipchirchir <potterke4@gmail.com>
 */
public class Main {
    private static final String LOGTAG = "BMICALC";
    
    public static void main(String[] args) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        
        try {
            telegramBotsApi.registerBot(new BmiCalcBotHandler());
        } catch (TelegramApiException e) {
            BotLogger.error(LOGTAG, e);
        } //end catch()
    } // end main()
}
