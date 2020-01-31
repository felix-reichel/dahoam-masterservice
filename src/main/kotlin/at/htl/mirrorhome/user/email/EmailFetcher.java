package at.htl.mirrorhome.user.email;

import at.htl.mirrorhome.user.calendar.CalendarFetcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.*;


public class EmailFetcher {

    private static Logger log = LoggerFactory.getLogger(EmailFetcher.class);


    public static List<Email> fetch(List<EmailAccount> accounts){
        List<Email> allEmails = new LinkedList<>();
        accounts.forEach(account -> {
            allEmails.addAll(fetchSingle(account));
        });
        return allEmails;
    }

    private static List<Email> fetchSingle(EmailAccount account){
        List<Email> emailsForAccount = new LinkedList<>();
        try{
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "pop3s");
            properties.put("mail.pop3.host", account.getHost());
            properties.put("mail.pop3.port", "995");
            properties.setProperty("mail.pop3s.auth", "true");
            properties.setProperty("mail.pop3s.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory" );
            properties.setProperty("mail.pop3s.ssl.trust", "*");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            // emailSession.setDebug(true);

            Store store = emailSession.getStore("pop3s");

            store.connect(account.getHost(), account.getUsername(), account.getPassword());

            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
            Message[] messages = emailFolder.getMessages();
            for(int i = 0; i < messages.length; i++){
                Email e = new Email();
                e.setSubject(messages[i].getSubject());
                if (messages[i].isMimeType("text/plain")) {
                    e.setBody((String) messages[i].getContent());
                }
                e.setSeen(messages[i].isSet(Flags.Flag.SEEN));
                e.setReceiveDate(messages[i].getReceivedDate());
                e.setFrom(EmailFetcher.accountArrayToList(messages[i].getFrom()));
                e.setTo(EmailFetcher.accountArrayToList(messages[i].getAllRecipients()));
                emailsForAccount.add(e);
            }
            emailFolder.close();
            store.close();
        }
        catch(Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return emailsForAccount;

    }

    private static List<String> accountArrayToList(Address[] accounts){
        LinkedList<String> accountList = new LinkedList<>();
        for(int i = 0; i < accounts.length; i++){
            accountList.add(accounts[i].toString());
        }
        return accountList;
    }
}
