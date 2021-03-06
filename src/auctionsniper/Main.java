package auctionsniper;

import auctionsniper.ui.MainWindow;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;

import javax.swing.*;

/**
 * Created by sean on 16/4/29.
 */
public class Main {
    public static final String SNIPER_STATUS_NAME = "sniper status";
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private Chat notToBeGCd;

    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        main.joinAuction(
            connectTo(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
            args[ARG_ITEM_ID]
        );
    }

    private void joinAuction(XMPPConnection connection, String itemId) throws XMPPException {
        final Chat chat = connection.getChatManager().createChat(
                auctionId(itemId, connection),
                new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        SwingUtilities.invokeLater(new Runnable() {
                           public void run() {
                               ui.showStatus(MainWindow.STATUS_LOST);
                           }
                        });
                    }
                }
        );
        this.notToBeGCd = chat;
        chat.sendMessage(new Message());
    }

    private static XMPPConnection connectTo(String hostname, String username, String password)
        throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }
    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable () {
            public void run() {
                ui = new MainWindow();
            }
        });
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }
}
